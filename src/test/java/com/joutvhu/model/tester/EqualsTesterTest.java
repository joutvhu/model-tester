package com.joutvhu.model.tester;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EqualsTesterTest {

    public static class BadEqualsModel {
        private String value;

        public BadEqualsModel() {}
        public BadEqualsModel(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            // Always return true, which violates equals contract for different instances/types
            return true;
        }
    }

    public static class NullFailingEqualsModel {
        public NullFailingEqualsModel() {}
        @Override
        public boolean equals(Object obj) {
            // Fails on null
            return obj.toString().equals("test");
        }
    }

    @Test
    void testBadEquals() {
        ModelTester<BadEqualsModel> tester = ModelTester.of(BadEqualsModel.class).equalsMethod();
        assertFalse(tester.test()); // Should fail
        List<TestResult> results = tester.getResults();
        
        boolean hasFailures = results.stream().anyMatch(r -> r.getStatus() == TestStatus.FAIL || r.getStatus() == TestStatus.ERROR);
        assertTrue(hasFailures, "Should have fail or error results");
    }

    @Test
    void testNullFailingEquals() {
        ModelTester<NullFailingEqualsModel> tester = ModelTester.of(NullFailingEqualsModel.class).equalsMethod();
        assertFalse(tester.test()); // Should fail due to NullPointerException when passed null
        List<TestResult> results = tester.getResults();

        boolean hasErrors = results.stream().anyMatch(r -> r.getStatus() == TestStatus.ERROR && r.getError() instanceof NullPointerException);
        assertTrue(hasErrors, "Should catch NullPointerException as ERROR");
    }
}
