package org.thoms.xpenses.services;

import lombok.extern.jbosslog.JBossLog;
import org.thoms.xpenses.model.User;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.utils.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.Map;
import java.util.Optional;

import static org.thoms.xpenses.configuration.UserConfiguration.*;
import static org.thoms.xpenses.configuration.UserConstants.USERS;

@JBossLog
@ApplicationScoped
public class UserService {

    @Inject
    DynamoDbClient dynamoDB;

    public User get(final String username) {
        if (StringUtils.isBlank(username)) {
            throw new BadRequestException("Username must not be blank");
        }

        final var request = GetItemRequest
                .builder()
                .tableName(USERS_TABLE)
                .key(Map.of(USERS_TABLE_KEY, AttributeValue.builder().s(username).build()))
                .build();

        return User.from(dynamoDB.getItem(request).item());
    }

    // TODO add to create activity endpoint
    public void addActivity(final String username, final String activityId) {
        validateParams(username, activityId);

        if (getUser(username).getActivities().contains(activityId))
            log.warnf("Activity '{}' already exists for user '{}'", activityId, username);

        else addActivityToUser(username, activityId);
    }

    private User getUser(String username) {
        return Optional
                .ofNullable(get(username))
                .orElseThrow(() -> new NotFoundException(String.format("User '%s' does not exist", username)));
    }

    private void validateParams(final String username, final String activityId) {
        if (StringUtils.isBlank(username)) {
            throw new BadRequestException("Username must not be blank");
        }
        if (StringUtils.isBlank(activityId)) {
            throw new BadRequestException("Activity id must not be blank");
        }
    }

    private void addActivityToUser(final String username, final String activityId) {
        UpdateItemRequest
                .builder()
                .tableName(USERS_TABLE)
                .key(Map.of(USERS_TABLE_KEY, AttributeValue.builder().s(username).build()))
                .attributeUpdates(Map.of(USERS_TABLE_ACTIVITIES,
                        AttributeValueUpdate
                                .builder()
                                .action(AttributeAction.ADD)
                                .value(AttributeValue.builder().s(activityId).build())
                                .build()))
                .build();

        log.infof("Successfully added activity '{}' to user '{}'", activityId, username);
    }

    // TODO add to activity : delete endpoint
    public void deleteActivity(final String activityId) {
        if (StringUtils.isBlank(activityId)) {
            throw new BadRequestException("Activity id must not be blank");
        }

        USERS.forEach(u -> {
            if (getUser(u).getActivities().contains(activityId)) deleteActivityToUser(u, activityId);
            else log.errorf("Activity '{}' not found for user '{}'", activityId, u);
        });
    }

    private void deleteActivityToUser(final String username, final String activityId) {
        UpdateItemRequest
                .builder()
                .tableName(USERS_TABLE)
                .key(Map.of(USERS_TABLE_KEY, AttributeValue.builder().s(username).build()))
                .attributeUpdates(Map.of(USERS_TABLE_ACTIVITIES,
                        AttributeValueUpdate
                                .builder()
                                .action(AttributeAction.DELETE)
                                .value(AttributeValue.builder().s(activityId).build())
                                .build()))
                .build();

        log.infof("Successfully deleted activity '{}' to user '{}'", activityId, username);
    }

}