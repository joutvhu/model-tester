package com.joutvhu.model.tester;

class ConstructorTester<T> implements Tester {
    private Creator<T> creatable;

    ConstructorTester(Creator<T> creatable) {
        this.creatable = creatable;
    }

    @Override
    public boolean test() {
        try {
            T result = creatable.create();
            Assert.assertNotNull(result);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }
}
