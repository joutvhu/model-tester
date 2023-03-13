package com.joutvhu.model.tester.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

public class ModelD extends ModelC {
    private BigInteger e;
    private BigDecimal f;

    public ModelD(BigInteger e, BigDecimal f) {
        this.e = e;
        this.f = f;
    }

    public ModelD(String[] a, Map<Integer, ModelA> b, Character c, Byte d) {
        super(a, b, c, d);
    }

    public ModelD(String[] a, Map<Integer, ModelA> b, Character c, Byte d, BigInteger e, BigDecimal f) {
        super(a, b, c, d);
        this.e = e;
        this.f = f;
    }

    public BigInteger getE() {
        return e;
    }

    public void setE(BigInteger e) {
        this.e = e;
    }

    public BigDecimal getF() {
        return f;
    }

    public void setF(BigDecimal f) {
        this.f = f;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ModelD modelD = (ModelD) o;

        if (e != null ? !e.equals(modelD.e) : modelD.e != null) return false;
        return f != null ? f.equals(modelD.f) : modelD.f == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (e != null ? e.hashCode() : 0);
        result = 31 * result + (f != null ? f.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ModelD{" +
                "e=" + e +
                ", f=" + f +
                "} " + super.toString();
    }
}
