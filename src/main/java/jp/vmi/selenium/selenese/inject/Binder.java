package jp.vmi.selenium.selenese.inject;

import java.io.File;

import org.openqa.selenium.WebDriver;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.TestSuite;

public class Binder {
    private static Injector injector;

    static {
        injector = Guice.createInjector(
            new AbstractModule() {
                @Override
                protected void configure() {
                    bindInterceptor(
                        Matchers.any(),
                        Matchers.annotatedWith(DoCommand.class),
                        new CommandLogInterceptor()
                    );
                    bindInterceptor(
                        Matchers.any(),
                        Matchers.annotatedWith(ExecuteTestCase.class),
                        new ExecuteTestCaseInterceptor()
                    );
                    bindInterceptor(
                        Matchers.any(),
                        Matchers.annotatedWith(ExecuteTestSuite.class),
                        new ExecuteTestSuiteInterceptor()
                    );
                }
            }
            );
    }

    public static TestCase newTestCase(File file, String name, WebDriver driver, String baseURI) {
        TestCase testCase = injector.getInstance(TestCase.class);
        return testCase.initialize(file, name, driver, baseURI);
    }

    public static TestSuite newTestSuite(File file, String name) {
        TestSuite testSuite = injector.getInstance(TestSuite.class);
        return testSuite.initialize(file, name);
    }
}
