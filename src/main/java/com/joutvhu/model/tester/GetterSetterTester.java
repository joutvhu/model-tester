package com.joutvhu.model.tester;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            Set<Method> tested = new HashSet<>();
            if (!testMethods(model, modelClass.getDeclaredMethods(), tested))
                success = false;
            if (!testMethods(model, modelClass.getMethods(), tested))
                success = false;
            return success;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean testMethods(T model, Method[] methods, Set<Method> tested) {
        boolean success = true;
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
                        success = testGetter(model, method, field) && success;
                    } else {
                        success = testGetter(model, method) && success;
                    }
                }
            } else if (isSetter(method)) {
                Field field = getField(method);
                if (checkName(method, field)) {
                    if (field != null && method.getParameterTypes()[0].equals(field.getType()) &&
                            !Modifier.isFinal(field.getModifiers()) &&
                            !Modifier.isStatic(field.getModifiers())) {
                        success = testSetter(model, method, field) && success;
                    } else {
                        success = testSetter(model, method) && success;
                    }
                }
            }
        }
        return success;
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

    private boolean testGetter(T model, Method method, Field field) {
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
            boolean success = Assert.assertEquals(value, result);
            if (success)
                System.out.println("Success: " + modelClass.getName() + "." + method.getName() + "()");
            else
                System.err.println("Failure: " + modelClass.getName() + "." + method.getName() + "()");
            return success;
        } catch (Throwable e) {
            System.err.println("Error: " + modelClass.getName() + "." + method.getName() + "()");
            e.printStackTrace();
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
        return false;
    }

    private boolean testGetter(T model, Method method) {
        try {
            method.setAccessible(true);
            method.invoke(model);
            System.out.println("Success: " + modelClass.getName() + "." + method.getName() + "()");
            return true;
        } catch (Throwable e) {
            System.err.println("Error: " + modelClass.getName() + "." + method.getName() + "()");
            e.printStackTrace();
            return false;
        }
    }

    private boolean testSetter(T model, Method method, Field field) {
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
            boolean success = Assert.assertEquals(value, result);
            if (success)
                System.out.println("Success: " + modelClass.getName() + "." + method.getName() + "(" + method.getParameterTypes()[0].getName() + ")");
            else
                System.err.println("Failure: " + modelClass.getName() + "." + method.getName() + "(" + method.getParameterTypes()[0].getName() + ")");
            return success;
        } catch (Throwable e) {
            System.err.println("Error: " + modelClass.getName() + "." + method.getName() + "(" + method.getParameterTypes()[0].getName() + ")");
            e.printStackTrace();
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
        return false;
    }

    private boolean testSetter(T model, Method method) {
        try {
            Object value = createTestValue(method.getParameterTypes()[0]);
            method.setAccessible(true);
            method.invoke(model, value);
            System.out.println("Success: " + modelClass.getName() + "." + method.getName() + "(" + method.getParameterTypes()[0].getName() + ")");
            return true;
        } catch (Throwable e) {
            System.err.println("Error: " + modelClass.getName() + "." + method.getName() + "(" + method.getParameterTypes()[0].getName() + ")");
            e.printStackTrace();
            return false;
        }
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
            return include.contains(method.getName()) || (field != null && include.contains(field.getName()));
        } else if (exclude != null && !exclude.isEmpty()) {
            return !exclude.contains(method.getName()) && (field == null || !exclude.contains(field.getName()));
        } else {
            return true;
        }
    }
}
