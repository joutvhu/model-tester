package com.joutvhu.model.tester;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Strategy to match methods to fields.
 */
public interface NamingStrategy {
    boolean isGetter(Method method);
    boolean isSetter(Method method);
    String getFieldName(Method method);
    boolean matches(Method method, Field field);

    NamingStrategy DEFAULT = new DefaultNamingStrategy();

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
            // Record support: check if it's a record component name
            return isRecordAccessor(method);
        }

        private boolean isRecordAccessor(Method method) {
            try {
                Class<?> clazz = method.getDeclaringClass();
                Method isRecord = Class.class.getMethod("isRecord");
                if ((Boolean) isRecord.invoke(clazz)) {
                    return method.getParameterCount() == 0 && !method.getName().equals("toString") && 
                           !method.getName().equals("hashCode") && !method.getName().equals("getClass");
                }
            } catch (Exception e) {
                // Not Java 14+ or not a record
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
            return name; // Record accessor
        }

        @Override
        public boolean matches(Method method, Field field) {
            String fieldName = getFieldName(method);
            return fieldName.equalsIgnoreCase(field.getName());
        }
    }
}
