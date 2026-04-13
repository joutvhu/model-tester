package com.joutvhu.model.tester;

import com.joutvhu.model.tester.domain.EnumX;
import com.joutvhu.model.tester.domain.ModelA;
import com.joutvhu.model.tester.domain.ModelB;
import com.joutvhu.model.tester.domain.ModelC;
import com.joutvhu.model.tester.domain.ModelD;
import com.joutvhu.model.tester.domain.ModelE;
import com.joutvhu.model.tester.domain.ModelF;
import com.joutvhu.model.tester.domain.ModelFluent;
import com.joutvhu.model.tester.domain.ModelRecord;
import com.joutvhu.model.tester.domain.ModelS;
import org.junit.jupiter.api.Test;

public class ModelTest {
    @Test
    public void test_ModelA() {
        ModelTester.allOf(ModelA.class).testAndThrows();
    }

    @Test
    public void test_ModelB() {
        ModelTester.allOf(ModelB.class).testAndThrows();
    }

    @Test
    public void test_ModelC() {
        ModelTester.allOf(ModelC.class).testAndThrows();
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

    @Test
    public void test_EnumX() {
        ModelTester.allOf(EnumX.class).testAndThrows();
    }

    @Test
    public void test_ModelRecord() {
        ModelTester.of(ModelRecord.class)
                .withNamingStrategy(NamingStrategy.RECORD)
                .constructors()
                .getterSetters()
                .equalsMethod()
                .hashCodeMethod()
                .toStringMethod()
                .testAndThrows();
    }

    @Test
    public void test_ModelFluent() {
        ModelTester.of(ModelFluent.class)
                .withNamingStrategy(NamingStrategy.FLUENT)
                .constructors()
                .getterSetters()
                .equalsMethod()
                .hashCodeMethod()
                .toStringMethod()
                .testAndThrows();
    }
}
