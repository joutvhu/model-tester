package com.joutvhu.model.tester;

class ToStringTester<T> implements Tester {
    private Class<T> modelClass;

    ToStringTester(Class<T> modelClass) {
        this.modelClass = modelClass;
    }

    @Override
    public boolean test() {
        try {
            T model = Creator.anyOf(modelClass).create();
            T newModel = Creator.makeCopy(model);
            return Assert.assertEquals(model.toString(), newModel.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }
}
