package com.joutvhu.model.tester;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelTester<T> {
    private final Class<T> modelClass;
    private final List<Tester> testers = new ArrayList<>();

    public ModelTester(Class<T> modelClass) {
        int mod = modelClass.getModifiers();
        if (Modifier.isInterface(mod))
            throw new TesterException("Can't test an interface.");
        if (Modifier.isAbstract(mod))
            throw new TesterException("Can't test an abstract class.");
        this.modelClass = modelClass;
    }

    public static <T> ModelTester<T> of(Class<T> modelClass) {
        return new ModelTester<>(modelClass);
    }

    public static <T> ModelTester<T> allOf(Class<T> modelClass) {
        return new ModelTester<>(modelClass)
                .allConstructor()
                .getterSetters()
                .equalsMethod()
                .hashCodeMethod()
                .toStringMethod();
    }

    /**
     * Should test all constructors.
     */
    public ModelTester<T> allConstructor() {
        for (Constructor<?> constructor : modelClass.getConstructors()) {
            testers.add(new ConstructorTester<>(Creator.of(constructor)));
        }
        return this;
    }

    public ModelTester<T> constructor(Object... parameters) {
        testers.add(new ConstructorTester<>(Creator.byParams(modelClass, parameters)));
        return this;
    }

    public ModelTester<T> constructor(Creator<?>... parameters) {
        testers.add(new ConstructorTester<>(Creator.of(modelClass, parameters)));
        return this;
    }

    /**
     * Should test all getters and setters methods.
     */
    public ModelTester<T> getterSetters() {
        testers.add(new GetterSetterTester<>(modelClass, null, null));
        return this;
    }

    public ModelTester<T> include(String... fields) {
        testers.add(new GetterSetterTester<>(modelClass, Arrays.asList(fields), null));
        return this;
    }

    public ModelTester<T> exclude(String... fields) {
        testers.add(new GetterSetterTester<>(modelClass, null, Arrays.asList(fields)));
        return this;
    }

    /**
     * Should test equals() method
     */
    public ModelTester<T> equalsMethod() {
        testers.add(new EqualsTester<>(modelClass));
        return this;
    }

    /**
     * Should test hashCode() method
     */
    public ModelTester<T> hashCodeMethod() {
        testers.add(new HashCodeTester<>(modelClass));
        return this;
    }

    /**
     * Should test toString() method
     */
    public ModelTester<T> toStringMethod() {
        testers.add(new ToStringTester<>(modelClass));
        return this;
    }

    /**
     * Start testing
     *
     * @return false if there is any error.
     */
    public boolean test() {
        boolean success = true;
        for (Tester tester : testers) {
            boolean result = tester.test();
            if (success) success = result;
        }
        return success;
    }

    /**
     * Start testing and throws Exception if there is any error.
     */
    public void testAndThrows() {
        if (!test()) {
            throw new TesterException("Fail when testing class <" + modelClass.getName() + ">");
        }
    }
}
