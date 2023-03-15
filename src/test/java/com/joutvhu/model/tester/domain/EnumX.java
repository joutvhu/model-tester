package com.joutvhu.model.tester.domain;

import java.util.StringJoiner;

public enum EnumX {
    TEST0(0, "Test 0"),
    TEST1(1, "Test 1"),
    TEST2(2, "Test 2");

    private int id;
    private String name;

    EnumX(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", EnumX.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .toString();
    }
}
