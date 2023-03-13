package com.joutvhu.model.tester.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigInteger;

public class ModelE {
    private short a;
    private Byte b;
    private BigInteger e;

    public ModelE() {
    }

    public short getA() {
        return a;
    }

    public void setA(short a) {
        this.a = a;
    }

    public Byte getB() {
        return b;
    }

    public void setB(Byte b) {
        this.b = b;
    }

    public BigInteger getE() {
        return e;
    }

    public void setE(BigInteger e) {
        this.e = e;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ModelE modelE = (ModelE) o;

        return new EqualsBuilder().append(a, modelE.a).append(b, modelE.b).append(e, modelE.e).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(a).append(b).append(e).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("a", a)
                .append("b", b)
                .append("e", e)
                .toString();
    }
}
