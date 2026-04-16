package com.joutvhu.model.tester;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConstructorTesterTest {

    public static class BadConstructorModel {
        public BadConstructorModel() {}
        public BadConstructorModel(String param) {
            throw new RuntimeException("Constructor error");
        }
    }

    @Test
    void testErrorConstructor() {
        ModelTester<BadConstructorModel> tester = ModelTester.of(BadConstructorModel.class).constructors();
        assertFalse(tester.test()); // Should fail because constructor throws
        
        List<TestResult> results = tester.getResults();
        boolean hasErrors = results.stream().anyMatch(r -> r.getStatus() == TestStatus.ERROR);
        assertTrue(hasErrors, "Should capture an ERROR result when constructor throws exception");
    }
}
