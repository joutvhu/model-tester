package com.joutvhu.model.tester;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TestResult {
    private final String className;
    private final String component;
    private final TestStatus status;
    private final String message;
    private final Throwable error;

    @Override
    public String toString() {
        return String.format("[%s] %s.%s: %s", status, className, component, message != null ? message : "");
    }
}
