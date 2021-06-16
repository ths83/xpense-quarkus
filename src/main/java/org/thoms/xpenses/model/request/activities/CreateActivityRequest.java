package org.thoms.xpenses.model.request.activities;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class CreateActivityRequest {
	private String name;
	private String createdBy;

	public CreateActivityRequest() {
		super();
	}

	public CreateActivityRequest(String name, String createdBy) {
		this.name = name;
		this.createdBy = createdBy;
	}

	public String getName() {
		return name;
	}

	public CreateActivityRequest setName(String name) {
		this.name = name;
		return this;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public CreateActivityRequest setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
		return this;
	}
}
