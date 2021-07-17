package org.thoms.xpenses.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public enum ActionEnum {

	IN_PROGRESS("IN_PROGRESS"),

	DONE("DONE");

	private final String value;

	ActionEnum(final String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
