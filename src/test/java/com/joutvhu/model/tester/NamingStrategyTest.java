package com.joutvhu.model.tester;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class NamingStrategyTest {

    static class Dummy {
        private String name;
        private boolean active;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        
        // Fluent setter
        public Dummy name(String name) { this.name = name; return this; }
        
        // Invalid getter
        public String get(String prop) { return null; }
    }

    @Test
    void testDefaultNamingStrategy() throws NoSuchMethodException, NoSuchFieldException {
        NamingStrategy strategy = NamingStrategy.DEFAULT;
        
        Method getName = Dummy.class.getMethod("getName");
        Method setName = Dummy.class.getMethod("setName", String.class);
        Method isActive = Dummy.class.getMethod("isActive");
        Method getWithParam = Dummy.class.getMethod("get", String.class);
        
        // isGetter
        assertTrue(strategy.isGetter(getName));
        assertTrue(strategy.isGetter(isActive));
        assertFalse(strategy.isGetter(setName));
        assertFalse(strategy.isGetter(getWithParam));
        
        // isSetter
        assertTrue(strategy.isSetter(setName));
        assertFalse(strategy.isSetter(getName));
        
        // getFieldName
        assertEquals("Name", strategy.getFieldName(getName));
        assertEquals("Name", strategy.getFieldName(setName));
        assertEquals("Active", strategy.getFieldName(isActive));
        
        // matches
        Field nameField = Dummy.class.getDeclaredField("name");
        assertTrue(strategy.matches(getName, nameField));
        assertTrue(strategy.matches(setName, nameField));
    }

    @Test
    void testFluentNamingStrategy() throws NoSuchMethodException {
        NamingStrategy strategy = NamingStrategy.FLUENT;
        
        Method fluentName = Dummy.class.getMethod("name", String.class);
        Method setName = Dummy.class.getMethod("setName", String.class);
        
        // isSetter
        assertTrue(strategy.isSetter(fluentName));
        assertTrue(strategy.isSetter(setName));
        
        // getFieldName
        assertEquals("name", strategy.getFieldName(fluentName));
        assertEquals("Name", strategy.getFieldName(setName));
    }
}
