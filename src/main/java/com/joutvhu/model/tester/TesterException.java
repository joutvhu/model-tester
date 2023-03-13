package com.joutvhu.model.tester;

public class TesterException extends RuntimeException {
    public TesterException(String message) {
        super(message);
    }

    public TesterException(String message, Throwable cause) {
        super(message, cause);
    }

    public TesterException(Throwable cause) {
        super(cause);
    }

    public TesterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
