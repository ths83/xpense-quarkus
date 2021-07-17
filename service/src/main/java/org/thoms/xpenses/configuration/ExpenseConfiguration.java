package org.thoms.xpenses.configuration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpenseConfiguration {
    public static final String EXPENSES_TABLE = "dev.expenses";
    public static final String ID = "id";
    public static final String EXPENSE_NAME = "expenseName";
    public static final String USER = "user";
    public static final String AMOUNT = "amount";
    public static final String START_DATE = "startDate";
    public static final String ACTIVITY_ID = "activityId";
    public static final String CURRENCY = "currency";
    public static final String CAD = "CAD";
}
