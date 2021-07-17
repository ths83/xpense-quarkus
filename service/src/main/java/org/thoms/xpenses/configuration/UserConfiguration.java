package org.thoms.xpenses.configuration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserConfiguration {
    public static final String USERS_TABLE = "dev.users";
    public static final String USERNAME = "username";
    public static final String ACTIVITIES = "activities";
}
