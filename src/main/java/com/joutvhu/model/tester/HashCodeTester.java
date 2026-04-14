package com.joutvhu.model.tester;

import java.util.ArrayList;
import java.util.List;

/**
 * Tester implementation for verifying the {@link Object#hashCode()} contract.
 * Verifies that equal objects have equal hash codes and that hash codes are consistent.
 *
 * @param <T> the type of model being tested
 */
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

    /**
     * Verifies that equal objects have consistent hash codes.
     *
     * @return list of results for hash code consistency check.
     */
    @Override
    public List<TestResult> test() {
        List<TestResult> results = new ArrayList<>();
        try {
            T model = Creator.anyOf(modelClass).create();
            if (safe) {
                boolean pass = Assert.assertEquals(model.hashCode(), model.hashCode());
                results.add(TestResult.builder()
                    .className(modelClass.getName())
                    .component("hashCode(itself)")
                    .status(pass ? TestStatus.PASS : TestStatus.FAIL)
                    .build());
            } else {
                T newModel = Creator.makeCopy(model);
                boolean pass = Assert.assertEquals(model.hashCode(), newModel.hashCode());
                results.add(TestResult.builder()
                    .className(modelClass.getName())
                    .component("hashCode(copy)")
                    .status(pass ? TestStatus.PASS : TestStatus.FAIL)
                    .build());
            }
        } catch (Throwable e) {
            results.add(TestResult.builder()
                .className(modelClass.getName())
                .component("hashCode")
                .status(TestStatus.ERROR)
                .message(e.getMessage())
                .error(e)
                .build());
        }
        return results;
    }
}
