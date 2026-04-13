package com.joutvhu.model.tester;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class GetterSetterTester<T> implements Tester {
    private static final Logger log = LoggerFactory.getLogger(GetterSetterTester.class);

    private Class<T> modelClass;
    private List<String> include;
    private List<String> exclude;

    GetterSetterTester(Class<T> modelClass, List<String> include, List<String> exclude) {
        this.modelClass = modelClass;
        this.include = include;
        this.exclude = exclude;
    }

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

            if (isGetter(method)) {
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
            } else if (isSetter(method)) {
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
        boolean restore = modelClass.isEnum();
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
        boolean restore = modelClass.isEnum();
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
            method.invoke(model, value);
            field.setAccessible(true);
            Object result = field.get(model);
            boolean pass = Assert.assertEquals(value, result);
            return TestResult.builder()
                    .className(modelClass.getName())
                    .component(method.getName())
                    .status(pass ? TestStatus.PASS : TestStatus.FAIL)
                    .message(pass ? null : "Value mismatch in field")
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
        String methodName = method.getName();
        if (methodName.startsWith("get") || methodName.startsWith("set"))
            methodName = methodName.substring(3);
        else if (methodName.startsWith("is"))
            methodName = methodName.substring(2);

        Field[] fields = ReflectionCache.getFields(modelClass);
        for (Field field : fields) {
            if (methodName.equalsIgnoreCase(field.getName()))
                return field;
        }
        return null;
    }

    private boolean isGetter(Method method) {
        String methodName = method.getName();
        if (methodName.length() > 3 && methodName.startsWith("get") && method.getParameterCount() == 0) {
            return true;
        }
        if (methodName.length() > 2 && methodName.startsWith("is") && method.getParameterCount() == 0) {
            Class<?> returnType = method.getReturnType();
            return Boolean.class.equals(returnType) || returnType == boolean.class;
        }
        return false;
    }

    private boolean isSetter(Method method) {
        String methodName = method.getName();
        return methodName.length() > 3 && methodName.startsWith("set") && method.getParameterCount() == 1;
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
