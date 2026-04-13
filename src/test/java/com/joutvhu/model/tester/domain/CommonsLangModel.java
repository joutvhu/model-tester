package com.joutvhu.model.tester.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigInteger;

public class CommonsLangModel {
    private short code;
    private Byte type;
    private BigInteger value;
    private SampleEnum status;

    public CommonsLangModel() {
    }

    public short getCode() {
        return code;
    }

    public void setCode(short code) {
        this.code = code;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public BigInteger getValue() {
        return value;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public SampleEnum getStatus() {
        return status;
    }

    public void setStatus(SampleEnum status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommonsLangModel that = (CommonsLangModel) o;

        return new EqualsBuilder()
                .append(code, that.code)
                .append(type, that.type)
                .append(value, that.value)
                .append(status, that.status)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(code)
                .append(type)
                .append(value)
                .append(status)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("code", code)
                .append("type", type)
                .append("value", value)
                .append("status", status)
                .toString();
    }
}
