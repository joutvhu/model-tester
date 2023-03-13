package com.joutvhu.model.tester.domain;

import java.util.List;

public class ModelB {
    private ModelA a;
    private Double b;
    private Long c;
    private float d;
    private List<String> x;

    public ModelB() {
    }

    public ModelB(ModelA a) {
        this.a = a;
    }

    public ModelA getA() {
        return a;
    }

    public void setA(ModelA a) {
        this.a = a;
    }

    public Double getB() {
        return b;
    }

    public void setB(Double b) {
        this.b = b;
    }

    public Long getC() {
        return c;
    }

    public void setC(Long c) {
        this.c = c;
    }

    public float getD() {
        return d;
    }

    public void setD(float d) {
        this.d = d;
    }

    public List<String> getX() {
        return x;
    }

    public void setX(List<String> x) {
        this.x = x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModelB modelB = (ModelB) o;

        if (Float.compare(modelB.d, d) != 0) return false;
        if (a != null ? !a.equals(modelB.a) : modelB.a != null) return false;
        if (b != null ? !b.equals(modelB.b) : modelB.b != null) return false;
        if (c != null ? !c.equals(modelB.c) : modelB.c != null) return false;
        return x != null ? x.equals(modelB.x) : modelB.x == null;
    }

    @Override
    public int hashCode() {
        int result = a != null ? a.hashCode() : 0;
        result = 31 * result + (b != null ? b.hashCode() : 0);
        result = 31 * result + (c != null ? c.hashCode() : 0);
        result = 31 * result + (d != +0.0f ? Float.floatToIntBits(d) : 0);
        result = 31 * result + (x != null ? x.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ModelB{" +
                "a=" + a +
                ", b=" + b +
                ", c=" + c +
                ", d=" + d +
                ", x=" + x +
                '}';
    }
}
