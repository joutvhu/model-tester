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
            T model = Creator.anyOf(modelClass).create();
            boolean success = true;
            if (safe) {
                success = Assert.assertEquals(model.toString(), model.toString());
            } else {
                T newModel = Creator.makeCopy(model);
                success = Assert.assertEquals(model.toString(), newModel.toString());
            }
            if (success)
                System.out.println("Success: " + modelClass.getName() + ".toString()");
            else
                System.err.println("Failure: " + modelClass.getName() + ".toString()");
            return success;
        } catch (Throwable e) {
            System.err.println("Error: " + modelClass.getName() + ".toString()");
            e.printStackTrace();
        }
        return false;
    }
}
