package com.joutvhu.model.tester;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Represents the result of a single testing operation (e.g., verifying a specific setter).
 * Holds status, targeted class/component, and failure details.
 */
@Data
@Builder
@AllArgsConstructor
public class TestResult {
    /**
     * Default constructor for creating an empty test result.
     */
    public TestResult() {
    }

    /**
     * The simple or full name of the class under test.
     */
    private String className;
    /**
     * The specific component being tested (e.g., "setFirstName", "equals").
     */
    private String component;
    /**
     * The outcome of the test (PASS, FAIL, ERROR).
     */
    private TestStatus status;
    /**
     * A human-readable message explaining a failure or error.
     */
    private String message;
    /**
     * The exception encountered if the test status is ERROR.
     */
    private Throwable error;

    @Override
    public String toString() {
        return String.format("[%s] %s.%s: %s", status, className, component, message != null ? message : "");
    }
}
