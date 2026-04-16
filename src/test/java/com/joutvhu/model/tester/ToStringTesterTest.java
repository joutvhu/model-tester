package com.joutvhu.model.tester;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ToStringTesterTest {

    public static class ErrorToStringModel {
        public ErrorToStringModel() {}
        @Override
        public String toString() {
            throw new RuntimeException("toString error");
        }
    }

    @Test
    void testErrorToString() {
        ModelTester<ErrorToStringModel> tester = ModelTester.of(ErrorToStringModel.class).toStringMethod();
        assertFalse(tester.test()); // Should catch error
        
        List<TestResult> results = tester.getResults();
        
        boolean hasErrors = results.stream().anyMatch(r -> r.getStatus() == TestStatus.ERROR);
        assertTrue(hasErrors, "Should capture an ERROR result when toString throws exception");
    }
}
