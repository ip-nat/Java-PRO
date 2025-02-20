package test;

import annotation.AfterSuite;
import annotation.AfterTest;
import annotation.BeforeSuite;
import annotation.BeforeTest;
import annotation.CsvSource;
import annotation.Test;

public class TestSuite {

    @BeforeSuite
    public static void setUp() {
        System.out.println("Before Suite");
    }

    @BeforeTest
    public void beforeEach() {
        System.out.println("Before Test");
    }

    @Test(priority = 1)
    public void test1() {
        System.out.println("Test Method 1");
    }

    @Test(priority = 10)
    public void test2() {
        System.out.println("Test Method 2");
    }

    @AfterSuite
    public static void tearDown() {
        System.out.println("After Suite");
    }

    @AfterTest
    public void afterEach() {
        System.out.println("After Test");
    }

    @CsvSource("33, Cows, true")
    public void testCsv(int a, String b, boolean c) {
        System.out.printf("Parsed CSV: %d, %s, %b", a, b, c);
    }

}
