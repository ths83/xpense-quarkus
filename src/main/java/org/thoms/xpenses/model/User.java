package org.thoms.xpenses.model;

import lombok.Builder;
import lombok.Getter;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.utils.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.thoms.xpenses.configuration.UserConfiguration.ACTIVITIES;
import static org.thoms.xpenses.configuration.UserConfiguration.USERNAME;
import static org.thoms.xpenses.utils.DynamoDBUtils.getListAttribute;
import static org.thoms.xpenses.utils.DynamoDBUtils.getStringAttribute;

@Builder
@Getter
public class User {

    private final List<String> activities;

    private final String username;

    public static User from(final Map<String, AttributeValue> response) {
        return CollectionUtils.isNullOrEmpty(response) ? null :
                Optional.of(response)
                        .map(r -> new UserBuilder()
                                .username(getStringAttribute(response.get(USERNAME)))
                                .activities(getListAttribute(response.get(ACTIVITIES)))
                                .build()
                        )
                        .orElse(null);
    }

}
