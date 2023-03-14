package com.joutvhu.model.tester;

class HashCodeTester<T> implements Tester {
    private final Class<T> modelClass;
    private final boolean safe;

    HashCodeTester(Class<T> modelClass) {
        this(modelClass, false);
    }

    HashCodeTester(Class<T> modelClass, boolean safe) {
        this.modelClass = modelClass;
        this.safe = safe;
    }

    @Override
    public boolean test() {
        try {
            System.out.println("Start testing method hashCode()");
            T model = Creator.anyOf(modelClass).create();
            if (safe) {
                return Assert.assertEquals(model.hashCode(), model.hashCode());
            } else {
                T newModel = Creator.makeCopy(model);
                return Assert.assertEquals(model.hashCode(), newModel.hashCode());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }
}
