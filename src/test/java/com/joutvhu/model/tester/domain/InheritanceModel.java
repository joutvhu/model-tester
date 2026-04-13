package com.joutvhu.model.tester.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Objects;

public class InheritanceModel extends AbstractMapModel {
    private BigInteger count;
    private BigDecimal amount;

    public InheritanceModel() {
    }

    public InheritanceModel(BigInteger count, BigDecimal amount) {
        this.count = count;
        this.amount = amount;
    }

    public InheritanceModel(String[] options, Map<Integer, SimplePojo> itemMap, Character symbol, Byte errorCode) {
        super(options, itemMap, symbol, errorCode);
    }

    public InheritanceModel(String[] options, Map<Integer, SimplePojo> itemMap, Character symbol, Byte errorCode, BigInteger count, BigDecimal amount) {
        super(options, itemMap, symbol, errorCode);
        this.count = count;
        this.amount = amount;
    }

    public BigInteger getCount() {
        return count;
    }

    public void setCount(BigInteger count) {
        this.count = count;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        InheritanceModel that = (InheritanceModel) o;

        if (!Objects.equals(count, that.count)) return false;
        return Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), count, amount);
    }

    @Override
    public String toString() {
        return "InheritanceModel{" +
                "count=" + count +
                ", amount=" + amount +
                "} " + super.toString();
    }
}
