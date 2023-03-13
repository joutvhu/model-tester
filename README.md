# Model Tester

Model Tester is a utility for automatically testing model classes.

## Installation

- If you are using Gradle just add the following dependency to your `build.gradle`.

```groovy
testImplementation "com.github.joutvhu:model-tester:1.0.0"
```

- Or add the following dependency to your `pom.xml` if you are using Maven.

```xml
<dependency>
    <groupId>com.github.joutvhu</groupId>
    <artifactId>model-tester</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

## How to use?

- Provide model class to be used for testing.
- Set up test options.
- Use method `test()` or `testAndThrows()` to execute the tester.

```java

public class UserTest {
    @Test
    public void test_all() {
        Assertions.assertTrue(ModelTester.allOf(User.class).test());
    }

    @Test
    public void test_and_throws() {
        ModelTester.allOf(User.class).testAndThrows();
    }

    @Test
    public void test_safe() {
        ModelTester.safeOf(User.class).testAndThrows();
    }

    @Test
    public void test_custom() {
        ModelTester.of(User.class)
                .constructors()
                .exclude("getId", "setId")
                .equalsMethod()
                .hashCodeMethod()
                .toStringMethod()
                .testAndThrows();
    }
}
```
