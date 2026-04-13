package com.joutvhu.model.tester.domain;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractMapModel {
    private String[] options;
    private Map<Integer, SimplePojo> itemMap;
    private Character symbol;
    private Byte errorCode;

    public AbstractMapModel() {
    }

    public AbstractMapModel(String[] options, Map<Integer, SimplePojo> itemMap, Character symbol, Byte errorCode) {
        this.options = options;
        this.itemMap = itemMap;
        this.symbol = symbol;
        this.errorCode = errorCode;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public Map<Integer, SimplePojo> getItemMap() {
        return itemMap;
    }

    public void setItemMap(Map<Integer, SimplePojo> itemMap) {
        this.itemMap = itemMap;
    }

    public Character getSymbol() {
        return symbol;
    }

    public void setSymbol(Character symbol) {
        this.symbol = symbol;
    }

    public Byte getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Byte errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractMapModel that = (AbstractMapModel) o;

        if (!Arrays.equals(options, that.options)) return false;
        if (!Objects.equals(itemMap, that.itemMap)) return false;
        if (!Objects.equals(symbol, that.symbol)) return false;
        return Objects.equals(errorCode, that.errorCode);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(options);
        result = 31 * result + Objects.hash(itemMap, symbol, errorCode);
        return result;
    }

    @Override
    public String toString() {
        return "AbstractMapModel{" +
            "options=" + Arrays.toString(options) +
            ", itemMap=" + itemMap +
            ", symbol=" + symbol +
            ", errorCode=" + errorCode +
            '}';
    }
}
