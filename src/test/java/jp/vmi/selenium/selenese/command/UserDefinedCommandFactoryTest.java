package jp.vmi.selenium.selenese.command;

import org.junit.Test;
import org.openqa.selenium.WebDriverCommandProcessor;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * User defined command factory test.
 */
@SuppressWarnings("javadoc")
public class UserDefinedCommandFactoryTest {

    private static class TestCommand extends Command {

        TestCommand(int index, String name, String[] args, int argCnt) {
            super(index, name, args, argCnt);
        }
    }

    @Test
    public void test() {

        CommandFactory.registerUserDefinedCommandFactory(new UserDefinedCommandFactory() {
            @Override
            public Command newCommand(int index, String name, String... args) {
                if ("test".equals(name)) {
                    return new TestCommand(index, name, args, args.length);
                } else {
                    return null;
                }
            }
        });
        CommandFactory cf = new CommandFactory(new WebDriverCommandProcessor("http://localhost/", new HtmlUnitDriver(true)));
        assertThat(cf.newCommand(1, "test"), is(instanceOf(TestCommand.class)));
        assertThat(cf.newCommand(2, "echo", "test"), is(instanceOf(Echo.class)));
    }
}
