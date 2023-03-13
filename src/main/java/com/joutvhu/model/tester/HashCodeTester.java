package com.joutvhu.model.tester;

class HashCodeTester<T> implements Tester {
    private Class<T> modelClass;

    HashCodeTester(Class<T> modelClass) {
        this.modelClass = modelClass;
    }

    @Override
    public boolean test() {
        try {
            T model = Creator.anyOf(modelClass).create();
            T newModel = Creator.makeCopy(model);
            return Assert.assertEquals(model.hashCode(), newModel.hashCode());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }
}
