package ru.otus.java.pro.reflection;

import ru.otus.java.pro.reflection.annotations.*;

public class TestSuite {

    @BeforeSuite
    public static void init() {
        System.out.println("init");
    }

    @Test(priority = 1)
    public static void test1() {
        System.out.println("Test 1");
    }

    @Test(priority = 10)
    public static void test2() {
        System.out.println("Test 2");
    }

    @Test(priority = 5)
    @Disabled(reason = "Test 3 is disabled for demonstration")
    public static void test3() {
        System.out.println("Test 3");
    }

    @AfterSuite
    public static void tearDown() {
        System.out.println("tearDown");
    }
}
