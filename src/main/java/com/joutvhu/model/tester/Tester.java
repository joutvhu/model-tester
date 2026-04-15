package com.joutvhu.model.tester;

import java.util.List;

/**
 * Base interface for specialized model testers (Constructor, Getter/Setter, Equals, etc.).
 */
interface Tester {
    /**
     * Executes the test logic for a specific model aspect.
     *
     * @return a list of {@link TestResult} objects detailing the outcomes
     */
    List<TestResult> test();
}
