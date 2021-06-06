package org.thoms.xpenses.model;

import lombok.Builder;
import lombok.Getter;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.utils.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.thoms.xpenses.configuration.ActivityConfiguration.*;
import static org.thoms.xpenses.utils.DynamoDBUtils.getListAttribute;
import static org.thoms.xpenses.utils.DynamoDBUtils.getStringAttribute;

@Builder
@Getter
public class Activity {
    private final String id;
    private final String activityName;
    private final String createdBy;
    private final List<String> expenses;
    private final List<String> users;
    private final String startDate;
    private final ActionEnum activityStatus;

    public static Activity from(final Map<String, AttributeValue> response) {
        return CollectionUtils.isNullOrEmpty(response) ? null :
                Optional.of(response)
                        .map(r -> Activity.builder()
                                .id(getStringAttribute(r.get(ID)))
                                .activityName(getStringAttribute(r.get(ACTIVITY_NAME)))
                                .createdBy(getStringAttribute(r.get(CREATED_BY)))
                                .expenses(getListAttribute(r.get(EXPENSES)))
                                .users(getListAttribute(r.get(USERS_ATTRIBUTE)))
                                .startDate(getStringAttribute(r.get(START_DATE)))
                                .activityStatus(ActionEnum.valueOf(getStringAttribute(r.get(ACTIVITY_STATUS))))
                                .build()
                        )
                        .orElse(null);
    }

}
