package com.joutvhu.model.tester.domain;

import java.util.Date;
import java.util.Objects;

public class ModelA {
    private String a;
    private Integer b;
    private int c;
    private Date d;

    public ModelA() {
    }

    public ModelA(String a, Integer b, int c, Date d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public Integer getB() {
        return b;
    }

    public void setB(Integer b) {
        this.b = b;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public Date getD() {
        return d;
    }

    public void setD(Date d) {
        this.d = d;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelA modelA = (ModelA) o;
        return c == modelA.c && Objects.equals(a, modelA.a) && Objects.equals(b, modelA.b) && Objects.equals(d, modelA.d);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, c, d);
    }

    @Override
    public String toString() {
        return "ModelA{" +
                "a='" + a + '\'' +
                ", b=" + b +
                ", c=" + c +
                ", d=" + d +
                '}';
    }
}
