package com.joutvhu.model.tester;

class ConstructorTester<T> implements Tester {
    private Creator<T> creatable;

    ConstructorTester(Creator<T> creatable) {
        this.creatable = creatable;
    }

    @Override
    public boolean test() {
        try {
            T result = creatable.create();
            boolean success = Assert.assertNotNull(result);
            if (success)
                System.out.println("Success: " + creatable.modelClass.getName() + "(" + params() + ")");
            else
                System.err.println("Failure: " + creatable.modelClass.getName() + "(" + params() + ")");
            return success;
        } catch (Throwable e) {
            System.err.println("Error: " + creatable.modelClass.getName() + "(" + params() + ")");
            e.printStackTrace();
        }
        return false;
    }

    private String params() {
        boolean comma = false;
        StringBuilder builder = new StringBuilder();
        if (creatable.values != null) {
            for (Object value : creatable.values) {
                if (comma)
                    builder.append(",");
                builder.append(value.getClass().getName());
                comma = true;
            }
        }
        if (creatable.parameters != null) {
            for (Creator<?> value : creatable.parameters) {
                if (comma)
                    builder.append(",");
                builder.append(value.modelClass.getName());
                comma = true;
            }
        }
        return builder.toString();
    }
}
