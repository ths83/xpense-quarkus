package org.thoms.xpenses.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.utils.CollectionUtils;

import java.util.Map;
import java.util.Optional;

import static org.thoms.xpenses.configuration.ExpenseConfiguration.ACTIVITY_ID;
import static org.thoms.xpenses.configuration.ExpenseConfiguration.AMOUNT;
import static org.thoms.xpenses.configuration.ExpenseConfiguration.CURRENCY;
import static org.thoms.xpenses.configuration.ExpenseConfiguration.EXPENSE_NAME;
import static org.thoms.xpenses.configuration.ExpenseConfiguration.ID;
import static org.thoms.xpenses.configuration.ExpenseConfiguration.START_DATE;
import static org.thoms.xpenses.configuration.ExpenseConfiguration.USER;
import static org.thoms.xpenses.utils.DynamoDBUtils.getNumberAttribute;
import static org.thoms.xpenses.utils.DynamoDBUtils.getStringAttribute;

@RegisterForReflection
public class Expense {
	private String id;
	private String expenseName;
	private Float amount;
	private String currency;
	private String user;
	private String startDate;
	private String activityId;

	public Expense() {
		super();
	}

	public Expense(String id,
	               String expenseName,
	               Float amount,
	               String currency,
	               String user,
	               String startDate,
	               String activityId) {
		this.id = id;
		this.expenseName = expenseName;
		this.amount = amount;
		this.currency = currency;
		this.user = user;
		this.startDate = startDate;
		this.activityId = activityId;
	}

	public static Expense from(final Map<String, AttributeValue> response) {
		return CollectionUtils.isNullOrEmpty(response) ? null :
				Optional.of(response)
						.map(r -> new Expense(
								getStringAttribute(r.get(ID)),
								getStringAttribute(r.get(EXPENSE_NAME)),
								getNumberAttribute(r.get(AMOUNT)),
								getStringAttribute(r.get(USER)),
								getStringAttribute(r.get(CURRENCY)),
								getStringAttribute(r.get(START_DATE)),
								getStringAttribute(r.get(ACTIVITY_ID)))
						)
						.orElse(null);
	}

	public String getId() {
		return id;
	}

	public Expense setId(String id) {
		this.id = id;
		return this;
	}

	public String getExpenseName() {
		return expenseName;
	}

	public Expense setExpenseName(String expenseName) {
		this.expenseName = expenseName;
		return this;
	}

	public Float getAmount() {
		return amount;
	}

	public Expense setAmount(Float amount) {
		this.amount = amount;
		return this;
	}

	public String getCurrency() {
		return currency;
	}

	public Expense setCurrency(String currency) {
		this.currency = currency;
		return this;
	}

	public String getUser() {
		return user;
	}

	public Expense setUser(String user) {
		this.user = user;
		return this;
	}

	public String getStartDate() {
		return startDate;
	}

	public Expense setStartDate(String startDate) {
		this.startDate = startDate;
		return this;
	}

	public String getActivityId() {
		return activityId;
	}

	public Expense setActivityId(String activityId) {
		this.activityId = activityId;
		return this;
	}
}
