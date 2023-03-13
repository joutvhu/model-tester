package com.joutvhu.model.tester;

import com.joutvhu.model.tester.domain.ModelA;
import com.joutvhu.model.tester.domain.ModelB;
import org.junit.jupiter.api.Test;

public class ModelTest {
    @Test
    public void test_ModelA() {
        ModelTester.allOf(ModelA.class).test();
    }

    @Test
    public void test_ModelB() {
        ModelTester.allOf(ModelB.class).testAndThrows();
    }
}
