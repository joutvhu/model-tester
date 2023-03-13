package com.joutvhu.model.tester.domain;

import java.util.Dictionary;
import java.util.Objects;

public class ModelF {
    private Dictionary d;
    private EnumE e;

    public Dictionary getD() {
        return d;
    }

    public void setD(Dictionary d) {
        this.d = d;
    }

    public EnumE getE() {
        return e;
    }

    public void setE(EnumE e) {
        this.e = e;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelF modelF = (ModelF) o;
        return Objects.equals(d, modelF.d) && e == modelF.e;
    }

    @Override
    public int hashCode() {
        return Objects.hash(d, e);
    }

    @Override
    public String toString() {
        return com.google.common.base.MoreObjects.toStringHelper(this)
                .add("d", d)
                .add("e", e)
                .toString();
    }

    public static enum EnumE {
        A, B, C;
    }
}
