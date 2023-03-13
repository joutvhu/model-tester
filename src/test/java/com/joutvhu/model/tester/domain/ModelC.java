package com.joutvhu.model.tester.domain;

import java.util.Arrays;
import java.util.Map;

public abstract class ModelC {
    private String[] a;
    private Map<Integer, ModelA> b;
    private Character c;
    private Byte d;

    public ModelC() {
    }

    public ModelC(String[] a, Map<Integer, ModelA> b, Character c, Byte d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public String[] getA() {
        return a;
    }

    public void setA(String[] a) {
        this.a = a;
    }

    public Map<Integer, ModelA> getB() {
        return b;
    }

    public void setB(Map<Integer, ModelA> b) {
        this.b = b;
    }

    public Character getC() {
        return c;
    }

    public void setC(Character c) {
        this.c = c;
    }

    public Byte getD() {
        return d;
    }

    public void setD(Byte d) {
        this.d = d;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModelC modelC = (ModelC) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(a, modelC.a)) return false;
        if (b != null ? !b.equals(modelC.b) : modelC.b != null) return false;
        if (c != null ? !c.equals(modelC.c) : modelC.c != null) return false;
        return d != null ? d.equals(modelC.d) : modelC.d == null;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(a);
        result = 31 * result + (b != null ? b.hashCode() : 0);
        result = 31 * result + (c != null ? c.hashCode() : 0);
        result = 31 * result + (d != null ? d.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ModelC{" +
                "a=" + Arrays.toString(a) +
                ", b=" + b +
                ", c=" + c +
                ", d=" + d +
                '}';
    }
}
