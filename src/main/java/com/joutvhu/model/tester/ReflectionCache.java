package com.joutvhu.model.tester;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple thread-safe cache for reflection metadata (fields and methods) to improve performance
 * during intensive model testing.
 */
public class ReflectionCache {
    private static final Map<Class<?>, Field[]> fieldsCache = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Method[]> methodsCache = new ConcurrentHashMap<>();

    private ReflectionCache() {
        // Prevent instantiation
    }

    /**
     * Retrieves all fields of a class, including private and inherited ones, with caching.
     *
     * @param clazz the class to inspect.
     * @return an array of all discovered {@link Field} objects.
     */
    public static Field[] getFields(Class<?> clazz) {
        return fieldsCache.computeIfAbsent(clazz, c -> {
            Set<Field> fields = new HashSet<>();
            Class<?> current = c;
            while (current != null && current != Object.class) {
                fields.addAll(Arrays.asList(current.getDeclaredFields()));
                fields.addAll(Arrays.asList(current.getFields()));
                current = current.getSuperclass();
            }
            return fields.toArray(new Field[0]);
        });
    }

    /**
     * Retrieves all methods of a class, including private and inherited ones, with caching.
     *
     * @param clazz the class to inspect.
     * @return an array of all discovered {@link Method} objects.
     */
    public static Method[] getMethods(Class<?> clazz) {
        return methodsCache.computeIfAbsent(clazz, c -> {
            Set<Method> methods = new HashSet<>();
            Class<?> current = c;
            while (current != null && current != Object.class) {
                methods.addAll(Arrays.asList(current.getDeclaredMethods()));
                methods.addAll(Arrays.asList(current.getMethods()));
                current = current.getSuperclass();
            }
            return methods.toArray(new Method[0]);
        });
    }
}
