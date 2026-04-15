package com.joutvhu.model.tester;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tester implementation for verifying getter and setter methods.
 * Supports standard POJOs, Java Records (via {@link NamingStrategy#RECORD}),
 * and fluent setters (via {@link NamingStrategy#FLUENT}).
 *
 * @param <T> the type of model being tested
 */
@Slf4j
class GetterSetterTester<T> implements Tester {
    private final Class<T> modelClass;
    private final List<String> include;
    private final List<String> exclude;
    private NamingStrategy namingStrategy = NamingStrategy.DEFAULT;

    GetterSetterTester(Class<T> modelClass, List<String> include, List<String> exclude) {
        this.modelClass = modelClass;
        this.include = include;
        this.exclude = exclude;
    }

    /**
     * Configures a custom naming strategy for identifying getters and setters.
     *
     * @param namingStrategy the strategy to use.
     * @return current tester instance.
     */
    public GetterSetterTester<T> withNamingStrategy(NamingStrategy namingStrategy) {
        this.namingStrategy = namingStrategy;
        return this;
    }

    /**
     * Executes the getter and setter tests for all identified methods in the model class.
     *
     * @return list of results for each method tested.
     */
    @Override
    public List<TestResult> test() {
        List<TestResult> results = new ArrayList<>();
        try {
            T model = Creator.anyOf(modelClass).create();
            if (model == null) {
                results.add(TestResult.builder()
                    .className(modelClass.getName())
                    .component("Instantiation")
                    .status(TestStatus.FAIL)
                    .message("Could not instantiate model")
                    .build());
                return results;
            }

            Set<Method> tested = new HashSet<>();
            testMethods(model, ReflectionCache.getMethods(modelClass), tested, results);
        } catch (Throwable e) {
            log.error("Error during getter/setter testing for {}", modelClass.getName(), e);
            results.add(TestResult.builder()
                .className(modelClass.getName())
                .component("GetterSetter")
                .status(TestStatus.ERROR)
                .message(e.getMessage())
                .error(e)
                .build());
        }
        return results;
    }

    private void testMethods(T model, Method[] methods, Set<Method> tested, List<TestResult> results) {
        for (Method method : methods) {
            if (tested.contains(method))
                continue;
            else
                tested.add(method);

            if (namingStrategy.isGetter(method)) {
                Field field = getField(method);
                if (checkName(method, field)) {
                    if (field != null && method.getReturnType().equals(field.getType()) &&
                        !Modifier.isFinal(field.getModifiers()) &&
                        !Modifier.isStatic(field.getModifiers())) {
                        results.add(testGetter(model, method, field));
                    } else {
                        results.add(testGetter(model, method));
                    }
                }
            } else if (namingStrategy.isSetter(method)) {
                Field field = getField(method);
                if (checkName(method, field)) {
                    if (field != null && method.getParameterTypes()[0].equals(field.getType()) &&
                        !Modifier.isFinal(field.getModifiers()) &&
                        !Modifier.isStatic(field.getModifiers())) {
                        results.add(testSetter(model, method, field));
                    } else {
                        results.add(testSetter(model, method));
                    }
                }
            }
        }
    }

    private Object createTestValue(Class<?> fieldClass) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        try {
            return Creator.anyOf(fieldClass).create();
        } catch (Throwable e) {
            if (Creator.isNullable(fieldClass))
                return null;
            throw e;
        }
    }

    private TestResult testGetter(T model, Method method, Field field) {
        boolean restore = true;
        Object backup = null;
        try {
            field.setAccessible(true);
            backup = field.get(model);
        } catch (Throwable e) {
            restore = false;
        }
        try {
            Object value = createTestValue(field.getType());
            field.setAccessible(true);
            field.set(model, value);
            method.setAccessible(true);
            Object result = method.invoke(model);
            boolean pass = Assert.assertEquals(value, result);
            return TestResult.builder()
                .className(modelClass.getName())
                .component(method.getName())
                .status(pass ? TestStatus.PASS : TestStatus.FAIL)
                .message(pass ? null : "Value mismatch")
                .build();
        } catch (TesterException e) {
            return TestResult.builder()
                .className(modelClass.getName())
                .component(method.getName())
                .status(TestStatus.FAIL)
                .message(e.getMessage())
                .build();
        } catch (Throwable e) {
            return TestResult.builder()
                .className(modelClass.getName())
                .component(method.getName())
                .status(TestStatus.ERROR)
                .message(e.getMessage())
                .error(e)
                .build();
        } finally {
            if (restore) {
                try {
                    field.setAccessible(true);
                    field.set(model, backup);
                } catch (Throwable e) {
                    // Do nothing
                }
            }
        }
    }

    private TestResult testGetter(T model, Method method) {
        try {
            method.setAccessible(true);
            method.invoke(model);
            return TestResult.builder()
                .className(modelClass.getName())
                .component(method.getName())
                .status(TestStatus.PASS)
                .build();
        } catch (Throwable e) {
            return TestResult.builder()
                .className(modelClass.getName())
                .component(method.getName())
                .status(TestStatus.ERROR)
                .message(e.getMessage())
                .error(e)
                .build();
        }
    }

    private TestResult testSetter(T model, Method method, Field field) {
        boolean restore = true;
        Object backup = null;
        try {
            field.setAccessible(true);
            backup = field.get(model);
        } catch (Throwable e) {
            restore = false;
        }
        try {
            Object value = createTestValue(method.getParameterTypes()[0]);
            method.setAccessible(true);
            Object setterResult = method.invoke(model, value);
            field.setAccessible(true);
            Object result = field.get(model);
            boolean pass = Assert.assertEquals(value, result);

            // Fluent setter support
            if (pass && !method.getReturnType().equals(Void.TYPE)) {
                if (setterResult != model) {
                    pass = false;
                    log.warn("Fluent setter {} did not return 'this' for class {}", method.getName(), modelClass.getName());
                }
            }

            return TestResult.builder()
                .className(modelClass.getName())
                .component(method.getName())
                .status(pass ? TestStatus.PASS : TestStatus.FAIL)
                .message(pass ? null : "Value mismatch or incorrect return for fluent setter")
                .build();
        } catch (TesterException e) {
            return TestResult.builder()
                .className(modelClass.getName())
                .component(method.getName())
                .status(TestStatus.FAIL)
                .message(e.getMessage())
                .build();
        } catch (Throwable e) {
            return TestResult.builder()
                .className(modelClass.getName())
                .component(method.getName())
                .status(TestStatus.ERROR)
                .message(e.getMessage())
                .error(e)
                .build();
        } finally {
            if (restore) {
                try {
                    field.setAccessible(true);
                    field.set(model, backup);
                } catch (Throwable e) {
                    // Do nothing
                }
            }
        }
    }

    private TestResult testSetter(T model, Method method) {
        try {
            Object value = createTestValue(method.getParameterTypes()[0]);
            method.setAccessible(true);
            method.invoke(model, value);
            return TestResult.builder()
                .className(modelClass.getName())
                .component(method.getName())
                .status(TestStatus.PASS)
                .build();
        } catch (Throwable e) {
            return TestResult.builder()
                .className(modelClass.getName())
                .component(method.getName())
                .status(TestStatus.ERROR)
                .message(e.getMessage())
                .error(e)
                .build();
        }
    }

    private Field getField(Method method) {
        Field[] fields = ReflectionCache.getFields(modelClass);
        for (Field field : fields) {
            if (namingStrategy.matches(method, field))
                return field;
        }
        return null;
    }

    private boolean checkName(Method method, Field field) {
        if (include != null && !include.isEmpty()) {
            return include.contains(method.getName()) || (field != null && include.contains(field.getName()));
        } else if (exclude != null && !exclude.isEmpty()) {
            return !exclude.contains(method.getName()) && (field == null || !exclude.contains(field.getName()));
        } else {
            return true;
        }
    }
}
