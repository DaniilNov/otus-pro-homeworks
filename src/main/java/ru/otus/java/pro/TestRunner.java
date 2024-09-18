package ru.otus.java.pro;

import ru.otus.java.pro.annotations.AfterSuite;
import ru.otus.java.pro.annotations.BeforeSuite;
import ru.otus.java.pro.annotations.Disabled;
import ru.otus.java.pro.annotations.Test;
import ru.otus.java.pro.exceptions.TestConfigurationException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TestRunner {

    public static void run(Class<?> testClass) {
        if (testClass.isAnnotationPresent(Disabled.class)) {
            Disabled disabled = testClass.getAnnotation(Disabled.class);
            System.out.println("Class " + testClass.getName() + " is disabled: " + disabled.reason());
            return;
        }

        Method beforeSuite = null;
        Method afterSuite = null;
        List<Method> testMethods = new ArrayList<>();

        for (Method method : testClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(BeforeSuite.class)) {
                if (beforeSuite != null) {
                    throw new TestConfigurationException("Only one method can be annotated with @BeforeSuite");
                }
                if (method.isAnnotationPresent(Test.class) || method.isAnnotationPresent(AfterSuite.class)) {
                    throw new TestConfigurationException("Method cannot be annotated with both @BeforeSuite and @Test/@AfterSuite");
                }
                beforeSuite = method;
            } else if (method.isAnnotationPresent(AfterSuite.class)) {
                if (afterSuite != null) {
                    throw new TestConfigurationException("Only one method can be annotated with @AfterSuite");
                }
                if (method.isAnnotationPresent(Test.class) || method.isAnnotationPresent(BeforeSuite.class)) {
                    throw new TestConfigurationException("Method cannot be annotated with both @AfterSuite and @Test/@BeforeSuite");
                }
                afterSuite = method;
            } else if (method.isAnnotationPresent(Test.class)) {
                Test testAnnotation = method.getAnnotation(Test.class);
                int priority = testAnnotation.priority();
                if (priority < 1 || priority > 10) {
                    throw new TestConfigurationException("Test priority must be between 1 and 10");
                }
                testMethods.add(method);
            }
        }

        testMethods.sort(Comparator.comparingInt((Method m) -> m.getAnnotation(Test.class).priority()).reversed());

        int passed = 0;
        int failed = 0;

        try {
            if (beforeSuite != null) {
                beforeSuite.invoke(null);
            }

            for (Method testMethod : testMethods) {
                if (testMethod.isAnnotationPresent(Disabled.class)) {
                    Disabled disabled = testMethod.getAnnotation(Disabled.class);
                    System.out.println("Method " + testMethod.getName() + " is disabled: " + disabled.reason());
                    continue;
                }

                try {
                    testMethod.invoke(null);
                    passed++;
                } catch (Exception e) {
                    System.out.println("Test " + testMethod.getName() + " failed: " + e.getCause());
                    failed++;
                }
            }

            if (afterSuite != null) {
                afterSuite.invoke(null);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during test execution", e);
        }

        System.out.println("Tests run: " + (passed + failed) + ", Passed: " + passed + ", Failed: " + failed);
    }
}