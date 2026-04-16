package com.joutvhu.model.tester;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestResultTest {

    @Test
    void testTestResultBuilder() {
        Throwable t = new RuntimeException("Test Exception");
        
        TestResult result = TestResult.builder()
            .className("MyClass")
            .component("MyComponent")
            .status(TestStatus.FAIL)
            .message("It failed")
            .error(t)
            .build();
            
        assertEquals("MyClass", result.getClassName());
        assertEquals("MyComponent", result.getComponent());
        assertEquals(TestStatus.FAIL, result.getStatus());
        assertEquals("It failed", result.getMessage());
        assertEquals(t, result.getError());
    }

    @Test
    void testTestResultToString() {
        TestResult resultPass = new TestResult("MyClass", "myMethod", TestStatus.PASS, "Success", null);
        assertEquals("[PASS] MyClass.myMethod: Success", resultPass.toString());

        TestResult resultError = new TestResult("Object", "equals", TestStatus.ERROR, null, new NullPointerException());
        assertEquals("[ERROR] Object.equals: ", resultError.toString());
    }
}
