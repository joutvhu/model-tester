package com.joutvhu.model.tester;

class EqualsTester<T> implements Tester {
    private Class<T> modelClass;

    EqualsTester(Class<T> modelClass) {
        this.modelClass = modelClass;
    }

    @Override
    public boolean test() {
        try {
            T model = Creator.anyOf(modelClass).create();
            T newModel = Creator.makeCopy(model);
            return Assert.assertEquals(model, newModel);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }
}
