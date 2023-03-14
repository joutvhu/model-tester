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
            T model = Creator.anyOf(modelClass).create();
            boolean success = true;
            if (safe) {
                success = Assert.assertEquals(model.hashCode(), model.hashCode());
            } else {
                T newModel = Creator.makeCopy(model);
                success = Assert.assertEquals(model.hashCode(), newModel.hashCode());
            }
            if (success)
                System.out.println("Success: " + modelClass.getName() + ".hashCode()");
            else
                System.err.println("Failure: " + modelClass.getName() + ".hashCode()");
            return success;
        } catch (Throwable e) {
            System.err.println("Error: " + modelClass.getName() + ".hashCode()");
            e.printStackTrace();
        }
        return false;
    }
}
