package org.thoms.xpenses.model.request.expenses;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class UpdateExpenseRequest {
	private String expenseName;
	private Float amount;
	private String currency;
	private String startDate;

	public UpdateExpenseRequest() {
		super();
	}

	public UpdateExpenseRequest(String expenseName,
	                            Float amount,
	                            String currency,
	                            String startDate) {
		this.expenseName = expenseName;
		this.amount = amount;
		this.currency = currency;
		this.startDate = startDate;
	}

	public String getExpenseName() {
		return expenseName;
	}

	public UpdateExpenseRequest setExpenseName(String expenseName) {
		this.expenseName = expenseName;
		return this;
	}

	public Float getAmount() {
		return amount;
	}

	public UpdateExpenseRequest setAmount(Float amount) {
		this.amount = amount;
		return this;
	}

	public String getCurrency() {
		return currency;
	}

	public UpdateExpenseRequest setCurrency(String currency) {
		this.currency = currency;
		return this;
	}

	public String getStartDate() {
		return startDate;
	}

	public UpdateExpenseRequest setStartDate(String startDate) {
		this.startDate = startDate;
		return this;
	}
}
