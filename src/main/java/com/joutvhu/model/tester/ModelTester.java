package com.joutvhu.model.tester;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The main entry point for testing model classes.
 * It provides a fluent API to configure and execute various tests on POJOs,
 * such as constructors, getters/setters, equals, hashCode, and toString.
 *
 * @param <T> The type of the model class to test.
 */
public class ModelTester<T> {
    private static final Logger log = LoggerFactory.getLogger(ModelTester.class);

    private final Class<T> modelClass;
    private final List<Tester> testers = new ArrayList<>();
    private final List<TestResult> results = new ArrayList<>();
    private NamingStrategy namingStrategy = NamingStrategy.DEFAULT;

    /**
     * Initializes a ModelTester for the specified domain class.
     * Throws an exception if the class is an interface.
     *
     * @param modelClass the class to be tested.
     */
    public ModelTester(Class<T> modelClass) {
        int mod = modelClass.getModifiers();
        if (Modifier.isInterface(mod))
            throw new TesterException("Can't test an interface.");
        this.modelClass = modelClass;
    }

    /**
     * Sets a custom naming strategy for matching fields and methods (e.g., for Java Records or fluent builders).
     *
     * @param namingStrategy the naming strategy to use.
     * @return this instance for chaining.
     */
    public ModelTester<T> withNamingStrategy(NamingStrategy namingStrategy) {
        this.namingStrategy = namingStrategy;
        return this;
    }

    /**
     * Factory method to create a ModelTester for the specified class.
     *
     * @param modelClass the class to test.
     * @param <T> type of the model.
     * @return a new ModelTester instance.
     */
    public static <T> ModelTester<T> of(Class<T> modelClass) {
        return new ModelTester<>(modelClass);
    }

    /**
     * Factory method to create a ModelTester and automatically add all standard tests:
     * constructors, getters/setters, equals, hashCode, and toString.
     *
     * @param modelClass the class to test.
     * @param <T> type of the model.
     * @return a configured ModelTester instance.
     */
    public static <T> ModelTester<T> allOf(Class<T> modelClass) {
        return new ModelTester<>(modelClass)
                .constructors()
                .getterSetters()
                .equalsMethod()
                .hashCodeMethod()
                .toStringMethod();
    }

    /**
     * Factory method to create a ModelTester with "safe" versions of equals, hashCode, and toString.
     * "Safe" checks verify consistency and null-safety but might avoid deep object comparison
     * if copy constructor is not available.
     *
     * @param modelClass the class to test.
     * @param <T> type of the model.
     * @return a configured ModelTester instance with safe tests.
     */
    public static <T> ModelTester<T> safeOf(Class<T> modelClass) {
        return new ModelTester<>(modelClass)
                .constructors()
                .getterSetters()
                .equalsSafe()
                .hashCodeSafe()
                .toStringSafe();
    }

    /**
     * Configures the tester to verify all constructors of the model class.
     *
     * @return this instance for chaining.
     */
    public ModelTester<T> constructors() {
        for (Creator<T> creatable : Creator.allOf(modelClass)) {
            testers.add(new ConstructorTester<>(creatable));
        }
        return this;
    }

    /**
     * Configures the tester to verify a constructor using specific parameter values.
     *
     * @param parameters raw values to pass to the constructor.
     * @return this instance for chaining.
     */
    public ModelTester<T> constructor(Object... parameters) {
        testers.add(new ConstructorTester<>(Creator.byParams(modelClass, parameters)));
        return this;
    }

    /**
     * Configures the tester to verify a constructor using specific {@link Creator} instances for parameters.
     *
     * @param parameters creators for construction parameters.
     * @return this instance for chaining.
     */
    public ModelTester<T> constructor(Creator<?>... parameters) {
        testers.add(new ConstructorTester<>(Creator.of(modelClass, parameters)));
        return this;
    }

    /**
     * Configures the tester to verify all getter and setter methods.
     *
     * @return this instance for chaining.
     */
    public ModelTester<T> getterSetters() {
        testers.add(new GetterSetterTester<>(modelClass, null, null).withNamingStrategy(namingStrategy));
        return this;
    }

    /**
     * Configures the tester to verify only specific getter/setter methods or fields.
     *
     * @param names names of the methods or fields to include.
     * @return this instance for chaining.
     */
    public ModelTester<T> include(String... names) {
        testers.add(new GetterSetterTester<>(modelClass, Arrays.asList(names), null).withNamingStrategy(namingStrategy));
        return this;
    }

    /**
     * Configures the tester to exclude specific getter/setter methods or fields from testing.
     *
     * @param names names of the methods or fields to exclude.
     * @return this instance for chaining.
     */
    public ModelTester<T> exclude(String... names) {
        testers.add(new GetterSetterTester<>(modelClass, null, Arrays.asList(names)).withNamingStrategy(namingStrategy));
        return this;
    }

    /**
     * Configures the tester to verify the {@link Object#equals(Object)} contract.
     *
     * @return this instance for chaining.
     */
    public ModelTester<T> equalsMethod() {
        testers.add(new EqualsTester<>(modelClass));
        return this;
    }

    /**
     * Configures the tester to verify the {@link Object#equals(Object)} contract in safe mode.
     *
     * @return this instance for chaining.
     */
    public ModelTester<T> equalsSafe() {
        testers.add(new EqualsTester<>(modelClass, true));
        return this;
    }

    /**
     * Configures the tester to verify the {@link Object#hashCode()} contract.
     *
     * @return this instance for chaining.
     */
    public ModelTester<T> hashCodeMethod() {
        testers.add(new HashCodeTester<>(modelClass));
        return this;
    }

    /**
     * Configures the tester to verify the {@link Object#hashCode()} contract in safe mode.
     *
     * @return this instance for chaining.
     */
    public ModelTester<T> hashCodeSafe() {
        testers.add(new HashCodeTester<>(modelClass, true));
        return this;
    }

    /**
     * Configures the tester to verify the {@link Object#toString()} contract.
     *
     * @return this instance for chaining.
     */
    public ModelTester<T> toStringMethod() {
        testers.add(new ToStringTester<>(modelClass));
        return this;
    }

    /**
     * Configures the tester to verify the {@link Object#toString()} contract in safe mode.
     *
     * @return this instance for chaining.
     */
    public ModelTester<T> toStringSafe() {
        testers.add(new ToStringTester<>(modelClass, true));
        return this;
    }

    /**
     * Executes all configured tests and logs the results.
     *
     * @return true if all tests passed, false otherwise.
     */
    public boolean test() {
        results.clear();
        for (Tester tester : testers) {
            results.addAll(tester.test());
        }
        boolean success = results.stream().allMatch(r -> r.getStatus() == TestStatus.PASS);
        if (success) {
            log.info("Test passed for class <{}>", modelClass.getName());
        } else {
            log.error("Test failed for class <{}>", modelClass.getName());
            results.stream()
                    .filter(r -> r.getStatus() != TestStatus.PASS)
                    .forEach(r -> log.error("  - {}", r));
        }
        return success;
    }

    /**
     * Retrieves the results of the executed tests.
     *
     * @return a list of {@link TestResult} containing test details.
     */
    public List<TestResult> getResults() {
        return results;
    }

    /**
     * Executes all configured tests and throws a {@link TesterException} if any test fails or errors.
     * Use this in test suites (e.g., JUnit) to enforce build failures on regressions.
     *
     * @throws TesterException if any test fails.
     */
    public void testAndThrows() {
        if (!test()) {
            throw new TesterException("Fail when testing class <" + modelClass.getName() + ">");
        }
    }
}
