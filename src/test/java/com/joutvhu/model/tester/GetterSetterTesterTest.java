package com.joutvhu.model.tester;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GetterSetterTesterTest {

    public static class BadSetterModel {
        private String prop;
        
        public BadSetterModel() {}
        public String getProp() { return prop; }
        public void setProp(String prop) { 
            // Bad logic, it does not assign the property
        }
    }

    public static class ErrorGetterModel {
        private String prop;
        
        public ErrorGetterModel() {}
        public String getProp() { 
            throw new RuntimeException("getter failed");
        }
        public void setProp(String prop) {
            this.prop = prop;
        }
    }

    @Test
    void testBadSetter() {
        ModelTester<BadSetterModel> tester = ModelTester.of(BadSetterModel.class).getterSetters();
        assertFalse(tester.test()); // Should fail because getter value won't match setter value
        
        boolean hasFailures = tester.getResults().stream().anyMatch(r -> r.getStatus() == TestStatus.FAIL || r.getStatus() == TestStatus.ERROR);
        assertTrue(hasFailures, "Should capture an error when setter logic is bad");
    }

    @Test
    void testErrorGetter() {
        ModelTester<ErrorGetterModel> tester = ModelTester.of(ErrorGetterModel.class).getterSetters();
        assertFalse(tester.test()); // Should fail because getter throws
        
        boolean hasErrors = tester.getResults().stream().anyMatch(r -> r.getStatus() == TestStatus.ERROR);
        assertTrue(hasErrors, "Should capture an ERROR result when getter throws exception");
    }
}
