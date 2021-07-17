package org.thoms.xpenses.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.utils.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.thoms.xpenses.configuration.ActivityConfiguration.ACTIVITY_NAME;
import static org.thoms.xpenses.configuration.ActivityConfiguration.ACTIVITY_STATUS;
import static org.thoms.xpenses.configuration.ActivityConfiguration.CREATED_BY;
import static org.thoms.xpenses.configuration.ActivityConfiguration.EXPENSES;
import static org.thoms.xpenses.configuration.ActivityConfiguration.ID;
import static org.thoms.xpenses.configuration.ActivityConfiguration.START_DATE;
import static org.thoms.xpenses.configuration.ActivityConfiguration.USERS_ATTRIBUTE;
import static org.thoms.xpenses.utils.DynamoDBUtils.getListAttribute;
import static org.thoms.xpenses.utils.DynamoDBUtils.getStringAttribute;

@RegisterForReflection
public class Activity {
	private String id;
	private String activityName;
	private String createdBy;
	private List<String> expenses;
	private List<String> users;
	private String startDate;
	private ActionEnum activityStatus;

	public Activity() {
		super();
	}

	public Activity(String id,
	                String activityName,
	                String createdBy,
	                List<String> expenses,
	                List<String> users,
	                String startDate,
	                ActionEnum activityStatus) {
		this.id = id;
		this.activityName = activityName;
		this.createdBy = createdBy;
		this.expenses = expenses;
		this.users = users;
		this.startDate = startDate;
		this.activityStatus = activityStatus;
	}

	public static Activity from(final Map<String, AttributeValue> response) {
		return CollectionUtils.isNullOrEmpty(response) ? null :
				Optional.of(response)
						.map(r -> new Activity(
								getStringAttribute(r.get(ID)),
								getStringAttribute(r.get(ACTIVITY_NAME)),
								getStringAttribute(r.get(CREATED_BY)),
								getListAttribute(r.get(EXPENSES)),
								getListAttribute(r.get(USERS_ATTRIBUTE)),
								getStringAttribute(r.get(START_DATE)),
								ActionEnum.valueOf(getStringAttribute(r.get(ACTIVITY_STATUS)))))
						.orElse(null);
	}

	public String getId() {
		return id;
	}

	public Activity setId(String id) {
		this.id = id;
		return this;
	}

	public String getActivityName() {
		return activityName;
	}

	public Activity setActivityName(String activityName) {
		this.activityName = activityName;
		return this;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public Activity setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
		return this;
	}

	public List<String> getExpenses() {
		return expenses;
	}

	public Activity setExpenses(List<String> expenses) {
		this.expenses = expenses;
		return this;
	}

	public List<String> getUsers() {
		return users;
	}

	public Activity setUsers(List<String> users) {
		this.users = users;
		return this;
	}

	public String getStartDate() {
		return startDate;
	}

	public Activity setStartDate(String startDate) {
		this.startDate = startDate;
		return this;
	}

	public ActionEnum getActivityStatus() {
		return activityStatus;
	}

	public Activity setActivityStatus(ActionEnum activityStatus) {
		this.activityStatus = activityStatus;
		return this;
	}
}
