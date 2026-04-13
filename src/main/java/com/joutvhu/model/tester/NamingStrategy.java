package com.joutvhu.model.tester;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Strategy for identifying if a method is a getter or a setter, 
 * and matching fields to their corresponding methods.
 */
public interface NamingStrategy {
    boolean isGetter(Method method);
    boolean isSetter(Method method);
    String getFieldName(Method method);
    boolean matches(Method method, Field field);

    NamingStrategy DEFAULT = new DefaultNamingStrategy();
    NamingStrategy RECORD = new RecordNamingStrategy();
    NamingStrategy FLUENT = new FluentNamingStrategy();

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
            } catch (Exception e) {}
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
