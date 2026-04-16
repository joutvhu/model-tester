package com.joutvhu.model.tester;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HashCodeTesterTest {

    public static class RandomHashCodeModel {
        public RandomHashCodeModel() {}
        @Override
        public int hashCode() {
            return (int) (Math.random() * 1000); // Changes every time, violated consistent hashcode rule
        }
    }

    public static class ErrorHashCodeModel {
        public ErrorHashCodeModel() {}
        @Override
        public int hashCode() {
            throw new RuntimeException("Hashcode error");
        }
    }

    @Test
    void testInconsistentHashCode() {
        ModelTester<RandomHashCodeModel> tester = ModelTester.of(RandomHashCodeModel.class).hashCodeMethod();
        assertFalse(tester.test()); // Should fail consistency check
        List<TestResult> results = tester.getResults();

        boolean hasFailures = results.stream().anyMatch(r -> r.getStatus() == TestStatus.FAIL || r.getStatus() == TestStatus.ERROR);
        assertTrue(hasFailures, "Should report failure for inconsistent hashCode");
    }

    @Test
    void testErrorHashCode() {
        ModelTester<ErrorHashCodeModel> tester = ModelTester.of(ErrorHashCodeModel.class).hashCodeMethod();
        assertFalse(tester.test()); // Should report error
        List<TestResult> results = tester.getResults();

        boolean hasErrors = results.stream().anyMatch(r -> r.getStatus() == TestStatus.ERROR);
        assertTrue(hasErrors, "Should report ERROR when hashCode throws an Exception");
    }
}
