package com.joutvhu.model.tester;

import com.joutvhu.model.tester.domain.ModelA;
import com.joutvhu.model.tester.domain.ModelB;
import com.joutvhu.model.tester.domain.ModelC;
import com.joutvhu.model.tester.domain.ModelD;
import org.junit.jupiter.api.Assertions;
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

    @Test
    public void test_ModelC() {
        Assertions.assertThrows(Exception.class, () -> {
            ModelTester.allOf(ModelC.class).test();
        });
    }

    @Test
    public void test_ModelD() {
        ModelTester.allOf(ModelD.class).testAndThrows();
    }
}
