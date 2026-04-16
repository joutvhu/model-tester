package com.joutvhu.model.tester;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CreatorTest {

    interface MyInterface {
        String greet();
    }

    abstract static class AbstractModel {
        public abstract int getCount();
        public String normal() { return "Normal"; }
    }

    static class SimpleModel {
        public String name;
        public SimpleModel() {}
        public SimpleModel(String name) { this.name = name; }
    }

    @Test
    void testBasicTypes() throws Exception {
        Creator<String> stringCreator = Creator.anyOf(String.class);
        assertEquals("", stringCreator.create());

        Creator<Integer> intCreator = Creator.anyOf(Integer.class);
        assertEquals(0, intCreator.create());

        Creator<Boolean> boolCreator = Creator.anyOf(boolean.class);
        assertEquals(true, boolCreator.create());

        Creator<List> listCreator = Creator.anyOf(List.class);
        assertTrue(listCreator.create().isEmpty());

        Creator<Map> mapCreator = Creator.anyOf(Map.class);
        assertTrue(mapCreator.create().isEmpty());
    }

    @Test
    void testCreateInterface() throws Exception {
        Creator<MyInterface> creator = Creator.anyOf(MyInterface.class);
        MyInterface obj = creator.create();
        assertNotNull(obj);
        assertEquals("", obj.greet()); // Method returns empty string for String type
    }

    @Test
    void testCreateAbstractClass() throws Exception {
        Creator<AbstractModel> creator = Creator.anyOf(AbstractModel.class);
        AbstractModel obj = creator.create();
        assertNotNull(obj);
        assertEquals(0, obj.getCount()); // integer method returning 0 due to null conversion fail or default? Wait, abstractProxy returns default instance.
        assertEquals("Normal", obj.normal());
    }

    @Test
    void testMakeCopy() throws Exception {
        SimpleModel original = new SimpleModel("TestName");
        SimpleModel copy = Creator.makeCopy(original);
        
        assertNotNull(copy);
        assertNotSame(original, copy);
        assertEquals("TestName", copy.name);
    }
}
