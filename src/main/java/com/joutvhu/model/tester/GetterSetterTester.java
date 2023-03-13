package com.joutvhu.model.tester;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

class GetterSetterTester<T> implements Tester {
    private Class<T> modelClass;
    private List<String> include;
    private List<String> exclude;

    GetterSetterTester(Class<T> modelClass, List<String> include, List<String> exclude) {
        this.modelClass = modelClass;
        this.include = include;
        this.exclude = exclude;
    }

    @Override
    public boolean test() {
        try {
            T model = Creator.anyOf(modelClass).create();
            if (model == null)
                return false;
            Method[] methods = modelClass.getMethods();
            for (Method method : methods) {
                if (isGetter(method)) {
                    Field field = getField(method);
                    if (field != null && checkName(field.getName()))
                        testGetter(model, method, field);
                } else if (isSetter(method)) {
                    Field field = getField(method);
                    if (field != null && checkName(field.getName()))
                        testSetter(model, method, field);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    private void testGetter(T model, Method method, Field field) {
        try {
            Object value = Creator.anyOf(field.getType()).create();
            field.setAccessible(true);
            field.set(model, value);
            method.setAccessible(true);
            Object result = method.invoke(model);
            Assert.assertEquals(value, result);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void testSetter(T model, Method method, Field field) {
        try {
            Object value = Creator.anyOf(method.getParameterTypes()[0]).create();
            method.setAccessible(true);
            method.invoke(model, value);
            field.setAccessible(true);
            Object result = field.get(model);
            Assert.assertEquals(value, result);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private Field getField(Method method) {
        String methodName = method.getName();
        if (methodName.startsWith("get") || methodName.startsWith("set"))
            methodName = methodName.substring(3);
        else if (methodName.startsWith("is"))
            methodName = methodName.substring(2);
        for (Field field : modelClass.getFields()) {
            if (methodName.equalsIgnoreCase(field.getName()))
                return field;
        }
        for (Field field : modelClass.getDeclaredFields()) {
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

    private boolean checkName(String name) {
        if (include != null && !include.isEmpty()) {
            return include.contains(name);
        } else if (exclude != null && !exclude.isEmpty()) {
            return !exclude.contains(name);
        } else {
            return true;
        }
    }
}
