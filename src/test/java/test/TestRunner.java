package test;

import annotation.AfterSuite;
import annotation.AfterTest;
import annotation.BeforeSuite;
import annotation.BeforeTest;
import annotation.CsvSource;
import annotation.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TestRunner {

    public static void runTests(Class<?> c) throws Exception {
        List<Method> allMethods = List.of(c.getDeclaredMethods());

        // Проверка на наличие методов с аннотациями @BeforeSuite и @AfterSuite
        Method beforeSuiteMethod = null;
        Method afterSuiteMethod = null;
        List<Method> testMethods = new ArrayList<>();
        List<Method> beforeTestMethods = new ArrayList<>();
        List<Method> afterTestMethods = new ArrayList<>();

        for (Method method : allMethods) {
            if (method.isAnnotationPresent(BeforeSuite.class)) {
                if (!java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
                    throw new RuntimeException("@BeforeSuite method must be static.");
                }
                if (beforeSuiteMethod != null) {
                    throw new RuntimeException("Only one method with @BeforeSuite is allowed.");
                }
                beforeSuiteMethod = method;
            }
            if (method.isAnnotationPresent(AfterSuite.class)) {
                if (!java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
                    throw new RuntimeException("@AfterSuite method must be static.");
                }
                if (afterSuiteMethod != null) {
                    throw new RuntimeException("Only one method with @AfterSuite is allowed.");
                }
                afterSuiteMethod = method;
            }
            if (method.isAnnotationPresent(Test.class)) {
                validateTestPriority(method);
                testMethods.add(method);
            }
            if (method.isAnnotationPresent(BeforeTest.class)) {
                beforeTestMethods.add(method);
            }
            if (method.isAnnotationPresent(AfterTest.class)) {
                afterTestMethods.add(method);
            }
        }


        // Выполнение метода @BeforeSuite
        if (beforeSuiteMethod != null) {
            beforeSuiteMethod.invoke(c.getDeclaredConstructor().newInstance());
        }

        // Сортировка методов с аннотацией @Test по приоритету
        testMethods.sort(Comparator.comparingInt(method -> method.getAnnotation(Test.class).priority()));

        // Выполнение тестов @BeforeTest и @AfterTest
        for (Method testMethod : testMethods) {
            try {
                // Выполнение метода @BeforeTest перед каждым тестом
                for (Method beforeTestMethod : beforeTestMethods) {
                    beforeTestMethod.invoke(c.getDeclaredConstructor().newInstance());
                }

                // Выполнение теста
                testMethod.invoke(c.getDeclaredConstructor().newInstance());

                // Выполнение метода @AfterTest после каждого теста
                for (Method afterTestMethod : afterTestMethods) {
                    afterTestMethod.invoke(c.getDeclaredConstructor().newInstance());
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to invoke test method: " + testMethod.getName(), e);
            }
        }

        // Выполнение метода @AfterSuite
        if (afterSuiteMethod != null) {
            afterSuiteMethod.invoke(null);
        }

        // Выполнение методов с аннотацией @CsvSource
        for (Method method : allMethods) {
            if (method.isAnnotationPresent(CsvSource.class)) {
                List<Object> args = parseCsvArguments(method);
                method.invoke(c.getDeclaredConstructor().newInstance(), args.toArray());
            }
        }

    }

    private static void validateTestPriority(Method method) {
        int priority = method.getAnnotation(Test.class).priority();
        if (priority < 1 || priority > 10) {
            throw new IllegalArgumentException("Priority for method " + method.getName() + " must be between 1 and 10.");
        }
    }

    private static List<Object> parseCsvArguments(Method method) {
        CsvSource csvSource = method.getAnnotation(CsvSource.class);
        List<String> values = Arrays.asList(csvSource.value().split(",\\s*")); // Разделение по запятой (с учетом пробелов)
        List<Parameter> parameters = Arrays.asList(method.getParameters());

        if (values.size() != parameters.size()) {
            throw new IllegalArgumentException("Mismatch between CSV values and method parameters for: " + method.getName() +
                    ". Expected " + parameters.size() + " parameters but got " + values.size());
        }

        List<Object> args = new ArrayList<>(parameters.size());

        for (int i = 0; i < parameters.size(); i++) {
            args.add(convertValue(values.get(i), parameters.get(i).getType()));
        }

        return args;
    }

    private static Object convertValue(String value, Class<?> type) {
        if (type == int.class) {
            return parseInteger(value);
        } else if (type == boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (type == String.class) {
            return value;
        } else {
            throw new UnsupportedOperationException("Unsupported parameter type: " + type.getName());
        }
    }

    private static int parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid integer value: " + value, e);
        }
    }

}
