package com.joutvhu.model.tester;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Internal assertion utility for verifying model state.
 * Supports deep equality checks for collections, maps, and arrays.
 */
@UtilityClass
class Assert {
    private static final Logger log = LoggerFactory.getLogger(Assert.class);

    /**
     * Asserts that two objects are equal using deep equality logic.
     *
     * @param expected the expected value
     * @param actual   the actual value
     * @return true if equal, otherwise throws {@link TesterException}
     */
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

    /**
     * Asserts that an object is not null.
     *
     * @param actual the value to check.
     * @return true if not null, otherwise throws {@link TesterException}.
     */
    public boolean assertNotNull(Object actual) {
        return assertNotNull(actual, null);
    }

    /**
     * Asserts that an object is not null with a custom error message.
     *
     * @param actual  the value to check.
     * @param message the failure message prefix.
     * @return true if not null, otherwise throws {@link TesterException}.
     */
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

    /**
     * Core equality engine. Handles primitives, arrays, collections, and recursive deep equality.
     */
    private boolean objectsAreEqual(Object obj1, Object obj2) {
        if (obj1 == obj2) return true;
        if (obj1 == null || obj2 == null) return false;

        if (obj1.getClass().isArray() && obj2.getClass().isArray()) {
            return arraysAreEqual(obj1, obj2);
        }

        if (obj1 instanceof Iterable && obj2 instanceof Iterable) {
            return iterablesAreEqual((Iterable<?>) obj1, (Iterable<?>) obj2);
        }

        if (obj1 instanceof java.util.Map && obj2 instanceof java.util.Map) {
            return mapsAreEqual((java.util.Map<?, ?>) obj1, (java.util.Map<?, ?>) obj2);
        }

        return obj1.equals(obj2);
    }

    private boolean arraysAreEqual(Object obj1, Object obj2) {
        if (obj1 instanceof boolean[] && obj2 instanceof boolean[])
            return Arrays.equals((boolean[]) obj1, (boolean[]) obj2);
        if (obj1 instanceof char[] && obj2 instanceof char[])
            return Arrays.equals((char[]) obj1, (char[]) obj2);
        if (obj1 instanceof byte[] && obj2 instanceof byte[])
            return Arrays.equals((byte[]) obj1, (byte[]) obj2);
        if (obj1 instanceof short[] && obj2 instanceof short[])
            return Arrays.equals((short[]) obj1, (short[]) obj2);
        if (obj1 instanceof int[] && obj2 instanceof int[])
            return Arrays.equals((int[]) obj1, (int[]) obj2);
        if (obj1 instanceof long[] && obj2 instanceof long[])
            return Arrays.equals((long[]) obj1, (long[]) obj2);
        if (obj1 instanceof float[] && obj2 instanceof float[])
            return Arrays.equals((float[]) obj1, (float[]) obj2);
        if (obj1 instanceof double[] && obj2 instanceof double[])
            return Arrays.equals((double[]) obj1, (double[]) obj2);
        if (obj1 instanceof Object[] && obj2 instanceof Object[])
            return Arrays.deepEquals((Object[]) obj1, (Object[]) obj2);
        return false;
    }

    private boolean iterablesAreEqual(Iterable<?> iter1, Iterable<?> iter2) {
        java.util.Iterator<?> it1 = iter1.iterator();
        java.util.Iterator<?> it2 = iter2.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            if (!objectsAreEqual(it1.next(), it2.next())) return false;
        }
        return !it1.hasNext() && !it2.hasNext();
    }

    private boolean mapsAreEqual(java.util.Map<?, ?> map1, java.util.Map<?, ?> map2) {
        if (map1.size() != map2.size()) return false;
        for (java.util.Map.Entry<?, ?> entry : map1.entrySet()) {
            if (!map2.containsKey(entry.getKey())) return false;
            if (!objectsAreEqual(entry.getValue(), map2.get(entry.getKey()))) return false;
        }
        return true;
    }

    private void failNotEqual(Object expected, Object actual, String message) {
        fail(format(expected, actual, message));
    }

    private String format(Object expected, Object actual, String message) {
        return buildPrefix(message) + formatValues(expected, actual);
    }

    private String buildPrefix(String message) {
        return isNotBlank(message) ? message + " ==> " : "";
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    private boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * Formats the difference between two values for the error message.
     */
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
            log.error("Failed to get canonical name for {}", clazz.getName(), e);
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
                        if (obj instanceof boolean[])
                            return Arrays.toString((boolean[]) obj);
                        if (obj instanceof char[])
                            return Arrays.toString((char[]) obj);
                        if (obj instanceof short[])
                            return Arrays.toString((short[]) obj);
                        if (obj instanceof byte[])
                            return Arrays.toString((byte[]) obj);
                        if (obj instanceof int[])
                            return Arrays.toString((int[]) obj);
                        if (obj instanceof long[])
                            return Arrays.toString((long[]) obj);
                        if (obj instanceof float[])
                            return Arrays.toString((float[]) obj);
                        if (obj instanceof double[])
                            return Arrays.toString((double[]) obj);
                    }

                    return Arrays.deepToString((Object[]) obj);
                } else {
                    String result = obj.toString();
                    return result != null ? result : "null";
                }
            } catch (Throwable e) {
                log.error("Failed to convert object to string", e);
                return defaultToString(obj);
            }
        }
    }

    private String defaultToString(Object obj) {
        return obj == null ? "null" : obj.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(obj));
    }

    private void fail(String message) {
        throw new TesterException(message);
    }
}
