package test;

import annotation.AfterSuite;
import annotation.AfterTest;
import annotation.BeforeSuite;
import annotation.BeforeTest;
import annotation.Test;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;

public class TestRunner {

    public static void runTests(Class<?> c) throws Exception {
        List<Method> allMethods = List.of(c.getDeclaredMethods());

        // Проверка на наличие методов с аннотациями @BeforeSuite и @AfterSuite
        Method beforeSuiteMethod = null;
        Method afterSuiteMethod = null;

        for (Method method : allMethods) {
            if (method.isAnnotationPresent(BeforeSuite.class)) {
                if (!isStatic(method)) {
                    throw new RuntimeException("@BeforeSuite method must be static.");
                }
                if (beforeSuiteMethod != null) {
                    throw new RuntimeException("Only one method with @BeforeSuite is allowed.");
                }
                beforeSuiteMethod = method;
            }
            if (method.isAnnotationPresent(AfterSuite.class)) {
                if (!isStatic(method)) {
                    throw new RuntimeException("@AfterSuite method must be static.");
                }
                if (afterSuiteMethod != null) {
                    throw new RuntimeException("Only one method with @AfterSuite is allowed.");
                }
                afterSuiteMethod = method;
            }
        }

        // Выполнение метода @BeforeSuite
        if (beforeSuiteMethod != null) {
            beforeSuiteMethod.invoke(c.getDeclaredConstructor().newInstance());
        }

        // Сортировка методов с аннотацией @Test по приоритету
        List<Method> testMethods = allMethods.stream()
                .filter(method -> method.isAnnotationPresent(Test.class))
                .peek(method -> {
                    int priority = method.getAnnotation(Test.class).priority();
                    if (priority < 1 || priority > 10) {
                        throw new IllegalArgumentException("Priority for method " + method.getName() + " must be between 1 and 10.");
                    }
                })
                .sorted(Comparator.comparingInt(method -> method.getAnnotation(Test.class).priority()))
                .toList();

        // Выполнение тестов с @BeforeTest и @AfterTest
        for (Method testMethod : testMethods) {
            try {
                // Выполнение метода @BeforeTest перед каждым тестом
                for (Method method : allMethods) {
                    if (method.isAnnotationPresent(BeforeTest.class)) {
                        method.invoke(c.getDeclaredConstructor().newInstance());
                    }
                }

                // Выполнение теста
                testMethod.invoke(c.getDeclaredConstructor().newInstance());

                // Выполнение метода @AfterTest после каждого теста
                for (Method method : allMethods) {
                    if (method.isAnnotationPresent(AfterTest.class)) {
                        method.invoke(c.getDeclaredConstructor().newInstance());
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to invoke test method: " + testMethod.getName(), e);
            }
        }

        // Выполнение метода @AfterSuite
        if (afterSuiteMethod != null) {
            afterSuiteMethod.invoke(null);
        }

    }

    private static boolean isStatic(Method method) {
        return java.lang.reflect.Modifier.isStatic(method.getModifiers());
    }

}
