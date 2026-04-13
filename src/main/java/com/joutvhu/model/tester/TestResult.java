package com.joutvhu.model.tester;

import lombok.Builder;
import lombok.Data;

/**
 * Represents the result of a single test component.
 */
@Data
@Builder
public class TestResult {
    private String className;
    private String component;
    private TestStatus status;
    private String message;
    private Throwable error;

    @Override
    public String toString() {
        return String.format("[%s] %s.%s: %s", status, className, component, message != null ? message : "");
    }
}
