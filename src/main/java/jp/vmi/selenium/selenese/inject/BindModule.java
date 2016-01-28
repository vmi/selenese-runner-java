package jp.vmi.selenium.selenese.inject;

import java.util.Arrays;

import org.aopalliance.intercept.MethodInterceptor;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

/**
 * Bind interceptors for test-suite/test-case execution.
 *
 * If you want to add your custom interceptors, you extends this class
 * and override get***Interceptors() which return your interceptors.
 */
public class BindModule extends AbstractModule {

    @Override
    protected void configure() {
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(DoCommand.class), getDoCommandInterceptors());
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(ExecuteTestCase.class), getExecuteTestCaseInterceptors());
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(ExecuteTestSuite.class), getExecuteTestSuiteInterceptors());
    }

    /**
     * Prepend custom interceptors to original interceptors.
     *
     * @param <T> subclass of {@link MethodInterceptor}.
     * @param originItcs array of original interceptors.
     * @param customItcs custom interceptors.
     * @return modified array of interceptors.
     */
    @SafeVarargs
    protected static <T extends MethodInterceptor> T[] prependInterceptors(T[] originItcs, T... customItcs) {
        if (customItcs.length == 0)
            return originItcs;
        T[] newItcs = Arrays.copyOf(customItcs, customItcs.length + originItcs.length);
        System.arraycopy(originItcs, 0, newItcs, customItcs.length, originItcs.length);
        return newItcs;
    }

    /**
     * Append custom interceptors to original interceptors.
     *
     * @param <T> subclass of {@link MethodInterceptor}.
     * @param originItcs array of original interceptors.
     * @param customItcs custom interceptors.
     * @return modified array of interceptors.
     */
    @SafeVarargs
    protected static <T extends MethodInterceptor> T[] appendInterceptors(T[] originItcs, T... customItcs) {
        if (customItcs.length == 0)
            return originItcs;
        T[] newItcs = Arrays.copyOf(originItcs, originItcs.length + customItcs.length);
        System.arraycopy(customItcs, 0, newItcs, originItcs.length, customItcs.length);
        return newItcs;
    }

    /**
     * Get DoCommand interceptors.
     *
     * @return DoCommand interceptors.
     */
    protected AbstractDoCommandInterceptor[] getDoCommandInterceptors() {
        return new AbstractDoCommandInterceptor[] {
            new CommandLogInterceptor(), /* 1st */
            new HighlightInterceptor(), /* 2nd */
            new ScreenshotInterceptor() /* 3rd */
        };
    }

    /**
     * Get ExecuteTestCase interceptors.
     *
     * @return ExecuteTestCase interceptors.
     */
    protected AbstractExecuteTestCaseInterceptor[] getExecuteTestCaseInterceptors() {
        return new AbstractExecuteTestCaseInterceptor[] {
            new ExecuteTestCaseInterceptor()
        };
    }

    /**
     * Get ExecuteTestSuite interceptors.
     *
     * @return ExecuteTestSuite interceptors.
     */
    protected AbstractExecuteTestSuiteInterceptor[] getExecuteTestSuiteInterceptors() {
        return new AbstractExecuteTestSuiteInterceptor[] {
            new ExecuteTestSuiteInterceptor()
        };
    }
}
