package foldlabs.concurrency.util;

import java.lang.reflect.Method;

public final class Reflections {

    private Reflections() {
    }

    public static Method findMethod(Class<?> aClass, String methodName) {
        for (Method method : aClass.getMethods()) {
            if (method.getName().equals(methodName))
                return method;
        }

        throw new IllegalArgumentException("cannot find method " + methodName + " in class " + aClass);
    }
}
