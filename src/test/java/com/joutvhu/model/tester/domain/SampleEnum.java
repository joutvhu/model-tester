package com.joutvhu.model.tester.domain;

import java.util.StringJoiner;

public enum SampleEnum {
    TEST0(0, "Test 0"),
    TEST1(1, "Test 1"),
    TEST2(2, "Test 2");

    private int id;
    private String label;

    SampleEnum(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SampleEnum.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("label='" + label + "'")
                .toString();
    }
}
