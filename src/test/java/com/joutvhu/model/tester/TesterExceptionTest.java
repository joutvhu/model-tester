package com.joutvhu.model.tester;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TesterExceptionTest {

    @Test
    void testMessageConstructor() {
        TesterException exception = new TesterException("Custom message");
        assertEquals("Custom message", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testCauseConstructor() {
        Throwable cause = new RuntimeException("Underlying cause");
        TesterException exception = new TesterException(cause);
        
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testMessageAndCauseConstructor() {
        Throwable cause = new RuntimeException("Underlying cause");
        TesterException exception = new TesterException("Message", cause);
        
        assertEquals("Message", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testFullConstructor() {
        Throwable cause = new RuntimeException("Underlying cause");
        TesterException exception = new TesterException("Message", cause, true, false);
        
        assertEquals("Message", exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(0, exception.getSuppressed().length);
    }
}
