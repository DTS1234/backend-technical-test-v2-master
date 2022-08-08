package com.tui.proof.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Order {
    ASC, DESC;

    @JsonCreator
    public static Order forValue(String value) {
        return valueOf(value);
    }

    @JsonValue
    public String toValue() {
        return toString();
    }

}
