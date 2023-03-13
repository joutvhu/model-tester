package com.joutvhu.model.tester.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.temporal.Temporal;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;

public class ModelS {
    private Set<String> a;
    private String b;
    private Queue<String> c;
    private Temporal t;
    private SortedSet<Integer> s;

    public ModelS(Set<String> a, String b, Queue<String> c, Temporal t, SortedSet<Integer> s) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.t = t;
        this.s = s;
    }

    public Set<String> getA() {
        return a;
    }

    public void setA(Set<String> a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public Queue<String> getC() {
        return c;
    }

    public void setC(Queue<String> c) {
        this.c = c;
    }

    public Temporal getT() {
        return t;
    }

    public void setT(Temporal t) {
        this.t = t;
    }

    public SortedSet<Integer> getS() {
        return s;
    }

    public void setS(SortedSet<Integer> s) {
        this.s = s;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
