package com.joutvhu.model.tester;

import com.joutvhu.model.tester.domain.AbstractMapModel;
import com.joutvhu.model.tester.domain.CommonsLangModel;
import com.joutvhu.model.tester.domain.GuavaDictionaryModel;
import com.joutvhu.model.tester.domain.InheritanceModel;
import com.joutvhu.model.tester.domain.ModelFluent;
import com.joutvhu.model.tester.domain.ModelRecord;
import com.joutvhu.model.tester.domain.NestedListModel;
import com.joutvhu.model.tester.domain.ReflectionModel;
import com.joutvhu.model.tester.domain.SampleEnum;
import com.joutvhu.model.tester.domain.SimplePojo;
import org.junit.jupiter.api.Test;

public class ModelTest {
    @Test
    public void test_SimplePojo() {
        ModelTester.allOf(SimplePojo.class).testAndThrows();
    }

    @Test
    public void test_NestedListModel() {
        ModelTester.allOf(NestedListModel.class).testAndThrows();
    }

    @Test
    public void test_AbstractMapModel() {
        ModelTester.allOf(AbstractMapModel.class).testAndThrows();
    }

    @Test
    public void test_InheritanceModel() {
        ModelTester.allOf(InheritanceModel.class).testAndThrows();
    }

    @Test
    public void test_CommonsLangModel() {
        ModelTester.safeOf(CommonsLangModel.class).testAndThrows();
    }

    @Test
    public void test_GuavaDictionaryModel() {
        ModelTester.allOf(GuavaDictionaryModel.class).testAndThrows();
    }

    @Test
    public void test_ReflectionModel() {
        ModelTester.safeOf(ReflectionModel.class).testAndThrows();
    }

    @Test
    public void test_SampleEnum() {
        ModelTester.allOf(SampleEnum.class).testAndThrows();
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
