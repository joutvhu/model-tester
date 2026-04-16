# Model Tester

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.joutvhu/model-tester.svg)](https://central.sonatype.com/artifact/com.github.joutvhu/model-tester)
[![Java](https://img.shields.io/badge/Java-8%2B-orange.svg)](https://www.oracle.com/java/)

**Model Tester** is a robust Java utility designed to eliminate boilerplate code when testing model classes (POJOs, Java Records, Lombok classes, etc.). It automatically verifies constructors, getters, setters, `equals()`, `hashCode()`, and `toString()` contracts with minimal effort—often in just one line of code.

---

## 🚀 Key Features

| Feature | Description |
| :--- | :--- |
| **Fluent API** | Highly readable and easily configurable method chaining. |
| **Java Records Support** | Tests Record accessors natively using `NamingStrategy.RECORD`. |
| **Fluent Setters** | Built-in strategy for fluent setters (`setXxx` returning `this`) via `NamingStrategy.FLUENT`. |
| **Deep Equality** | Recursive comparison engine for `List`, `Set`, `Map`, arrays, and primitive types. |
| **Auto Data Generation** | Automatically generates synthetic test data for all Java field types. |
| **Auto Proxying** | Automatically creates proxies for Interface and Abstract class dependencies. |
| **Thread-safe Cache** | Optimized reflection caching to minimize overhead in large test suites. |
| **Safe Mode** | Alternative "safe" testing methods for legacy or highly complex models. |
| **Detailed Reporting** | Structured granular testing outcomes via `TestResult` objects. |

---

## 📦 Installation

### Gradle

```groovy
dependencies {
    testImplementation "com.github.joutvhu:model-tester:1.1.1"
}
```

### Maven

```xml
<dependency>
    <groupId>com.github.joutvhu</groupId>
    <artifactId>model-tester</artifactId>
    <version>1.1.1</version>
    <scope>test</scope>
</dependency>
```

---

## ⚡ Quick Start

Test an entire standard POJO in just **one line of code**:

```java
@Test
void testUserModel() {
    ModelTester.allOf(User.class).testAndThrows();
}
```

`allOf()` automatically verifies everything: constructors, all getters/setters, `equals()`, `hashCode()`, and `toString()`.

---

## 🛠️ Detailed Usage Guide

### 1. Basic POJO Testing

Assuming you have the following `User` class:

```java
public class User {
    private Long id;
    private String name;
    private String email;

    public User() {}
    public User(Long id, String name, String email) { ... }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    // ... getters/setters, equals, hashCode, toString
}
```

**Approach 1 – Verify everything at once:**

```java
@Test
void testUser_allOf() {
    // Automatically tests: constructors, getters/setters, equals, hashCode, toString
    ModelTester.allOf(User.class).testAndThrows();
}
```

**Approach 2 – Granular control:**

```java
@Test
void testUser_step_by_step() {
    ModelTester.of(User.class)
        .constructors()       // Tests all public/private constructors
        .getterSetters()      // Tests field accessors and mutators
        .equalsMethod()       // Tests the equals() contract
        .hashCodeMethod()     // Tests the hashCode() contract
        .toStringMethod()     // Ensures toString() doesn't crash and is consistent
        .testAndThrows();
}
```

**Approach 3 – Return boolean instead of throwing:**

```java
@Test
void testUser_boolean() {
    boolean passed = ModelTester.allOf(User.class).test();
    Assertions.assertTrue(passed, "User model should pass all tests");
}
```

---

### 2. Testing Java Records

Java Records use accessors that match field names exactly (without `get`/`set` prefixes). Use `NamingStrategy.RECORD` to handle them.

```java
// Record definition
public record ProductRecord(Long id, String name, List<String> tags, Map<String, Object> metadata) {}
```

```java
@Test
void testProductRecord() {
    ModelTester.of(ProductRecord.class)
        .withNamingStrategy(NamingStrategy.RECORD)
        .constructors()
        .getterSetters()   // Tests accessors: id(), name(), tags(), metadata()
        .equalsMethod()
        .hashCodeMethod()
        .toStringMethod()
        .testAndThrows();
}
```

> **Note:** Records do not have setters, so `getterSetters()` only verifies the accessors (getters). The library automatically handles this when using `NamingStrategy.RECORD`.

---

### 3. Testing Fluent Setters / Builders

A fluent setter returns `this` instead of `void`, enabling method chaining.

```java
// Using Lombok's @Accessors(chain = true)
@Data
@Accessors(chain = true)
public class Article {
    private Long id;
    private String title;
    private String content;
    private boolean published;
}
```

```java
@Test
void testFluentArticle() {
    ModelTester.of(Article.class)
        .withNamingStrategy(NamingStrategy.FLUENT)
        .constructors()
        .getterSetters()
        .equalsMethod()
        .hashCodeMethod()
        .toStringMethod()
        .testAndThrows();
}
```

The library verifies that a fluent setter:
1. Assigns the correct value to the field.
2. Returns the correct `this` reference (not a different object instance).

---

### 4. Inclusion and Exclusion

Use `include()` to selectively test specific fields or methods:

```java
@Test
void testUser_onlyPublicFields() {
    ModelTester.of(User.class)
        .include("id", "name", "email")  // Only tests these three fields
        .testAndThrows();
}
```

Use `exclude()` to skip sensitive or non-standard fields:

```java
@Test
void testUser_excludeSensitiveFields() {
    ModelTester.of(User.class)
        .exclude("passwordHash", "salt", "secretToken")  // Skips these fields
        .testAndThrows();
}
```

> **Note:** `include()` and `exclude()` only apply to the `getterSetters()` tests. They do not affect `constructors()`, `equalsMethod()`, `hashCodeMethod()`, or `toStringMethod()`.

---

### 5. Custom Parameterized Constructors

When your model requires specific constructor arguments or constraints:

**Pass explicit parameter values:**

```java
@Test
void testUser_withCustomConstructor() {
    ModelTester.of(User.class)
        .constructor(100L, "joutvhu", "joutvhu@example.com")  // Uses the 3-param constructor
        .getterSetters()
        .testAndThrows();
}
```

**Use internal `Creator` instances for fine-grained control:**

```java
@Test
void testOrder_withCreator() {
    ModelTester.of(Order.class)
        .constructor(
            Creator.anyOf(Long.class),       // Auto-generates a Long
            Creator.byParams(String.class, "ORDER-001")  // Fixed value
        )
        .getterSetters()
        .testAndThrows();
}
```

**Test all discovered constructors at once:**

```java
@Test
void testUser_allConstructors() {
    ModelTester.of(User.class)
        .constructors()   // Discovers and tests every constructor
        .testAndThrows();
}
```

---

### 6. Testing Class Inheritance

`ModelTester` seamlessly validates fields and methods declared in parent classes:

```java
// Parent class
public abstract class BaseEntity {
    private Long id;
    private Date createdAt;
    // getters/setters...
}

// Child class
public class Customer extends BaseEntity {
    private String fullName;
    private String phone;
    // getters/setters, equals, hashCode, toString...
}
```

```java
@Test
void testCustomer_withInheritance() {
    ModelTester.allOf(Customer.class).testAndThrows();
    // Tests inherited id, createdAt PLUS fullName, phone
}
```

---

### 7. Testing Enums

The library can fully validate Enum types by cycling through their available values:

```java
public enum Status {
    ACTIVE, INACTIVE, PENDING;

    private final String label;

    Status() { this.label = name().toLowerCase(); }

    public String getLabel() { return label; }
}
```

```java
@Test
void testStatus_enum() {
    ModelTester.allOf(Status.class).testAndThrows();
}
```

---

### 8. Handling Complex Dependencies (Interfaces & Abstracts)

If your model has fields that are Interfaces or Abstract classes, `ModelTester` **automatically creates proxies** for them:

```java
public class OrderService {
    private PaymentGateway gateway;    // Interface
    private AbstractProcessor processor;  // Abstract class

    public PaymentGateway getGateway() { return gateway; }
    public void setGateway(PaymentGateway gateway) { this.gateway = gateway; }
    // ...
}
```

```java
@Test
void testOrderService_autoProxy() {
    // Automatically creates proxies for PaymentGateway and AbstractProcessor
    ModelTester.allOf(OrderService.class).testAndThrows();
}
```

Alternatively, inject your own mock objects:

```java
@Test
void testOrderService_withMock() {
    PaymentGateway mockGateway = mock(PaymentGateway.class);  // Mockito mock

    ModelTester.of(OrderService.class)
        .constructor(mockGateway)   // Inject mock via constructor
        .getterSetters()
        .testAndThrows();
}
```

---

### 9. Safe Mode – For Legacy or Complex Models

When dealing with large, legacy models where a strict contract test fails (e.g., due to missing copy constructors or complex circular dependencies), use the `Safe` methods to ensure basic consistency and null-safety without rigorous deep equality checks.

```java
@Test
void testLegacyModel_safeShortcut() {
    // safeOf() = constructors() + getterSetters() + equalsSafe() + hashCodeSafe() + toStringSafe()
    ModelTester.safeOf(LegacyCustomer.class).testAndThrows();
}
```

Or mix and match modes:

```java
@Test
void testLegacyModel_mixedMode() {
    ModelTester.of(LegacyCustomer.class)
        .constructors()
        .getterSetters()      // Standard rigorous verification
        .equalsSafe()         // Only tests equals(itself) and equals(null)
        .hashCodeSafe()       // Tests that hashCode() implies internal consistency
        .toStringSafe()       // Tests that toString() executes successfully
        .testAndThrows();
}
```

**Understanding Strict vs Safe Checks:**

| Method | Verification Logic |
| :--- | :--- |
| `equalsMethod()` | Reflexivity, null-safety, symmetry with copy, and deep field-by-field inequality detection. |
| `equalsSafe()` | Only reflexivity (`a.equals(a)`) and null-safety (`a.equals(null) == false`). |
| `hashCodeMethod()` | `hashCode(original) == hashCode(copy)` ensures parity across objects with identical states. |
| `hashCodeSafe()` | `hashCode()` on the same object returns the same consistent value. |
| `toStringMethod()` | `toString(original) == toString(copy)`. |
| `toStringSafe()` | Calling `toString()` does not throw an exception and is consistent. |

---

### 10. Framework Integrations

**JUnit 5 – Recommended approach:**

```java
@Test
void testUser_junit5() {
    // testAndThrows() throws a TesterException on first failure, failing the JUnit test immediately.
    ModelTester.allOf(User.class).testAndThrows();
}
```

**JUnit 5 – assertDoesNotThrow:**

```java
@Test
void testUser_assertDoesNotThrow() {
    assertDoesNotThrow(
        () -> ModelTester.allOf(User.class).testAndThrows(),
        "User model should pass all standard tests"
    );
}
```

**JUnit 4:**

```java
@org.junit.Test
public void testUser_junit4() {
    ModelTester.allOf(User.class).testAndThrows();
}
```

**TestNG:**

```java
@org.testng.annotations.Test
public void testUser_testng() {
    ModelTester.allOf(User.class).testAndThrows();
}
```

---

## 📊 Handling Test Results (TestResult)

While `.test()` provides a simple boolean summary, you can inspect granular component-level outcomes using `.getResults()`.

### `TestResult` Structure

| Field | Type | Description |
| :--- | :--- | :--- |
| `className` | `String` | Fully qualified name of the class tested |
| `component` | `String` | The field or method failing the test (e.g., `setName`, `equals(copy)`) |
| `status` | `TestStatus` | Enum value: `PASS`, `FAIL`, or `ERROR` |
| `message` | `String` | Human-readable explanation of a failure |
| `error` | `Throwable` | The root exception (present only when `status == ERROR`) |

### `TestStatus` Meanings

| Status | Meaning | Typical Cause |
| :--- | :--- | :--- |
| `PASS` | Success | The component functions exactly as expected. |
| `FAIL` | Logic Failure | An assertion failed (e.g., `getName()` didn't return the value set by `setName()`). |
| `ERROR` | Technical Error | An unexpected exception occurred (e.g., a crash inside your model during instantiation). |

### Example: Custom Soft Reporting

If you want to view exactly which getters/setters failed without aborting immediately:

```java
@Test
void testUser_customReporting() {
    ModelTester<User> tester = ModelTester.allOf(User.class);
    boolean success = tester.test();

    if (!success) {
        List<TestResult> results = tester.getResults();

        System.err.println("=== FAILED COMPONENTS ===");
        results.stream()
            .filter(r -> r.getStatus() != TestStatus.PASS)
            .forEach(result -> {
                System.err.printf("[%s] %s.%s: %s%n",
                    result.getStatus(),
                    result.getClassName(),
                    result.getComponent(),
                    result.getMessage() != null ? result.getMessage() : "(no message)");

                if (result.getError() != null) {
                    result.getError().printStackTrace(System.err);
                }
            });

        Assertions.fail("Model test failed. See FAILED COMPONENTS above.");
    }
}
```

### Example: Classifying Results

```java
@Test
void testUser_classifyResults() {
    ModelTester<User> tester = ModelTester.allOf(User.class);
    tester.test();

    List<TestResult> results = tester.getResults();

    long passCount  = results.stream().filter(r -> r.getStatus() == TestStatus.PASS).count();
    long failCount  = results.stream().filter(r -> r.getStatus() == TestStatus.FAIL).count();
    long errorCount = results.stream().filter(r -> r.getStatus() == TestStatus.ERROR).count();

    System.out.printf("PASS: %d | FAIL: %d | ERROR: %d%n", passCount, failCount, errorCount);

    // Assert that no technical exceptions occurred, even if logic fails exists
    Assertions.assertEquals(0, errorCount, "Ensure no ERRORs occurred during tests");
}
```

---

## ⚙️ API Reference

### Initializer Factories

| Method | Description |
| :--- | :--- |
| `ModelTester.of(Class<T>)` | Creates an empty tester allowing full manual configuration. |
| `ModelTester.allOf(Class<T>)` | Automatically configures: `constructors()`, `getterSetters()`, `equalsMethod()`, `hashCodeMethod()`, `toStringMethod()`. |
| `ModelTester.safeOf(Class<T>)` | Configures safe operations: `constructors()`, `getterSetters()`, `equalsSafe()`, `hashCodeSafe()`, `toStringSafe()`. |

### NamingStrategy Configuration

| Method | Description |
| :--- | :--- |
| `withNamingStrategy(NamingStrategy.DEFAULT)` | Standard POJOs: `getXxx()`, `setXxx()`, `isXxx()` (default behavior). |
| `withNamingStrategy(NamingStrategy.RECORD)` | Java Records: Accessors match field names perfectly, no setters allowed. |
| `withNamingStrategy(NamingStrategy.FLUENT)` | Fluent APIs: Setters return `this` instance rather than `void`. |

### Execution Restrictions

| Method | Description |
| :--- | :--- |
| `include("field1", "field2")` | Restricts getter/setter testing to exclusively these fields or methods. |
| `exclude("field1", "field2")` | Explicitly ignores these fields or methods during getter/setter testing. |

### Component Verification Checks

| Method | Target tested |
| :--- | :--- |
| `constructors()` | Discovers and validates all declared class constructors. |
| `constructor(Object... params)` | Forces tests to use a constructor with specific argument values. |
| `constructor(Creator<?>... params)` | Uses highly configurable parameterized Creator generators. |
| `getterSetters()` | Exercises all field accessors and mutators per the `NamingStrategy`. |
| `equalsMethod()` | Performs strict, comprehensive verification of the `equals()` contract. |
| `equalsSafe()` | Basic `equals()` null-safety and reflexivity. |
| `hashCodeMethod()` | Strict `hashCode()` state parity across objects. |
| `hashCodeSafe()` | Basic non-crashing execution parity for `hashCode()`. |
| `toStringMethod()` | Strict `toString()` state parity across identically stateful objects. |
| `toStringSafe()` | Basic non-crashing execution parity for `toString()`. |

### Execution & Results

| Method | Returns | Description |
| :--- | :--- | :--- |
| `test()` | `boolean` | Executes all queued logic, returning `true` if completely successful. |
| `testAndThrows()` | `void` | Executes all logic, throwing a `TesterException` on the first test failure. |
| `getResults()` | `List<TestResult>` | Retrieve detailed granularity records following a `.test()` execution. |

---

## 🔬 Internal Mechanics

### Auto Data Generation (`Creator`)

The `Creator` utility automatically generates appropriate test data for your domain types during tests:

| Target Type | Generated Value |
| :--- | :--- |
| `String` | `""` (Empty string) |
| `int`, `Integer` | `0` |
| `long`, `Long` | `0L` |
| `boolean`, `Boolean` | `true` |
| `char`, `Character` | `'c'` |
| `double`, `Double` | `0.0` |
| `float`, `Float` | `0.0f` |
| `BigInteger`, `BigDecimal` | `0` |
| `List`, `Collection` | `new ArrayList<>()` |
| `Set` | `new HashSet<>()` |
| `Map` | `new HashMap<>()` |
| `Instant`, `Temporal` | `Instant.now()` |
| `SortedSet`, `NavigableSet` | `new TreeSet<>()` |
| `SortedMap`, `NavigableMap` | `new TreeMap<>()` |
| Arrays | Empty array of the component type |
| Enums | The first declared constant |
| Interfaces | JDK Dynamic Runtime Proxy |
| Abstract Classes | Javassist Dynamic Proxy |

### Getter / Setter Internal Verification

For every inferred Property Pair:
1. Generates test data identical to the field's type.
2. Directly bypasses access constraints via reflection to set the field.
3. Invokes the Getter and confirms it intercepts the newly seeded value.
4. Conversely, invokes the Setter utilizing new generated test data and subsequently inspects the raw underlying field to verify the Setter's operational success.

### `equals()` Internal Verification

1. **Reflexivity**: `obj.equals(obj)` must evaluate to `true`.
2. **Null-safety**: `obj.equals(null)` must reliably evaluate to `false`.
3. **Symmetry with copy**: `obj.equals(copy)` must evaluate to `true` when copy maintains identical state.
4. **Field sensitivity**: Iteratively alters individual field states via reflection inside the copied object, strictly demanding that `obj.equals(mutatedCopy)` evaluates to `false`.

### `hashCode()` Internal Verification

- **Strict mode**: Asserts that `hashCode(original) == hashCode(copy)`, guaranteeing two identitical domain objects provide equivalent digest hashes.
- **Safe mode**: Asserts two consecutive invocations of `hashCode()` on a singular unchanged object provides identical hashes.

---

## 💡 Pro Tips

### Tip 1: Coupling with Lombok

Lombok eliminates the writing of boilerplate code, and `ModelTester` guarantees that no Lombok annotations are mistakenly missed or malformed over the lifespan of a project.

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Long id;
    private String name;
    private BigDecimal price;
}
```

```java
@Test
void testProduct_lombok() {
    ModelTester.allOf(Product.class).testAndThrows();
}
```

### Tip 2: Bulk Testing Application Models

Reduce repetitive unit testing code by looping your application's domain model directory:

```java
@Test
void testAllDomainModels() {
    List<Class<?>> models = List.of(
        User.class, Product.class, Order.class,
        Category.class, Address.class
    );

    models.forEach(modelClass ->
        ModelTester.allOf(modelClass).testAndThrows()
    );
}
```

### Tip 3: Exploiting JUnit 5 `@ParameterizedTest`

Combine `ModelTester` with parameterized configurations to easily run automated class tests:

```java
@ParameterizedTest
@ValueSource(classes = {User.class, Product.class, Order.class})
void testDomainModels(Class<?> modelClass) {
    ModelTester.allOf(modelClass).testAndThrows();
}
```

### Tip 4: Preserving Hard Domain Constraints

If your business domain enforces defensive constructors (e.g. enforcing positive integers or forbidding null initialization), leverage the `constructor` injection pattern:

```java
@Test
void testInventoryItem_withConstraints() {
    // Guarantees quantity >= 0 initially to bypass validation safeguards
    ModelTester.of(InventoryItem.class)
        .constructor(1L, "SKU-001", 10)   // itemId, sku, quantity
        .getterSetters()
        .testAndThrows();
}
```

### Tip 5: Testing Models Containing Nested Objects

```java
public class Invoice {
    private Long id;
    private Customer customer;       // Deep Nested POJO
    private List<InvoiceLine> lines; // Deep Nested Collections
    // ...
}
```

```java
@Test
void testInvoice_withNestedModels() {
    // The internal Creator automatically recursively analyzes and generates instances
    // for Customer and InvoiceLine when building the initial test Invoice state.
    ModelTester.allOf(Invoice.class).testAndThrows();
}
```

---

## 📋 Changelog

Review the [CHANGELOG.md](CHANGELOG.md) to explore granular historical progression timelines and iteration details.

---

## 📄 License

This software project is directly sourced and distributed strictly under the structural governance provided by the [MIT License](LICENSE).
