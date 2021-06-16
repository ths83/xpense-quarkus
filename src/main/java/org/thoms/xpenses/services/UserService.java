package org.thoms.xpenses.services;

import lombok.extern.jbosslog.JBossLog;
import org.thoms.xpenses.model.User;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.utils.CollectionUtils;
import software.amazon.awssdk.utils.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.Map;
import java.util.Optional;

import static org.thoms.xpenses.configuration.UserConfiguration.ACTIVITIES;
import static org.thoms.xpenses.configuration.UserConfiguration.USERNAME;
import static org.thoms.xpenses.configuration.UserConfiguration.USERS_TABLE;
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
				.key(Map.of(USERNAME, AttributeValue.builder().s(username).build()))
				.build();

		final var response = dynamoDB.getItem(request).item();

		if (CollectionUtils.isNullOrEmpty(response))
			throw new NotFoundException(String.format("Username '%s' not found", username));

		log.infof("Successfully found user '%s'", username);

		return User.from(response);
	}

	void addActivity(final String username, final String activityId) {
		validateParams(username, activityId);

		if (getUser(username).getActivities().contains(activityId))
			log.warnf("Activity '%s' already exists for user '%s'", activityId, username);

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
		final var request = UpdateItemRequest
				.builder()
				.tableName(USERS_TABLE)
				.key(Map.of(USERNAME, AttributeValue.builder().s(username).build()))
				.attributeUpdates(Map.of(ACTIVITIES,
						AttributeValueUpdate
								.builder()
								.action(AttributeAction.PUT)
								.value(AttributeValue.builder().s(activityId).build())
								.build()))
				.build();

		dynamoDB.updateItem(request);

		log.infof("Successfully added activity '%s' to user '%s'", activityId, username);
	}

	void deleteActivity(final String activityId) {
		if (StringUtils.isBlank(activityId)) {
			throw new BadRequestException("Activity id must not be blank");
		}

		USERS.forEach(u -> {
			if (getUser(u).getActivities().contains(activityId)) deleteActivityToUser(u, activityId);
			else log.errorf("Activity '%s' not found for user '%s'", activityId, u);
		});
	}

	private void deleteActivityToUser(final String username, final String activityId) {
		final var request = UpdateItemRequest
				.builder()
				.tableName(USERS_TABLE)
				.key(Map.of(USERNAME, AttributeValue.builder().s(username).build()))
				.attributeUpdates(Map.of(ACTIVITIES,
						AttributeValueUpdate
								.builder()
								.action(AttributeAction.DELETE)
								.value(AttributeValue.builder().s(activityId).build())
								.build()))
				.build();

		dynamoDB.updateItem(request);

		log.infof("Successfully deleted activity '%s' to user '%s'", activityId, username);
	}

}