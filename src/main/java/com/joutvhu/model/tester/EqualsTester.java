package com.joutvhu.model.tester;

class EqualsTester<T> implements Tester {
    private final Class<T> modelClass;
    private final boolean safe;

    EqualsTester(Class<T> modelClass) {
        this(modelClass, false);
    }

    EqualsTester(Class<T> modelClass, boolean safe) {
        this.modelClass = modelClass;
        this.safe = safe;
    }

    @Override
    public boolean test() {
        try {
            T model = Creator.anyOf(modelClass).create();
            if (safe) {
                return Assert.assertEquals(model, model);
            } else {
                T newModel = Creator.makeCopy(model);
                return Assert.assertEquals(model, newModel);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }
}
