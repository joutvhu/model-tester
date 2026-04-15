package com.joutvhu.model.tester;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Strategy for identifying if a method is a getter or a setter,
 * and matching methods to their corresponding fields.
 * This allows the library to support various coding conventions.
 */
public interface NamingStrategy {
    /**
     * Checks if the method is a getter.
     *
     * @param method the method to check.
     * @return true if it looks like a getter.
     */
    boolean isGetter(Method method);

    /**
     * Checks if the method is a setter.
     *
     * @param method the method to check.
     * @return true if it looks like a setter.
     */
    boolean isSetter(Method method);

    /**
     * Extracts the potential field name from a method name.
     *
     * @param method the method to analyze.
     * @return the inferred field name.
     */
    String getFieldName(Method method);

    /**
     * Verifies if a method matches a specific field based on naming conventions.
     *
     * @param method the method to match.
     * @param field  the field to match.
     * @return true if they correspond to each other.
     */
    boolean matches(Method method, Field field);

    /**
     * Standard POJO naming strategy: getXxx, setXxx, isXxx (booleans).
     */
    NamingStrategy DEFAULT = new DefaultNamingStrategy();
    /**
     * Strategy for Java Records: getters match parameter names, no setters.
     */
    NamingStrategy RECORD = new RecordNamingStrategy();
    /**
     * Strategy for fluent builders or models: setXxx or xxx() returning 'this'.
     */
    NamingStrategy FLUENT = new FluentNamingStrategy();

    /**
     * Default implementation of {@link NamingStrategy} following standard POJO conventions.
     * Expects "get" and "set" prefixes for most fields, and "is" for boolean getters.
     */
    class DefaultNamingStrategy implements NamingStrategy {
        @Override
        public boolean isGetter(Method method) {
            String name = method.getName();
            int params = method.getParameterCount();
            if (params != 0) return false;

            if (name.length() > 3 && name.startsWith("get")) return true;
            if (name.length() > 2 && name.startsWith("is")) {
                Class<?> rt = method.getReturnType();
                return rt == boolean.class || rt == Boolean.class;
            }
            return false;
        }

        @Override
        public boolean isSetter(Method method) {
            String name = method.getName();
            return name.length() > 3 && name.startsWith("set") && method.getParameterCount() == 1;
        }

        @Override
        public String getFieldName(Method method) {
            String name = method.getName();
            if (name.startsWith("get") || name.startsWith("set")) return name.substring(3);
            if (name.startsWith("is")) return name.substring(2);
            return name;
        }

        @Override
        public boolean matches(Method method, Field field) {
            String fieldName = getFieldName(method);
            return fieldName.equalsIgnoreCase(field.getName());
        }
    }

    /**
     * Naming strategy for Java Records.
     * Getters do not have prefixes, and setters are not supported for records.
     */
    class RecordNamingStrategy extends DefaultNamingStrategy {
        @Override
        public boolean isGetter(Method method) {
            if (super.isGetter(method)) return true;
            try {
                Class<?> clazz = method.getDeclaringClass();
                Method isRecord = Class.class.getMethod("isRecord");
                if ((Boolean) isRecord.invoke(clazz)) {
                    return method.getParameterCount() == 0 && !method.getName().equals("toString") &&
                        !method.getName().equals("hashCode") && !method.getName().equals("getClass");
                }
            } catch (Exception e) {
            }
            return false;
        }

        @Override
        public boolean isSetter(Method method) {
            return false;
        }

        @Override
        public String getFieldName(Method method) {
            String name = method.getName();
            if (name.startsWith("get") || name.startsWith("is")) return super.getFieldName(method);
            return name;
        }
    }

    /**
     * Naming strategy supporting fluent setter patterns (chaining).
     * Setters are identified by having one parameter and returning the object instance.
     */
    class FluentNamingStrategy extends DefaultNamingStrategy {
        @Override
        public boolean isSetter(Method method) {
            if (super.isSetter(method)) return true;
            // Fluent setters: name matches field name or starts with field name, 1 param, returns declaring class
            return method.getParameterCount() == 1 && method.getReturnType().isAssignableFrom(method.getDeclaringClass());
        }

        @Override
        public String getFieldName(Method method) {
            String name = method.getName();
            if (name.startsWith("set") || name.startsWith("get") || name.startsWith("is"))
                return super.getFieldName(method);
            return name;
        }
    }
}
