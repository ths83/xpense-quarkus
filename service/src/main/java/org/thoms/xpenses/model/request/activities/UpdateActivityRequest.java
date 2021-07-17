package org.thoms.xpenses.model.request.activities;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class UpdateActivityRequest {
	private String name;
	private String date;

	public UpdateActivityRequest() {
		super();
	}

	public UpdateActivityRequest(String name, String date) {
		this.name = name;
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public UpdateActivityRequest setName(String name) {
		this.name = name;
		return this;
	}

	public String getDate() {
		return date;
	}

	public UpdateActivityRequest setDate(String date) {
		this.date = date;
		return this;
	}
}
