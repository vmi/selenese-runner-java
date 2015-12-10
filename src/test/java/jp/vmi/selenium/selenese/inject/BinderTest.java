package jp.vmi.selenium.selenese.inject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;

import jp.vmi.junit.result.ITestCase;
import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.Selenese;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.TestSuite;
import jp.vmi.selenium.selenese.command.CommandFactory;
import jp.vmi.selenium.selenese.command.ICommand;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.testutils.TestBase;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

/**
 * Test and example of registering custom interceptors.
 */
public class BinderTest extends TestBase {

    private void execute() throws Exception {
        setWebDriverFactory(WebDriverManager.HTMLUNIT, new DriverOptions());
        Runner runner = new Runner();
        runner.setDriver(manager.get());
        CommandFactory cf = runner.getCommandFactory();
        File testCaseFile = File.createTempFile("testCase", ".html");
        TestCase testCase = Binder.newTestCase(testCaseFile.getPath(), "testCase", "http://example.com");
        testCase.addCommand(cf, "echo", "test intercepters");
        File testSuiteFile = File.createTempFile("testSuite", ".html");
        TestSuite testSuite = Binder.newTestSuite(testSuiteFile.getPath(), "testSuite");
        testSuite.addSelenese(testCase);
        runner.execute(testSuite);
    }

    /**
     * Test of appending custom interceptors.
     *
     * @throws Exception exception.
     */
    @Test
    public void testAppendInterceptors() throws Exception {
        final List<String> called = new ArrayList<>();
        Binder.replaceBindModule(new BindModule() {
            @Override
            protected AbstractExecuteTestSuiteInterceptor[] getExecuteTestSuiteInterceptors() {
                return appendInterceptors(super.getExecuteTestSuiteInterceptors(), new AbstractExecuteTestSuiteInterceptor() {
                    @Override
                    protected Result invoke(MethodInvocation invocation, TestSuite testSuite, Selenese parent, Context context) throws Throwable {
                        called.add("ExecuteTestSuite1");
                        Result result = (Result) invocation.proceed();
                        called.add("ExecuteTestSuite2");
                        return result;
                    }
                });
            }

            @Override
            protected AbstractExecuteTestCaseInterceptor[] getExecuteTestCaseInterceptors() {
                return appendInterceptors(super.getExecuteTestCaseInterceptors(), new AbstractExecuteTestCaseInterceptor() {
                    @Override
                    protected Result invoke(MethodInvocation invocation, ITestCase testCase, Selenese parent, Context context) throws Throwable {
                        called.add("ExecuteTestCase1");
                        Result result = (Result) invocation.proceed();
                        called.add("ExecuteTestCase2");
                        return result;
                    }
                });
            }

            @Override
            protected AbstractDoCommandInterceptor[] getDoCommandInterceptors() {
                return appendInterceptors(super.getDoCommandInterceptors(), new AbstractDoCommandInterceptor() {
                    @Override
                    protected Result invoke(MethodInvocation invocation, Context context, ICommand command, String[] curArgs) throws Throwable {
                        called.add("DoCommand1");
                        Result result = (Result) invocation.proceed();
                        called.add("DoCommand2");
                        return result;
                    }
                });
            }
        });
        execute();
        assertThat(called, is(equalTo(Arrays.asList(
            "ExecuteTestSuite1",
            "ExecuteTestCase1",
            "DoCommand1",
            "DoCommand2",
            "ExecuteTestCase2",
            "ExecuteTestSuite2"))));
    }

    /**
     * Test of prepending custom interceptors.
     *
     * @throws Exception exception.
     */
    @Test
    public void testPrependInterceptors() throws Exception {
        final List<String> called = new ArrayList<>();
        Binder.replaceBindModule(new BindModule() {
            @Override
            protected AbstractExecuteTestSuiteInterceptor[] getExecuteTestSuiteInterceptors() {
                return prependInterceptors(super.getExecuteTestSuiteInterceptors(), new AbstractExecuteTestSuiteInterceptor() {
                    @Override
                    protected Result invoke(MethodInvocation invocation, TestSuite testSuite, Selenese parent, Context context) throws Throwable {
                        called.add("ExecuteTestSuite1");
                        Result result = (Result) invocation.proceed();
                        called.add("ExecuteTestSuite2");
                        return result;
                    }
                });
            }

            @Override
            protected AbstractExecuteTestCaseInterceptor[] getExecuteTestCaseInterceptors() {
                return prependInterceptors(super.getExecuteTestCaseInterceptors(), new AbstractExecuteTestCaseInterceptor() {
                    @Override
                    protected Result invoke(MethodInvocation invocation, ITestCase testCase, Selenese parent, Context context) throws Throwable {
                        called.add("ExecuteTestCase1");
                        Result result = (Result) invocation.proceed();
                        called.add("ExecuteTestCase2");
                        return result;
                    }
                });
            }

            @Override
            protected AbstractDoCommandInterceptor[] getDoCommandInterceptors() {
                return prependInterceptors(super.getDoCommandInterceptors(), new AbstractDoCommandInterceptor() {
                    @Override
                    protected Result invoke(MethodInvocation invocation, Context context, ICommand command, String[] curArgs) throws Throwable {
                        called.add("DoCommand1");
                        Result result = (Result) invocation.proceed();
                        called.add("DoCommand2");
                        return result;
                    }
                });
            }
        });
        execute();
        assertThat(called, is(equalTo(Arrays.asList(
            "ExecuteTestSuite1",
            "ExecuteTestCase1",
            "DoCommand1",
            "DoCommand2",
            "ExecuteTestCase2",
            "ExecuteTestSuite2"))));
    }

    /**
     * Test of complex custom interceptors.
     *
     * @throws Exception exception.
     */
    @Test
    public void testComplexInterceptors() throws Exception {
        final List<String> called = new ArrayList<>();
        Binder.replaceBindModule(new BindModule() {
            @Override
            protected AbstractExecuteTestSuiteInterceptor[] getExecuteTestSuiteInterceptors() {
                AbstractExecuteTestSuiteInterceptor[] itcs = super.getExecuteTestSuiteInterceptors();
                itcs = prependInterceptors(itcs, new AbstractExecuteTestSuiteInterceptor() {
                    @Override
                    protected Result invoke(MethodInvocation invocation, TestSuite testSuite, Selenese parent, Context context) throws Throwable {
                        called.add("ExecuteTestSuite/Prepend1");
                        Result result = (Result) invocation.proceed();
                        called.add("ExecuteTestSuite/Prepend2");
                        return result;
                    }
                });
                itcs = appendInterceptors(itcs,
                    new AbstractExecuteTestSuiteInterceptor() {
                    @Override
                    protected Result invoke(MethodInvocation invocation, TestSuite testSuite, Selenese parent, Context context) throws Throwable {
                        called.add("ExecuteTestSuite/Append1");
                        Result result = (Result) invocation.proceed();
                        called.add("ExecuteTestSuite/Append2");
                        return result;
                    }
                });
                return itcs;
            }

            @Override
            protected AbstractExecuteTestCaseInterceptor[] getExecuteTestCaseInterceptors() {
                AbstractExecuteTestCaseInterceptor[] itcs = super.getExecuteTestCaseInterceptors();
                itcs = prependInterceptors(itcs, new AbstractExecuteTestCaseInterceptor() {
                    @Override
                    protected Result invoke(MethodInvocation invocation, ITestCase testCase, Selenese parent, Context context) throws Throwable {
                        called.add("ExecuteTestCase/Prepend1");
                        Result result = (Result) invocation.proceed();
                        called.add("ExecuteTestCase/Prepend2");
                        return result;
                    }
                });
                itcs = appendInterceptors(itcs,
                    new AbstractExecuteTestCaseInterceptor() {
                    @Override
                    protected Result invoke(MethodInvocation invocation, ITestCase testCase, Selenese parent, Context context) throws Throwable {
                        called.add("ExecuteTestCase/Append1");
                        Result result = (Result) invocation.proceed();
                        called.add("ExecuteTestCase/Append2");
                        return result;
                    }
                });
                return itcs;
            }

            @Override
            protected AbstractDoCommandInterceptor[] getDoCommandInterceptors() {
                AbstractDoCommandInterceptor[] itcs = super.getDoCommandInterceptors();
                itcs = prependInterceptors(itcs, new AbstractDoCommandInterceptor() {
                    @Override
                    protected Result invoke(MethodInvocation invocation, Context context, ICommand command, String[] curArgs) throws Throwable {
                        called.add("DoCommand/Prepend1");
                        Result result = (Result) invocation.proceed();
                        called.add("DoCommand/Prepend2");
                        return result;
                    }
                });
                itcs = appendInterceptors(itcs,
                    new AbstractDoCommandInterceptor() {
                    @Override
                    protected Result invoke(MethodInvocation invocation, Context context, ICommand command, String[] curArgs) throws Throwable {
                        called.add("DoCommand/Append1");
                        Result result = (Result) invocation.proceed();
                        called.add("DoCommand/Append2");
                        return result;
                    }
                });
                return itcs;
            }
        });
        execute();
        assertThat(called, is(equalTo(Arrays.asList(
            "ExecuteTestSuite/Prepend1",
            "ExecuteTestSuite/Append1",
            "ExecuteTestCase/Prepend1",
            "ExecuteTestCase/Append1",
            "DoCommand/Prepend1",
            "DoCommand/Append1",
            "DoCommand/Append2",
            "DoCommand/Prepend2",
            "ExecuteTestCase/Append2",
            "ExecuteTestCase/Prepend2",
            "ExecuteTestSuite/Append2",
            "ExecuteTestSuite/Prepend2"))));
    }
}
