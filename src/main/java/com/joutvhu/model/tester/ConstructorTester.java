package com.joutvhu.model.tester;

import java.util.ArrayList;
import java.util.List;

class ConstructorTester<T> implements Tester {
    private Creator<T> creatable;

    ConstructorTester(Creator<T> creatable) {
        this.creatable = creatable;
    }

    @Override
    public List<TestResult> test() {
        List<TestResult> results = new ArrayList<>();
        try {
            T result = creatable.create();
            boolean pass = Assert.assertNotNull(result);
            results.add(TestResult.builder()
                    .className(creatable.modelClass.getName())
                    .component("Constructor(" + params() + ")")
                    .status(pass ? TestStatus.PASS : TestStatus.FAIL)
                    .build());
        } catch (Throwable e) {
            results.add(TestResult.builder()
                    .className(creatable.modelClass.getName())
                    .component("Constructor(" + params() + ")")
                    .status(TestStatus.ERROR)
                    .message(e.getMessage())
                    .error(e)
                    .build());
        }
        return results;
    }

    private String params() {
        boolean comma = false;
        StringBuilder builder = new StringBuilder();
        if (creatable.values != null) {
            for (Object value : creatable.values) {
                if (comma)
                    builder.append(",");
                builder.append(value.getClass().getName());
                comma = true;
            }
        }
        if (creatable.parameters != null) {
            for (Creator<?> value : creatable.parameters) {
                if (comma)
                    builder.append(",");
                builder.append(value.modelClass.getName());
                comma = true;
            }
        }
        return builder.toString();
    }
}
