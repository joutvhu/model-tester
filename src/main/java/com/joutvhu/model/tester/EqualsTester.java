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

    @Override
    public List<TestResult> test() {
        List<TestResult> results = new ArrayList<>();
        try {
            T model = Creator.anyOf(modelClass).create();
            // Check with itself
            results.add(TestResult.builder()
                    .className(modelClass.getName())
                    .component("equals(itself)")
                    .status(Assert.assertEquals(model, model) ? TestStatus.PASS : TestStatus.FAIL)
                    .build());

            // Check equals(null)
            results.add(TestResult.builder()
                    .className(modelClass.getName())
                    .component("equals(null)")
                    .status((model != null && !model.equals(null)) ? TestStatus.PASS : TestStatus.FAIL)
                    .build());

            try {
                T newModel = Creator.makeCopy(model);
                results.add(TestResult.builder()
                        .className(modelClass.getName())
                        .component("equals(copy)")
                        .status(Assert.assertEquals(model, newModel) ? TestStatus.PASS : TestStatus.FAIL)
                        .build());
                deepTest(model, newModel, results);
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
        boolean restore = modelClass.isEnum();
        Map<Field, Object> backup = new HashMap<>();
        for (Field field : fields) {
            try {
                if (restore) {
                    field.setAccessible(true);
                    backup.put(field, field.get(model));
                }
            } catch (Throwable e) {
                // Do nothing
            }
            try {
                if (!tested.contains(field) && !Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                    tested.add(field);
                    field.setAccessible(true);
                    Class<?> fieldType = field.getType();
                    if (fieldType == boolean.class)
                        field.set(newModel, false);
                    else if (fieldType == int.class)
                        field.set(newModel, 1);
                    else if (fieldType == long.class)
                        field.set(newModel, 2l);
                    else if (fieldType == float.class)
                        field.set(newModel, 1.1);
                    else if (fieldType == double.class)
                        field.set(newModel, 1.2);
                    else if (fieldType == char.class)
                        field.set(newModel, 'v');
                    else if (fieldType == byte.class)
                        field.set(newModel, (byte) 10);
                    else if (fieldType == short.class)
                        field.set(newModel, (byte) 20);
                    else
                        field.set(newModel, null);

                    results.add(TestResult.builder()
                            .className(modelClass.getName())
                            .component("equals (changed " + field.getName() + ")")
                            .status(Assert.assertEquals(model, newModel) ? TestStatus.PASS : TestStatus.FAIL)
                            .build());

                    field.set(newModel, field.get(model));
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
}
