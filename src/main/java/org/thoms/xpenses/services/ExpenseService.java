package org.thoms.xpenses.services;

import lombok.extern.jbosslog.JBossLog;
import org.thoms.xpenses.configuration.ActivityConfiguration;
import org.thoms.xpenses.model.Expense;
import org.thoms.xpenses.model.request.expenses.UpdateExpenseRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.utils.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.thoms.xpenses.configuration.ActivityConfiguration.ACTIVITIES_TABLE;
import static org.thoms.xpenses.configuration.ExpenseConfiguration.ACTIVITY_ID;
import static org.thoms.xpenses.configuration.ExpenseConfiguration.AMOUNT;
import static org.thoms.xpenses.configuration.ExpenseConfiguration.CAD;
import static org.thoms.xpenses.configuration.ExpenseConfiguration.CURRENCY;
import static org.thoms.xpenses.configuration.ExpenseConfiguration.EXPENSES_TABLE;
import static org.thoms.xpenses.configuration.ExpenseConfiguration.EXPENSE_NAME;
import static org.thoms.xpenses.configuration.ExpenseConfiguration.ID;
import static org.thoms.xpenses.configuration.ExpenseConfiguration.START_DATE;
import static org.thoms.xpenses.configuration.ExpenseConfiguration.USER;
import static org.thoms.xpenses.utils.DynamoDBUtils.buildAttributes;
import static org.thoms.xpenses.utils.DynamoDBUtils.buildNumberAttribute;
import static org.thoms.xpenses.utils.DynamoDBUtils.buildNumberAttributeUpdate;
import static org.thoms.xpenses.utils.DynamoDBUtils.buildStringAttribute;
import static org.thoms.xpenses.utils.DynamoDBUtils.buildStringAttributeUpdate;

@JBossLog
@ApplicationScoped
public class ExpenseService {

    @Inject
    DynamoDbClient dynamoDB;

    @Inject
    ActivityService activityService;

    public Expense create(final Expense request) {
        final var id = UUID.randomUUID().toString();

        final var expense = Map.of(
                ID, buildStringAttribute(id),
                EXPENSE_NAME, buildStringAttribute(request.getExpenseName()),
                USER, buildStringAttribute(request.getUser()),
                AMOUNT, buildNumberAttribute(request.getAmount()),
                CURRENCY, buildStringAttribute(CAD),
                START_DATE, buildStringAttribute(LocalDateTime.now().toString()),
                ACTIVITY_ID, buildStringAttribute(request.getActivityId())
        );

        dynamoDB.putItem(
                PutItemRequest
                        .builder()
                        .tableName(EXPENSES_TABLE)
                        .item(expense)
                        .build());

        activityService.addExpense(request.getActivityId(), id);

        log.infof("Successfully added expense '%s' to activity '%s'", id, request.getActivityId());

        return Expense.from(expense);
    }

    public Expense get(final String id) {
        if (StringUtils.isBlank(id)) {
            throw new BadRequestException("Expense id must not be blank");
        }

        final var request = GetItemRequest
                .builder()
                .tableName(EXPENSES_TABLE)
                .key(Map.of(ID, buildStringAttribute(id)))
                .build();

        return Expense.from(dynamoDB.getItem(request).item());
    }

    public List<Expense> getByActivity(final String activityId) {
        final var activity = activityService.get(activityId);

        final var request = BatchGetItemRequest
                .builder()
                .requestItems(Map.of(ACTIVITIES_TABLE, buildAttributes(activity.getExpenses())))
                .build();

        final var responses = dynamoDB.batchGetItem(request).responses();

        return responses.get(EXPENSES_TABLE)
                .stream()
                .map(Expense::from)
                .collect(Collectors.toList());
    }

    public void update(final String id, final UpdateExpenseRequest request) {
        get(id);

        final var itemRequest = UpdateItemRequest
                .builder()
                .tableName(EXPENSES_TABLE)
                .key(Map.of(ID, AttributeValue.builder().s(id).build()))
                .attributeUpdates(Map.of(
                        EXPENSE_NAME, buildStringAttributeUpdate(request.getExpenseName()),
                        START_DATE, buildStringAttributeUpdate(request.getStartDate()),
                        CURRENCY, buildStringAttributeUpdate(request.getCurrency()),
                        AMOUNT, buildNumberAttributeUpdate(request.getAmount())))
                .build();

        dynamoDB.updateItem(itemRequest);

        log.infof("Successfully updated expense '%s'", id);
    }

    public void delete(final String activityId, final String id) {
        get(id);

        activityService.deleteExpense(activityId, id);

        final var request = DeleteItemRequest
                .builder()
                .tableName(EXPENSES_TABLE)
                .key(Map.of(ActivityConfiguration.ID, AttributeValue.builder().s(activityId).build()))
                .build();

        dynamoDB.deleteItem(request);

        log.infof("Successfully deleted expense '%s' from activity '%s'", id, activityId);
    }
}
