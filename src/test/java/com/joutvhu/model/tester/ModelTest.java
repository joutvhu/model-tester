package com.joutvhu.model.tester;

import com.joutvhu.model.tester.domain.ModelA;
import com.joutvhu.model.tester.domain.ModelB;
import com.joutvhu.model.tester.domain.ModelC;
import com.joutvhu.model.tester.domain.ModelD;
import com.joutvhu.model.tester.domain.ModelE;
import com.joutvhu.model.tester.domain.ModelF;
import com.joutvhu.model.tester.domain.ModelS;
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

    @Test
    public void test_ModelE() {
        ModelTester.safeOf(ModelE.class).testAndThrows();
    }

    @Test
    public void test_ModelF() {
        ModelTester.allOf(ModelF.class).testAndThrows();
    }

    @Test
    public void test_ModelS() {
        ModelTester.safeOf(ModelS.class).testAndThrows();
    }
}
