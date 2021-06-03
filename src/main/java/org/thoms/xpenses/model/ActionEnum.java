package org.thoms.xpenses.model;

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
