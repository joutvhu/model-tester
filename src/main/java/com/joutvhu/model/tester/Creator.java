package com.joutvhu.model.tester;

import javassist.util.proxy.ProxyFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.*;

public class Creator<T> {
    final Class<T> modelClass;
    final List<Creator<?>> parameters;
    final Object[] values;
    final T staticValue;

    private Creator(Class<T> modelClass, List<Creator<?>> parameters, T staticValue, Object[] values) {
        this.modelClass = modelClass;
        this.parameters = parameters;
        this.staticValue = staticValue;
        this.values = values;
    }

    public static <T> Creator<T> of(Constructor<T> constructor) {
        List<Creator<?>> parameters = new ArrayList<>();
        for (Class<?> parameterType : constructor.getParameterTypes()) {
            parameters.add(Creator.anyOf(parameterType));
        }
        return new Creator<>(constructor.getDeclaringClass(), parameters, null, null);
    }

    public static <T> Creator<T> of(Class<T> modelClass, Creator<?>... parameters) {
        return new Creator<>(modelClass, Arrays.asList(parameters), null, null);
    }

    public static <T> Creator<T> byParams(Class<T> modelClass, Object... parameters) {
        return new Creator<>(modelClass, null, null, parameters);
    }

    public static <T> Creator<T> anyOf(Class<T> modelClass) {
        T result = tryMakeProxy(modelClass);
        if (result != null)
            return new Creator<>(modelClass, null, result, null);

        Constructor<T>[] constructors = (Constructor<T>[]) modelClass.getConstructors();
        if (constructors.length == 0)
            constructors = (Constructor<T>[]) modelClass.getDeclaredConstructors();
        if (constructors.length > 0) {
            Constructor<T> constructor = constructors[0];
            for (Constructor<T> c : constructors) {
                if (c.getParameterCount() < constructor.getParameterCount())
                    constructor = c;
            }
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            Creator<?>[] parameters = new Creator[parameterTypes.length];
            for (int i = 0, len = parameterTypes.length; i < len; i++) {
                parameters[i] = anyOf(parameterTypes[i]);
            }
            return of(modelClass, parameters);
        }
        return new Creator<>(modelClass, new ArrayList<>(), null, null);
    }

    public static <T> Creator<T>[] allOf(Class<T> modelClass) {
        ArrayList<Creator<T>> creators = new ArrayList<>();
        if (modelClass.isEnum()) {
            Object[] values = EnumSet.allOf((Class<? extends Enum>) modelClass).toArray();
            for (Object value : values) {
                creators.add(new Creator<>(modelClass, null, (T) value, null));
            }
        } else {
            Set<Constructor<?>> created = new HashSet<>();
            for (Constructor<?> constructor : modelClass.getConstructors()) {
                if (!created.contains(constructor)) {
                    created.add(constructor);
                    creators.add(Creator.of((Constructor<T>) constructor));
                }
            }
            for (Constructor<?> constructor : modelClass.getDeclaredConstructors()) {
                if (!created.contains(constructor)) {
                    created.add(constructor);
                    creators.add(Creator.of((Constructor<T>) constructor));
                }
            }
        }
        return creators.toArray(new Creator[creators.size()]);
    }

    public T create() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (staticValue != null)
            return staticValue;
        T result = makeProxy(modelClass);
        if (result != null)
            return result;
        int mod = modelClass.getModifiers();
        if (Modifier.isInterface(mod))
            return interfaceProxy(modelClass);
        if (Modifier.isAbstract(mod))
            return abstractProxy(modelClass);
        if (values != null)
            return create(modelClass, values);
        return create(modelClass, parameters);
    }

    private <T> T abstractProxy(Class<T> modelClass) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(modelClass);
        factory.setFilter(method -> Modifier.isAbstract(method.getModifiers()));
        Class<?>[] parameterTypes;
        Object[] parameterValues;
        if (values != null) {
            parameterTypes = new Class[values.length];
            parameterValues = values;
            for (int i = 0, len = values.length; i < len; i++) {
                parameterTypes[i] = values[i].getClass();
            }
        } else {
            if (parameters != null) {
                parameterTypes = new Class[parameters.size()];
                for (int i = 0, len = parameters.size(); i < len; i++) {
                    parameterTypes[i] = parameters.get(i).modelClass;
                }
            } else {
                Constructor<T>[] constructors = (Constructor<T>[]) modelClass.getConstructors();
                if (constructors.length == 0)
                    constructors = (Constructor<T>[]) modelClass.getDeclaredConstructors();
                if (constructors.length > 0) {
                    parameterTypes = constructors[0].getParameterTypes();
                } else {
                    parameterTypes = new Class[0];
                }
            }
            parameterValues = new Object[parameterTypes.length];
            for (int i = 0, len = parameterTypes.length; i < len; i++) {
                parameterValues[i] = anyOf(parameterTypes[i]).create();
            }
        }
        return (T) factory.create(parameterTypes, parameterValues, (self, thisMethod, proceed, args) -> {
            if (!Void.class.equals(thisMethod.getReturnType())) {
                try {
                    return anyOf(thisMethod.getReturnType()).create();
                } catch (Throwable x) {
                    return null;
                }
            }
            return null;
        });
    }

    private <T> T interfaceProxy(Class<T> modelClass) {
        return (T) Proxy.newProxyInstance(modelClass.getClassLoader(), new Class[]{modelClass}, (proxy, method, args) -> {
            if (!Void.class.equals(method.getReturnType())) {
                try {
                    return anyOf(method.getReturnType()).create();
                } catch (Throwable x) {
                    return null;
                }
            }
            return null;
        });
    }

    public static <T> T create(Class<T> modelClass, List<Creator<?>> parameters) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?>[] parameterTypes = new Class[parameters.size()];
        Object[] params = new Object[parameters.size()];
        for (int i = 0, len = parameters.size(); i < len; i++) {
            parameterTypes[i] = parameters.get(i).modelClass;
            params[i] = parameters.get(i).create();
        }
        Constructor<?> constructor = modelClass.getConstructor(parameterTypes);
        return (T) constructor.newInstance(params);
    }

    public static <T> T create(Class<T> modelClass, Object... params) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?>[] paramClasses = new Class[params.length];
        for (int i = 0, len = params.length; i < len; i++) {
            Object param = params[i];
            paramClasses[i] = param.getClass();
        }
        Constructor<T> constructor = modelClass.getConstructor(paramClasses);
        return constructor.newInstance(params);
    }

    private static <T> T tryMakeProxy(Class<T> modelClass) {
        try {
            return makeProxy(modelClass);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T makeCopy(T value) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        if (value == null)
            return null;
        Class<T> modelClass = (Class<T>) value.getClass();
        if (String.class.equals(modelClass) ||
                Boolean.class.equals(modelClass) || modelClass == boolean.class ||
                Integer.class.equals(modelClass) || modelClass == int.class ||
                Long.class.equals(modelClass) || modelClass == long.class ||
                Float.class.equals(modelClass) || modelClass == float.class ||
                Double.class.equals(modelClass) || modelClass == double.class ||
                Character.class.equals(modelClass) || modelClass == char.class ||
                Byte.class.equals(modelClass) || modelClass == byte.class ||
                Short.class.equals(modelClass) || modelClass == short.class ||
                BigInteger.class.equals(modelClass) ||
                BigDecimal.class.equals(modelClass) ||
                modelClass.isEnum())
            return value;
        T newValue = anyOf(modelClass).create();
        if (value instanceof Map) {
            ((Map) newValue).putAll((Map) value);
        } else if (value instanceof Collection) {
            ((Collection) newValue).addAll((Collection) value);
        } else if (newValue != null) {
            Class<?> clazz = modelClass;
            do {
                copyFields(modelClass.getDeclaredFields(), value, newValue);
                copyFields(modelClass.getFields(), value, newValue);
                clazz = clazz.getSuperclass();
            } while (clazz != null && !Object.class.equals(clazz));
        }
        return newValue;
    }

    private static void copyFields(Field[] fields, Object v1, Object v2) throws IllegalAccessException {
        for (Field field : fields) {
            if (!Modifier.isFinal(field.getModifiers())) {
                field.setAccessible(true);
                field.set(v2, field.get(v1));
            }
        }
    }

    private static <T> T makeProxy(Class<T> modelClass) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (String.class.equals(modelClass))
            return (T) "";
        if (Boolean.class.equals(modelClass) || modelClass == boolean.class)
            return (T) Boolean.TRUE;
        if (Integer.class.equals(modelClass) || modelClass == int.class)
            return (T) Integer.valueOf(0);
        if (Long.class.equals(modelClass) || modelClass == long.class)
            return (T) Long.valueOf(0);
        if (Float.class.equals(modelClass) || modelClass == float.class)
            return (T) Float.valueOf(0);
        if (Double.class.equals(modelClass) || modelClass == double.class)
            return (T) Double.valueOf(0);
        if (Character.class.equals(modelClass) || modelClass == char.class)
            return (T) Character.valueOf('c');
        if (Byte.class.equals(modelClass) || modelClass == byte.class)
            return (T) Byte.valueOf((byte) 0);
        if (Short.class.equals(modelClass) || modelClass == short.class)
            return (T) Short.valueOf((short) 0);
        if (BigInteger.class.equals(modelClass))
            return (T) BigInteger.valueOf(0);
        if (BigDecimal.class.equals(modelClass))
            return (T) BigDecimal.valueOf(0);
        if (modelClass.isArray())
            return (T) Array.newInstance(modelClass.getComponentType(), 0);
        if (List.class.equals(modelClass) || Collection.class.equals(modelClass) || AbstractCollection.class.equals(modelClass) || AbstractList.class.equals(modelClass))
            return (T) new ArrayList<>();
        if (Map.class.equals(modelClass) || AbstractMap.class.equals(modelClass))
            return (T) new HashMap<>();
        if (Set.class.equals(modelClass) || AbstractSet.class.equals(modelClass))
            return (T) new HashSet<>();
        if (SortedSet.class.equals(modelClass) || NavigableSet.class.equals(modelClass))
            return (T) new TreeSet<>();
        if (Queue.class.equals(modelClass) || Deque.class.equals(modelClass) || AbstractSequentialList.class.equals(modelClass))
            return (T) new LinkedList<>();
        if (SortedMap.class.equals(modelClass) || NavigableMap.class.equals(modelClass))
            return (T) new TreeMap<>();
        if (Dictionary.class.equals(modelClass))
            return (T) new Hashtable<>();
        if (Temporal.class.equals(modelClass) || Instant.class.equals(modelClass))
            return (T) Instant.now();
        return makeEnum(modelClass);
    }

    private static <T> T makeEnum(Class<T> modelClass) {
        if (modelClass.isEnum()) {
            Object[] values = EnumSet.allOf((Class<? extends Enum>) modelClass).toArray();
            return (T) values[0];
        }
        return null;
    }
}
