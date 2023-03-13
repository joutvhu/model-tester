package com.joutvhu.model.tester;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@UtilityClass
class Assert {
    public boolean assertEquals(Object expected, Object actual) {
        return assertEquals(expected, actual, null);
    }

    public boolean assertEquals(Object expected, Object actual, String message) {
        if (!objectsAreEqual(expected, actual)) {
            failNotEqual(expected, actual, message);
            return false;
        }
        return true;
    }

    public boolean assertNotNull(Object actual) {
        return assertNotNull(actual, null);
    }

    public boolean assertNotNull(Object actual, String message) {
        if (actual == null) {
            failNull(message);
            return false;
        }
        return true;
    }

    private void failNull(String message) {
        fail(buildPrefix(message) + "expected: not <null>");
    }

    private boolean objectsAreEqual(Object obj1, Object obj2) {
        if (obj1 == null) {
            return obj2 == null;
        } else {
            return obj1.equals(obj2);
        }
    }

    private void failNotEqual(Object expected, Object actual, String message) {
        fail(format(expected, actual, message), expected, actual);
    }

    private String format(Object expected, Object actual, String message) {
        return buildPrefix(message) + formatValues(expected, actual);
    }

    private String buildPrefix(String message) {
        return StringUtils.isNotBlank(message) ? message + " ==> " : "";
    }

    private String formatValues(Object expected, Object actual) {
        String expectedString = toString(expected);
        String actualString = toString(actual);
        return expectedString.equals(actualString) ?
                String.format("expected: %s but was: %s", formatClassAndValue(expected, expectedString), formatClassAndValue(actual, actualString)) :
                String.format("expected: <%s> but was: <%s>", expectedString, actualString);
    }

    private String formatClassAndValue(Object value, String valueString) {
        if (value == null) {
            return "<null>";
        } else {
            String classAndHash = getClassName(value) + toHash(value);
            return value instanceof Class ? "<" + classAndHash + ">" : classAndHash + "<" + valueString + ">";
        }
    }

    private String toString(Object obj) {
        return obj instanceof Class ? getCanonicalName((Class) obj) : nullSafeToString(obj);
    }


    private String toHash(Object obj) {
        return obj == null ? "" : "@" + Integer.toHexString(System.identityHashCode(obj));
    }

    private String getClassName(Object obj) {
        return obj == null ? "null" : (obj instanceof Class ? getCanonicalName((Class) obj) : obj.getClass().getName());
    }

    private String getCanonicalName(Class<?> clazz) {
        try {
            String canonicalName = clazz.getCanonicalName();
            return canonicalName != null ? canonicalName : clazz.getName();
        } catch (Throwable e) {
            e.printStackTrace();
            return clazz.getName();
        }
    }

    private String nullSafeToString(Object obj) {
        if (obj == null) {
            return "null";
        } else {
            try {
                if (obj.getClass().isArray()) {
                    if (obj.getClass().getComponentType().isPrimitive()) {
                        if (obj instanceof boolean[]) {
                            return Arrays.toString((boolean[]) obj);
                        }

                        if (obj instanceof char[]) {
                            return Arrays.toString((char[]) obj);
                        }

                        if (obj instanceof short[]) {
                            return Arrays.toString((short[]) obj);
                        }

                        if (obj instanceof byte[]) {
                            return Arrays.toString((byte[]) obj);
                        }

                        if (obj instanceof int[]) {
                            return Arrays.toString((int[]) obj);
                        }

                        if (obj instanceof long[]) {
                            return Arrays.toString((long[]) obj);
                        }

                        if (obj instanceof float[]) {
                            return Arrays.toString((float[]) obj);
                        }

                        if (obj instanceof double[]) {
                            return Arrays.toString((double[]) obj);
                        }
                    }

                    return Arrays.deepToString((Object[]) obj);
                } else {
                    String result = obj.toString();
                    return result != null ? result : "null";
                }
            } catch (Throwable e) {
                e.printStackTrace();
                return defaultToString(obj);
            }
        }
    }

    private String defaultToString(Object obj) {
        return obj == null ? "null" : obj.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(obj));
    }

    private void fail(String message, Object expected, Object actual) {
        throw new AssertionException(message, expected, actual);
    }

    private void fail(String message) {
        throw new AssertionException(message);
    }
}
