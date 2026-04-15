package com.joutvhu.model.tester;

/**
 * Enumeration of possible test outcomes.
 */
public enum TestStatus {
    /**
     * Test completed successfully.
     */
    PASS,
    /**
     * Test failed due to a mismatch or logic error.
     */
    FAIL,
    /**
     * Test failed due to an unexpected exception.
     */
    ERROR
}
