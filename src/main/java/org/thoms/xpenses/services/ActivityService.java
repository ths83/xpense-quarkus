package org.thoms.xpenses.services;

import lombok.extern.jbosslog.JBossLog;
import org.thoms.xpenses.model.ActionEnum;
import org.thoms.xpenses.model.Activity;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.utils.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.thoms.xpenses.configuration.ActivityConfiguration.*;
import static org.thoms.xpenses.configuration.UserConstants.USER_1;
import static org.thoms.xpenses.configuration.UserConstants.USER_2;
import static org.thoms.xpenses.utils.DynamoDBUtils.*;

@JBossLog
@ApplicationScoped
public class ActivityService {

    @Inject
    DynamoDbClient dynamoDB;

    @Inject
    UserService userService;

    public Activity create(final String name, final String createdBy) {
        if (StringUtils.isBlank(name)) {
            throw new BadRequestException("Name must not be blank");
        }
        if (StringUtils.isBlank(createdBy)) {
            throw new BadRequestException("Created by must not be blank");
        }

        final var activity = Map.of(
                ID, buildStringAttribute(UUID.randomUUID().toString()),
                ACTIVITY_NAME, buildStringAttribute(name),
                CREATED_BY, buildStringAttribute(createdBy),
                EXPENSES, buildAttributeList(List.of()),
                USERS_ATTRIBUTE, buildAttributeList(List.of(buildStringAttribute(USER_1), buildStringAttribute(USER_2))),
                START_DATE, buildStringAttribute(LocalDateTime.now().toString()),
                ACTIVITY_STATUS, buildStringAttribute(ActionEnum.IN_PROGRESS.toString())
        );

        dynamoDB.putItem(
                PutItemRequest
                        .builder()
                        .tableName(ACTIVITIES_TABLE)
                        .item(activity)
                        .build());

        return Activity.from(activity);
    }

    public Activity get(final String activityId) {
        if (StringUtils.isBlank(activityId)) {
            throw new BadRequestException("Activity id must not be blank");
        }

        final var request = GetItemRequest
                .builder()
                .tableName(ACTIVITIES_TABLE)
                .key(Map.of(ID, buildStringAttribute(activityId)))
                .build();

        return Activity.from(dynamoDB.getItem(request).item());
    }

    public List<Activity> getByUsername(final String username) {
        final var user = userService.get(username);

        final var request = BatchGetItemRequest
                .builder()
                .requestItems(Map.of(ACTIVITIES_TABLE, buildAttributes(user.getActivities())))
                .build();

        final var responses = dynamoDB.batchGetItem(request).responses();

        return responses.get(ACTIVITIES_TABLE)
                .stream()
                .map(Activity::from)
                .collect(Collectors.toList());
    }

    private KeysAndAttributes buildAttributes(final List<String> activities) {
        return KeysAndAttributes.builder()
                .keys(activities
                        .stream()
                        .map(a -> Map.of(ID, buildStringAttribute(a)))
                        .collect(Collectors.toList()))
                .build();
    }

    // TODO add to create expense endpoint
    void addExpense(final String activityId, final String expenseId) {
        final var request = UpdateItemRequest
                .builder()
                .tableName(ACTIVITIES_TABLE)
                .key(Map.of(ID, AttributeValue.builder().s(activityId).build()))
                .attributeUpdates(Map.of(EXPENSES,
                        AttributeValueUpdate
                                .builder()
                                .action(AttributeAction.ADD)
                                .value(AttributeValue.builder().s(expenseId).build())
                                .build()))
                .build();

        dynamoDB.updateItem(request);

        addUsersToActivity(activityId);

        log.infof("Successfully added expense '{}' to activity '{}'", expenseId, activityId);
    }

    private void addUsersToActivity(final String activityId) {
        final var firstUser = userService.get(USER_1);
        final var secondUser = userService.get(USER_2);
        List.of(firstUser, secondUser).forEach(u ->
                userService.addActivity(u.getUsername(), activityId)
        );
    }

    public void update(final String activityId, final String name, final String date) {
        validateActivityId(activityId);

        if (StringUtils.isBlank(name)) {
            throw new BadRequestException("Name must not be blank");
        }
        if (StringUtils.isBlank(date)) {
            throw new BadRequestException("Date must not be blank");
        }

        final var request = UpdateItemRequest
                .builder()
                .tableName(ACTIVITIES_TABLE)
                .key(Map.of(ID, AttributeValue.builder().s(activityId).build()))
                .attributeUpdates(Map.of(
                        ACTIVITY_NAME, buildStringAttributeUpdate(name),
                        START_DATE, buildStringAttributeUpdate(date)))
                .build();

        dynamoDB.updateItem(request);

        log.infof("Successfully updated activity '{}'", activityId);
    }

    public void delete(final String activityId) {
        validateActivityId(activityId);

        userService.deleteActivity(activityId);

        // TODO add delete expense for each expense

        final var request = DeleteItemRequest
                .builder()
                .tableName(ACTIVITIES_TABLE)
                .key(Map.of(ID, AttributeValue.builder().s(activityId).build()))
                .build();

        dynamoDB.deleteItem(request);

        log.infof("Successfully deleted activity '{}'", activityId);
    }

    public void close(final String activityId) {
        validateActivityId(activityId);

        final var request = UpdateItemRequest
                .builder()
                .tableName(ACTIVITIES_TABLE)
                .key(Map.of(ID, AttributeValue.builder().s(activityId).build()))
                .attributeUpdates(Map.of(
                        ACTIVITY_STATUS, buildStringAttributeUpdate(ActionEnum.DONE.toString())))
                .build();

        dynamoDB.updateItem(request);

        log.infof("Successfully close activity '{}'", activityId);
    }

    private void validateActivityId(String activityId) {
        if (StringUtils.isBlank(activityId)) {
            throw new BadRequestException("Activity id must not be blank");
        }

        get(activityId);
    }
}
