# Model Tester

Model Tester is a utility for automatically testing model classes (POJOs, Records, etc.), reducing boilerplate test code.

## Key Features

- **Automated Testing**: One-line testing for constructors, getters/setters, `equals`, `hashCode`, and `toString`.
- **Modern Java Support**: Works with standard POJOs, Lombok-generated methods, and Java Records.
- **Deep Equality**: Supports deep comparison for Collections (`List`, `Set`) and `Map` in both equals and getter/setter tests.
- **Customizable Naming**: Flexibility to handle various naming conventions (e.g., fluent setters, non-standard prefixes).
- **High Performance**: Built-in metadata cache to significantly speed up reflection-heavy test suites.
- **Detailed Reporting**: Returns structured `TestResult` objects with specific failure messages for better debugging.

## Installation

- If you are using Gradle, add the following dependency to your `build.gradle`:

```groovy
testImplementation "com.github.joutvhu:model-tester:1.0.5"
// Add a binding if you want to see test logs
testRuntimeOnly "org.slf4j:slf4j-simple:2.0.7"
```

- If you are using Maven, add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.github.joutvhu</groupId>
    <artifactId>model-tester</artifactId>
    <version>1.0.5</version>
    <scope>test</scope>
</dependency>
```

## How to use?

```java
public class UserTest {
    @Test
    public void testAll() {
        // Standard one-liner
        Assertions.assertTrue(ModelTester.allOf(User.class).test());
    }

    @Test
    public void testWithConfiguration() {
        ModelTester.of(User.class)
                .withNamingStrategy(NamingStrategy.DEFAULT) // Optional
                .constructors()
                .getterSetter()
                .exclude("internalField")
                .equalsMethod()
                .hashCodeMethod()
                .toStringMethod()
                .testAndThrows();
    }
}
```
