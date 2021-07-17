package org.thoms.xpenses.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.utils.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.thoms.xpenses.configuration.UserConfiguration.ACTIVITIES;
import static org.thoms.xpenses.configuration.UserConfiguration.USERNAME;
import static org.thoms.xpenses.utils.DynamoDBUtils.getListAttribute;
import static org.thoms.xpenses.utils.DynamoDBUtils.getStringAttribute;

@RegisterForReflection
public class User {

	private List<String> activities;

	private String username;

	public User() {
		super();
	}

	public User(List<String> activities, String username) {
		this.activities = activities;
		this.username = username;
	}

	public static User from(final Map<String, AttributeValue> response) {
		return CollectionUtils.isNullOrEmpty(response) ? null :
				Optional.of(response)
						.map(r -> new User(
								getListAttribute(response.get(ACTIVITIES)),
								getStringAttribute(response.get(USERNAME)))
						)
						.orElse(null);
	}

	public List<String> getActivities() {
		return activities;
	}

	public User setActivities(List<String> activities) {
		this.activities = activities;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public User setUsername(String username) {
		this.username = username;
		return this;
	}
}
