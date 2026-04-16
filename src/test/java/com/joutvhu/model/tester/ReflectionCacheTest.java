package com.joutvhu.model.tester;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ReflectionCacheTest {

    static class Parent {
        private String parentField;
        public void parentMethod() {}
    }

    static class Child extends Parent {
        private String childField;
        public void childMethod() {}
    }

    @Test
    void testGetFields() {
        Field[] fields = ReflectionCache.getFields(Child.class);
        assertNotNull(fields);
        
        boolean hasParentField = Arrays.stream(fields).anyMatch(f -> f.getName().equals("parentField"));
        boolean hasChildField = Arrays.stream(fields).anyMatch(f -> f.getName().equals("childField"));
        
        assertTrue(hasParentField, "Should include parent fields");
        assertTrue(hasChildField, "Should include child fields");

        // Test caching (reference equality)
        Field[] fields2 = ReflectionCache.getFields(Child.class);
        assertSame(fields, fields2, "Should return cached array instance");
    }

    @Test
    void testGetMethods() {
        Method[] methods = ReflectionCache.getMethods(Child.class);
        assertNotNull(methods);
        
        boolean hasParentMethod = Arrays.stream(methods).anyMatch(m -> m.getName().equals("parentMethod"));
        boolean hasChildMethod = Arrays.stream(methods).anyMatch(m -> m.getName().equals("childMethod"));
        
        assertTrue(hasParentMethod, "Should include parent methods");
        assertTrue(hasChildMethod, "Should include child methods");

        // Test caching (reference equality)
        Method[] methods2 = ReflectionCache.getMethods(Child.class);
        assertSame(methods, methods2, "Should return cached array instance");
    }
}
