package com.joutvhu.model.tester.domain;

import java.util.Date;
import java.util.Objects;

public class SimplePojo {
    private String name;
    private Integer age;
    private int id;
    private Date birthday;

    public SimplePojo() {
    }

    public SimplePojo(String name, Integer age, int id, Date birthday) {
        this.name = name;
        this.age = age;
        this.id = id;
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimplePojo that = (SimplePojo) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(age, that.age) && Objects.equals(birthday, that.birthday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, id, birthday);
    }

    @Override
    public String toString() {
        return "SimplePojo{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", id=" + id +
                ", birthday=" + birthday +
                '}';
    }
}
