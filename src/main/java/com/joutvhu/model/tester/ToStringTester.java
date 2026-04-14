package com.joutvhu.model.tester;

import java.util.ArrayList;
import java.util.List;

/**
 * Tester implementation for verifying the {@link Object#toString()} contract.
 * Verifies that equal objects have equal string representations and that the
 * method does not throw exceptions.
 *
 * @param <T> the type of model being tested
 */
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

    /**
     * Verifies that the toString method returns consistent results and doesn't crash.
     *
     * @return list of results for string representation check.
     */
    @Override
    public List<TestResult> test() {
        List<TestResult> results = new ArrayList<>();
        try {
            T model = Creator.anyOf(modelClass).create();
            if (safe) {
                boolean pass = Assert.assertEquals(model.toString(), model.toString());
                results.add(TestResult.builder()
                    .className(modelClass.getName())
                    .component("toString(itself)")
                    .status(pass ? TestStatus.PASS : TestStatus.FAIL)
                    .build());
            } else {
                T newModel = Creator.makeCopy(model);
                boolean pass = Assert.assertEquals(model.toString(), newModel.toString());
                results.add(TestResult.builder()
                    .className(modelClass.getName())
                    .component("toString(copy)")
                    .status(pass ? TestStatus.PASS : TestStatus.FAIL)
                    .build());
            }
        } catch (Throwable e) {
            results.add(TestResult.builder()
                .className(modelClass.getName())
                .component("toString")
                .status(TestStatus.ERROR)
                .message(e.getMessage())
                .error(e)
                .build());
        }
        return results;
    }
}
