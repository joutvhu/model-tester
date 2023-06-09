package com.joutvhu.model.tester;

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
        this.modelClass = modelClass;
    }

    public static <T> ModelTester<T> of(Class<T> modelClass) {
        return new ModelTester<>(modelClass);
    }

    public static <T> ModelTester<T> allOf(Class<T> modelClass) {
        return new ModelTester<>(modelClass)
                .constructors()
                .getterSetters()
                .equalsMethod()
                .hashCodeMethod()
                .toStringMethod();
    }

    public static <T> ModelTester<T> safeOf(Class<T> modelClass) {
        return new ModelTester<>(modelClass)
                .constructors()
                .getterSetters()
                .equalsSafe()
                .hashCodeSafe()
                .toStringSafe();
    }

    /**
     * Should test all constructors.
     */
    public ModelTester<T> constructors() {
        for (Creator<T> creatable : Creator.allOf(modelClass)) {
            testers.add(new ConstructorTester<>(creatable));
        }
        return this;
    }

    /**
     * Should test a constructor with parameters.
     */
    public ModelTester<T> constructor(Object... parameters) {
        testers.add(new ConstructorTester<>(Creator.byParams(modelClass, parameters)));
        return this;
    }

    public ModelTester<T> constructor(Creator<?>... parameters) {
        testers.add(new ConstructorTester<>(Creator.of(modelClass, parameters)));
        return this;
    }

    /**
     * Should test all getter and setter methods.
     */
    public ModelTester<T> getterSetters() {
        testers.add(new GetterSetterTester<>(modelClass, null, null));
        return this;
    }

    /**
     * Only getters setters are listed.
     */
    public ModelTester<T> include(String... names) {
        testers.add(new GetterSetterTester<>(modelClass, Arrays.asList(names), null));
        return this;
    }

    /**
     * Except for the getter setter methods listed.
     */
    public ModelTester<T> exclude(String... names) {
        testers.add(new GetterSetterTester<>(modelClass, null, Arrays.asList(names)));
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
     * Test equals() but compare with itself
     */
    public ModelTester<T> equalsSafe() {
        testers.add(new EqualsTester<>(modelClass, true));
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
     * Test hashCode() but compare with itself
     */
    public ModelTester<T> hashCodeSafe() {
        testers.add(new HashCodeTester<>(modelClass, true));
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
     * Test toString() but compare with itself
     */
    public ModelTester<T> toStringSafe() {
        testers.add(new ToStringTester<>(modelClass, true));
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
