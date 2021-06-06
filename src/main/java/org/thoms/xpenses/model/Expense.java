package org.thoms.xpenses.model;

import lombok.Builder;
import lombok.Getter;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.utils.CollectionUtils;

import java.util.Map;
import java.util.Optional;

import static org.thoms.xpenses.configuration.ExpenseConfiguration.ACTIVITY_ID;
import static org.thoms.xpenses.configuration.ExpenseConfiguration.AMOUNT;
import static org.thoms.xpenses.configuration.ExpenseConfiguration.CURRENCY;
import static org.thoms.xpenses.configuration.ExpenseConfiguration.EXPENSE_NAME;
import static org.thoms.xpenses.configuration.ExpenseConfiguration.ID;
import static org.thoms.xpenses.configuration.ExpenseConfiguration.START_DATE;
import static org.thoms.xpenses.configuration.ExpenseConfiguration.USER;
import static org.thoms.xpenses.utils.DynamoDBUtils.getNumberAttribute;
import static org.thoms.xpenses.utils.DynamoDBUtils.getStringAttribute;

@Builder
@Getter
public class Expense {
    private final String id;
    private final String expenseName;
    private final Float amount;
    private final String currency;
    private final String user;
    private final String startDate;
    private final String activityId;

    public static Expense from(final Map<String, AttributeValue> response) {
        return CollectionUtils.isNullOrEmpty(response) ? null :
                Optional.of(response)
                        .map(r -> Expense.builder()
                                .id(getStringAttribute(r.get(ID)))
                                .expenseName(getStringAttribute(r.get(EXPENSE_NAME)))
                                .user(getStringAttribute(r.get(
                                        USER)))
                                .amount(getNumberAttribute(r.get(AMOUNT)))
                                .currency(getStringAttribute(r.get(CURRENCY)))
                                .startDate(getStringAttribute(r.get(START_DATE)))
                                .activityId(getStringAttribute(r.get(ACTIVITY_ID)))
                                .build()
                        )
                        .orElse(null);
    }
}
