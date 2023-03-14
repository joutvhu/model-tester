package com.joutvhu.model.tester;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

class EqualsTester<T> implements Tester {
    private final Class<T> modelClass;
    private final boolean safe;

    EqualsTester(Class<T> modelClass) {
        this(modelClass, false);
    }

    EqualsTester(Class<T> modelClass, boolean safe) {
        this.modelClass = modelClass;
        this.safe = safe;
    }

    @Override
    public boolean test() {
        try {
            T model = Creator.anyOf(modelClass).create();
            boolean success = true;
            if (safe) {
                success = Assert.assertEquals(model, model);
                deepTest(model, null);
            } else {
                T newModel = Creator.makeCopy(model);
                success = Assert.assertEquals(model, newModel);
                deepTest(model, newModel);
            }
            if (success)
                System.out.println("Success: " + modelClass.getName() + ".equals()");
            else
                System.err.println("Failure: " + modelClass.getName() + ".equals()");
            return success;
        } catch (Throwable e) {
            System.err.println("Error: " + modelClass.getName() + ".equals()");
            e.printStackTrace();
        }
        return false;
    }

    private void deepTest(T model, T newModel) {
        try {
            if (newModel == null)
                newModel = Creator.makeCopy(model);
            Set<Field> tested = new HashSet<>();
            deepTest(model, newModel, modelClass.getDeclaredFields(), tested);
            deepTest(model, newModel, modelClass.getFields(), tested);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void deepTest(T model, T newModel, Field[] fields, Set<Field> tested) {
        for (Field field : fields) {
            try {
                if (!tested.contains(field) && !Modifier.isFinal(field.getModifiers())) {
                    tested.add(field);
                    field.setAccessible(true);
                    field.set(newModel, null);
                    Assert.assertEquals(model, newModel);
                    field.set(newModel, field.get(model));
                }
            } catch (Throwable e) {
            }
        }
    }
}
