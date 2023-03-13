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
            boolean success = true;
            if (!testMethods(model, modelClass.getDeclaredMethods()))
                success = false;
            if (!testMethods(model, modelClass.getMethods()))
                success = false;
            return success;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean testMethods(T model, Method[] methods) {
        boolean success = true;
        for (Method method : methods) {
            if (isGetter(method)) {
                Field field = getField(method);
                if (field != null && checkName(method, field)) {
                    boolean result = testGetter(model, method, field);
                    if (success) success = result;
                }
            } else if (isSetter(method)) {
                Field field = getField(method);
                if (field != null && checkName(method, field)) {
                    boolean result = testSetter(model, method, field);
                    if (success) success = result;
                }
            }
        }
        return success;
    }

    private boolean testGetter(T model, Method method, Field field) {
        try {
            Object value = Creator.anyOf(field.getType()).create();
            field.setAccessible(true);
            field.set(model, value);
            method.setAccessible(true);
            Object result = method.invoke(model);
            return Assert.assertEquals(value, result);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean testSetter(T model, Method method, Field field) {
        try {
            Object value = Creator.anyOf(method.getParameterTypes()[0]).create();
            method.setAccessible(true);
            method.invoke(model, value);
            field.setAccessible(true);
            Object result = field.get(model);
            return Assert.assertEquals(value, result);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    private Field getField(Method method) {
        String methodName = method.getName();
        if (methodName.startsWith("get") || methodName.startsWith("set"))
            methodName = methodName.substring(3);
        else if (methodName.startsWith("is"))
            methodName = methodName.substring(2);
        Class<?> clazz = modelClass;
        do {
            Field field = getField(methodName, clazz.getDeclaredFields());
            if (field != null)
                return field;
            field = getField(methodName, clazz.getFields());
            if (field != null)
                return field;
            clazz = clazz.getSuperclass();
        } while (clazz != null && !Object.class.equals(clazz));
        return null;
    }

    private Field getField(String fieldName, Field[] fields) {
        for (Field field : fields) {
            if (fieldName.equalsIgnoreCase(field.getName()))
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
            return include.contains(method.getName()) || include.contains(field.getName());
        } else if (exclude != null && !exclude.isEmpty()) {
            return !exclude.contains(method.getName()) && !exclude.contains(field.getName());
        } else {
            return true;
        }
    }
}
