package org.thoms.xpenses.configuration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ActivityConfiguration {
    public static final String ACTIVITIES_TABLE = "dev.activities";
    public static final String ID = "id";
    public static final String ACTIVITY_NAME = "activityName";
    public static final String CREATED_BY = "createdBy";
    public static final String EXPENSES = "expenses";
    public static final String USERS_ATTRIBUTE = "users";
    public static final String START_DATE = "startDate";
    public static final String ACTIVITY_STATUS = "activityStatus";
}
