package com.joutvhu.model.tester;

class ToStringTester<T> implements Tester {
    private final Class<T> modelClass;
    private final boolean safe;

    ToStringTester(Class<T> modelClass) {
        this(modelClass, false);
    }

    ToStringTester(Class<T> modelClass, boolean safe) {
        this.modelClass = modelClass;
        this.safe = safe;
    }

    @Override
    public boolean test() {
        try {
            System.out.println("Start testing method toString()");
            T model = Creator.anyOf(modelClass).create();
            if (safe) {
                return Assert.assertEquals(model.toString(), model.toString());
            } else {
                T newModel = Creator.makeCopy(model);
                return Assert.assertEquals(model.toString(), newModel.toString());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }
}
