package org.thoms.xpenses.services;

import lombok.extern.jbosslog.JBossLog;
import org.thoms.xpenses.model.ActionEnum;
import org.thoms.xpenses.model.Activity;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.utils.CollectionUtils;
import software.amazon.awssdk.utils.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.thoms.xpenses.configuration.ActivityConfiguration.ACTIVITIES_TABLE;
import static org.thoms.xpenses.configuration.ActivityConfiguration.ACTIVITY_NAME;
import static org.thoms.xpenses.configuration.ActivityConfiguration.ACTIVITY_STATUS;
import static org.thoms.xpenses.configuration.ActivityConfiguration.CREATED_BY;
import static org.thoms.xpenses.configuration.ActivityConfiguration.EXPENSES;
import static org.thoms.xpenses.configuration.ActivityConfiguration.ID;
import static org.thoms.xpenses.configuration.ActivityConfiguration.START_DATE;
import static org.thoms.xpenses.configuration.ActivityConfiguration.USERS_ATTRIBUTE;
import static org.thoms.xpenses.configuration.UserConstants.USER_1;
import static org.thoms.xpenses.configuration.UserConstants.USER_2;
import static org.thoms.xpenses.utils.DynamoDBUtils.buildAttributeList;
import static org.thoms.xpenses.utils.DynamoDBUtils.buildAttributes;
import static org.thoms.xpenses.utils.DynamoDBUtils.buildStringAttribute;
import static org.thoms.xpenses.utils.DynamoDBUtils.buildStringAttributeUpdate;

@JBossLog
@ApplicationScoped
public class ActivityService {

	@Inject
	DynamoDbClient dynamoDB;

	@Inject
	UserService userService;

	@Inject
	ExpenseService expenseService;

	public Activity create(final String name, final String createdBy) {
		if (StringUtils.isBlank(name)) {
			throw new BadRequestException("Name must not be blank");
		}
		if (StringUtils.isBlank(createdBy)) {
			throw new BadRequestException("Created by must not be blank");
		}

		final var id = UUID.randomUUID().toString();

		final var activity = Map.of(
				ID, buildStringAttribute(id),
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

		addUsersToActivity(id);

		log.infof("Successfully created activity '%s'", id);

		return Activity.from(activity);
	}

	private void addUsersToActivity(final String activityId) {
		final var firstUser = userService.get(USER_1);
		final var secondUser = userService.get(USER_2);
		List.of(firstUser, secondUser).forEach(u ->
				userService.addActivity(u.getUsername(), activityId)
		);
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

		final var response = dynamoDB.getItem(request).item();

		if (CollectionUtils.isNullOrEmpty(response))
			throw new NotFoundException(String.format("Activity %s not found", activityId));

		log.infof("Successfully found activity '%s'", activityId);

		return Activity.from(response);
	}

	public List<Activity> getByUsername(final String username) {
		final var user = userService.get(username);

		final var request = BatchGetItemRequest
				.builder()
				.requestItems(Map.of(ACTIVITIES_TABLE, buildAttributes(user.getActivities())))
				.build();

		final var responses = dynamoDB.batchGetItem(request).responses();

		final var response = responses.get(ACTIVITIES_TABLE)
				.stream()
				.map(Activity::from)
				.collect(Collectors.toList());

		log.infof("Successfully found '%s' activity(ies)", response.size());

		return response;
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

		log.infof("Successfully updated activity '%s'", activityId);
	}

	public void delete(final String activityId) {
		expenseService.getByActivity(activityId)
				.forEach(id -> expenseService.delete(id));

		userService.deleteActivity(activityId);

		final var request = DeleteItemRequest
				.builder()
				.tableName(ACTIVITIES_TABLE)
				.key(Map.of(ID, AttributeValue.builder().s(activityId).build()))
				.build();

		dynamoDB.deleteItem(request);

		log.infof("Successfully deleted activity '%s'", activityId);
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

		log.infof("Successfully close activity '%s'", activityId);
	}

	private void validateActivityId(String activityId) {
		if (StringUtils.isBlank(activityId)) {
			throw new BadRequestException("Activity id must not be blank");
		}

		get(activityId);
	}
}
