package com.joutvhu.model.tester;

import com.joutvhu.model.tester.domain.SimplePojo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTesterTest {

    public static class BadModel {
        public BadModel() {
            throw new RuntimeException("Constructor failed");
        }
    }

    interface AbstractModel {
    }

    @Test
    void testInterfaceThrowsException() {
        assertThrows(TesterException.class, () -> new ModelTester<>(AbstractModel.class));
    }

    @Test
    void testConstructorsConfig() {
        ModelTester<SimplePojo> tester = ModelTester.of(SimplePojo.class)
            .constructors();
        assertNotNull(tester);
    }

    @Test
    void testIncludesExcludesConfig() {
        ModelTester<SimplePojo> tester = ModelTester.of(SimplePojo.class)
            .include("id")
            .exclude("name");
        assertNotNull(tester);
    }

    @Test
    void testFailTestThrowsTesterException() {
        ModelTester<BadModel> tester = ModelTester.of(BadModel.class).constructors();
        
        TesterException exception = assertThrows(TesterException.class, tester::testAndThrows);
        
        assertTrue(exception.getMessage().contains("Fail when testing class"));
        assertTrue(exception.getMessage().contains("BadModel"));
    }
}
