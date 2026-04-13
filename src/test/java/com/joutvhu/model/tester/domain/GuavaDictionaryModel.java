package com.joutvhu.model.tester.domain;

import java.util.Dictionary;
import java.util.Objects;

public class GuavaDictionaryModel {
    private Dictionary properties;
    private NestedEnum state;

    public Dictionary getProperties() {
        return properties;
    }

    public void setProperties(Dictionary properties) {
        this.properties = properties;
    }

    public NestedEnum getState() {
        return state;
    }

    public void setState(NestedEnum state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuavaDictionaryModel that = (GuavaDictionaryModel) o;
        return Objects.equals(properties, that.properties) && state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties, state);
    }

    @Override
    public String toString() {
        return com.google.common.base.MoreObjects.toStringHelper(this)
            .add("properties", properties)
            .add("state", state)
            .toString();
    }

    public enum NestedEnum {
        ACTIVE, INACTIVE, PENDING;
    }
}
