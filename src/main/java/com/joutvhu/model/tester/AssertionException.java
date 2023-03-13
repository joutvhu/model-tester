package com.joutvhu.model.tester;

import lombok.Getter;

@Getter
public class AssertionException extends TesterException {
    private Object expected;
    private Object actual;

    public AssertionException(String message) {
        super(message);
    }

    public AssertionException(String message, Object expected, Object actual) {
        super(message);
        this.expected = expected;
        this.actual = actual;
    }
}
