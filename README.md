# Model Tester

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

Model Tester is a robust Java utility designed to eliminate boilerplate code when testing model classes (POJOs, Java Records, etc.). It automatically verifies constructors, getters, setters, `equals()`, `hashCode()`, and `toString()` contracts with minimal effort.

---

## 🚀 Key Features

*   **Automated Testing**: One-line testing for standard model methods.
*   **Modern Java Support**: Full support for standard POJOs, Lombok-generated code, and **Java Records**.
*   **Fluent API Support**: Built-in strategy for fluent setters/builders (where `setXxx` returns `this`).
*   **Deep Equality**: Enhanced assertion engine that recursively compares `List`, `Set`, and `Map`.
*   **Performance Optimized**: Thread-safe reflection cache minimizes performance overhead in large test suites.
*   **Detailed Diagnostics**: Structured reporting with exact field/method failure messages.
*   **Flexible Inclusion**: Group tests, exclude specific fields or internal methods easily.

---

## 📦 Installation

### Gradle
```groovy
testImplementation "com.github.joutvhu:model-tester:1.0.5"
// Recommended: add an SLF4J binding for test logs
testRuntimeOnly "org.slf4j:slf4j-simple:2.0.7"
```

### Maven
```xml
<dependency>
    <groupId>com.github.joutvhu</groupId>
    <artifactId>model-tester</artifactId>
    <version>1.0.5</version>
    <scope>test</scope>
</dependency>
```

---

## 🛠️ Usage Examples

### 1. Basic POJO Testing
Verify everything (constructors, getters, setters, equals, hashCode, toString) in one line.

```java
@Test
void testUserModel() {
    // Returns true if all tests pass
    Assertions.assertTrue(ModelTester.allOf(User.class).test());
    
    // OR: Throw a TesterException on first failure (recommended for CI)
    ModelTester.allOf(User.class).testAndThrows();
}
```

### 2. Testing Java Records
Records don't follow standard `getXxx` conventions. Use `NamingStrategy.RECORD`.

```java
@Test
void testRecord() {
    ModelTester.of(MyRecord.class)
            .withNamingStrategy(NamingStrategy.RECORD)
            .constructors()
            .getterSetter() // Checks record accessors
            .equalsMethod()
            .hashCodeMethod()
            .testAndThrows();
}
```

### 3. Fluent Setters & Exclusions
If your model has setters like `setName(String name)` that return `this`, use `NamingStrategy.FLUENT`.

```java
@Test
void testFluentModel() {
    ModelTester.of(FluentUser.class)
            .withNamingStrategy(NamingStrategy.FLUENT)
            .include("id", "username", "email") // Only test these
            .exclude("passwordHash")             // Explicitly skip this
            .getterSetter()
            .testAndThrows();
}
```

### 4. Custom Parameterized Constructors
You can specify exactly how `ModelTester` should instantiate your class.

```java
@Test
void testCustomConstructor() {
    ModelTester.of(User.class)
            // Specify custom values for a constructor
            .constructor(Long.class, String.class) 
            .withParams(1L, "joutvhu")
            .getterSetter()
            .testAndThrows();
}
```

---

## ⚙️ Configuration Options

| Option | Description |
| :--- | :--- |
| `withNamingStrategy(Strategy)` | Switch between `DEFAULT`, `RECORD`, and `FLUENT`. |
| `include(fields...)` | Whitelist specific fields or methods for testing. |
| `exclude(fields...)` | Blacklist specific fields or methods. |
| `constructors()` | Enable testing of all discovered public/private constructors. |
| `getterSetter()` | Enable testing of field accessors and mutators. |
| `equalsMethod()` | Verify the `equals()` contract (reflexivity, null-safety, symmetry). |
| `hashCodeMethod()` | Verify that `hashCode()` is consistent with `equals()`. |
| `toStringMethod()` | Ensure `toString()` doesn't crash and is consistent. |

---

## 📊 Test Results

By using `.test()`, you receive a `List<TestResult>`. This is useful for custom reporting or soft assertions.

```java
List<TestResult> results = ModelTester.allOf(User.class).test();

results.stream()
    .filter(r -> r.getStatus() == TestStatus.FAIL)
    .forEach(r -> System.err.println("Failed: " + r.getComponent() + " - " + r.getMessage()));
```

---

## 📝 Logging
`ModelTester` uses **SLF4J**. If a test fails, it logs detailed information about the mismatch (expected vs actual types and values). To see these logs, ensure you have an SLF4J implementation (like `slf4j-simple`) in your test runtime.

---

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
