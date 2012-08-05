package jp.vmi.selenium.selenese;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;

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
                }
            }
            );
    }

    public static Runner getRunner() {
        return injector.getInstance(Runner.class);
    }

}
