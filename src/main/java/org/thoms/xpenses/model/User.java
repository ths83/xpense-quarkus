package org.thoms.xpenses.model;

import lombok.Builder;
import lombok.Getter;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.utils.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Builder
@Getter
public class User {

    private final List<String> activities;

    private final String username;

    public static User from(final Map<String, AttributeValue> response) {

        final var username = "username";

        final var activities = "activities";

        return CollectionUtils.isNullOrEmpty(response) ? null :
                Optional.of(response)
                        .map(r -> new UserBuilder()
                                .username(Optional.ofNullable(r.get(username).s())
                                        .orElse(null))
                                .activities(Optional.ofNullable(r.get(activities).l())
                                        .orElse(List.of())
                                        .stream()
                                        .map(AttributeValue::s)
                                        .collect(Collectors.toList()))
                                .build()
                        )
                        .orElse(null);
    }

}
