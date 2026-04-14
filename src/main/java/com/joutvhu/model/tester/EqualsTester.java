package com.joutvhu.model.tester;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tester implementation for verifying the {@link Object#equals(Object)} contract.
 * Checks for reflexivity, null-safety, and verifies that changing field values
 * results in inequality.
 *
 * @param <T> the type of model being tested
 */
class EqualsTester<T> implements Tester {
    private static final Logger log = LoggerFactory.getLogger(EqualsTester.class);

    private final Class<T> modelClass;
    private final boolean safe;

    EqualsTester(Class<T> modelClass) {
        this(modelClass, false);
    }

    EqualsTester(Class<T> modelClass, boolean safe) {
        this.modelClass = modelClass;
        this.safe = safe;
    }

    /**
     * Tests the equals method for reflexivity, null-safety, and behavior on field changes.
     *
     * @return list of results for each scenario tested.
     */
    @Override
    public List<TestResult> test() {
        List<TestResult> results = new ArrayList<>();
        try {
            T model = Creator.anyOf(modelClass).create();
            // Check with itself
            results.add(runTest(modelClass.getName(), "equals(itself)", () -> {
                if (!model.equals(model)) {
                    throw new TesterException("Object should be equal to itself");
                }
            }));

            // Check equals(null)
            results.add(runTest(modelClass.getName(), "equals(null)", () -> {
                if (model != null && model.equals(null)) {
                    throw new TesterException("Object should not be equal to null");
                }
            }));

            try {
                T newModel = Creator.makeCopy(model);
                results.add(runTest(modelClass.getName(), "equals(copy)", () -> {
                    Assert.assertEquals(model, newModel, "Copy should be equal to original");
                }));
                if (!modelClass.isEnum()) {
                    deepTest(model, newModel, results);
                }
            } catch (Throwable x) {
                if (safe) {
                    deepTest(model, null, results);
                } else {
                    throw x;
                }
            }
        } catch (Throwable e) {
            log.error("Error during equals testing for {}", modelClass.getName(), e);
            results.add(TestResult.builder()
                .className(modelClass.getName())
                .component("equals")
                .status(TestStatus.ERROR)
                .message(e.getMessage())
                .error(e)
                .build());
        }
        return results;
    }

    /**
     * Internal helper to execute a singular test and wrap it in a TestResult.
     */
    private TestResult runTest(String className, String component, Runnable test) {
        try {
            test.run();
            return TestResult.builder()
                .className(className)
                .component(component)
                .status(TestStatus.PASS)
                .build();
        } catch (TesterException e) {
            return TestResult.builder()
                .className(className)
                .component(component)
                .status(TestStatus.FAIL)
                .message(e.getMessage())
                .build();
        } catch (Throwable e) {
            return TestResult.builder()
                .className(className)
                .component(component)
                .status(TestStatus.ERROR)
                .message(e.getMessage())
                .error(e)
                .build();
        }
    }

    private void deepTest(T model, T newModel, List<TestResult> results) {
        try {
            if (newModel == null)
                newModel = Creator.makeCopy(model);
            Set<Field> tested = new HashSet<>();
            deepTest(model, newModel, ReflectionCache.getFields(modelClass), tested, results);
        } catch (Throwable e) {
            log.error("Error during deep equals testing for {}", modelClass.getName(), e);
        }
    }

    private void deepTest(T model, T newModel, Field[] fields, Set<Field> tested, List<TestResult> results) {
        boolean restore = true;
        Map<Field, Object> backup = new HashMap<>();
        for (Field field : fields) {
            try {
                if (!tested.contains(field) && !Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                    tested.add(field);
                    field.setAccessible(true);

                    Object originalValue = field.get(model);
                    if (restore) {
                        backup.put(field, originalValue);
                    }

                    Object newValue = createDifferentValue(field.getType(), originalValue);
                    field.set(newModel, newValue);

                    results.add(runTest(modelClass.getName(), "equals (changed " + field.getName() + ")", () -> {
                        if (model.equals(newModel)) {
                            throw new TesterException("Objects should NOT be equal after changing field: " + field.getName());
                        }
                    }));

                    field.set(newModel, originalValue);
                }
            } catch (Throwable e) {
                // Do nothing
            }
        }
        if (restore && !backup.isEmpty()) {
            for (Map.Entry<Field, Object> entry : backup.entrySet()) {
                try {
                    Field field = entry.getKey();
                    Object value = entry.getValue();
                    field.set(model, value);
                } catch (Throwable e) {
                    // Do nothing
                }
            }
        }
    }

    private Object createDifferentValue(Class<?> fieldType, Object originalValue) {
        if (fieldType == boolean.class || fieldType == Boolean.class)
            return originalValue != null && (Boolean) originalValue ? false : true;
        if (fieldType == int.class || fieldType == Integer.class)
            return (originalValue != null ? (Integer) originalValue : 0) + 1;
        if (fieldType == long.class || fieldType == Long.class)
            return (originalValue != null ? (Long) originalValue : 0L) + 1L;
        if (fieldType == float.class || fieldType == Float.class)
            return (originalValue != null ? (Float) originalValue : 0.0f) + 1.1f;
        if (fieldType == double.class || fieldType == Double.class)
            return (originalValue != null ? (Double) originalValue : 0.0) + 1.2;
        if (fieldType == char.class || fieldType == Character.class)
            return originalValue != null && (Character) originalValue == 'a' ? 'b' : 'a';
        if (fieldType == byte.class || fieldType == Byte.class)
            return (byte) ((originalValue != null ? (Byte) originalValue : 0) + 1);
        if (fieldType == short.class || fieldType == Short.class)
            return (short) ((originalValue != null ? (Short) originalValue : 0) + 1);

        if (fieldType == String.class)
            return (originalValue != null ? (String) originalValue : "") + "diff";
        if (java.util.Date.class.isAssignableFrom(fieldType))
            return new java.util.Date((originalValue != null ? ((java.util.Date) originalValue).getTime() : 0) + 1000);

        if (java.util.List.class.isAssignableFrom(fieldType))
            return java.util.Collections.singletonList("diff");
        if (java.util.Set.class.isAssignableFrom(fieldType))
            return java.util.Collections.singleton("diff");
        if (java.util.Map.class.isAssignableFrom(fieldType))
            return java.util.Collections.singletonMap("diff", "diff");

        return originalValue == null ? new Object() : null;
    }
}
