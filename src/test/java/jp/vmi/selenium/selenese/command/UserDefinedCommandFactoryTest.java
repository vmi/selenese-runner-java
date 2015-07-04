package jp.vmi.selenium.selenese.command;

import org.junit.Test;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.cmdproc.CustomCommandProcessor;
import jp.vmi.selenium.selenese.inject.Binder;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.testutils.TestBase;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static jp.vmi.selenium.selenese.result.Success.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * User defined command factoryName test.
 */
@SuppressWarnings("deprecation")
public class UserDefinedCommandFactoryTest extends TestBase {

    private static class TestCommand extends Command {

        TestCommand(int index, String name, String[] args, int argCnt) {
            super(index, name, args, argCnt);
        }
    }

    private static class TestCommandNew extends AbstractCommand {

        TestCommandNew(int index, String name, String... args) {
            super(index, name, args);
        }

        @Override
        protected Result executeImpl(Context context, String... curArgs) {
            return SUCCESS;
        }
    }

    /**
     * Register user-defined command factory. (old style)
     */
    @Test
    public void registerUDCFOld() {
        setWebDriverFactory(WebDriverManager.HTMLUNIT, new DriverOptions());
        Runner runner = new Runner();
        // runner.setBaseURL("http://localhost/");
        // runner.setDriver(new HtmlUnitDriver(true));
        CommandFactory cf = runner.getCommandFactory();
        cf.registerUserDefinedCommandFactory(new UserDefinedCommandFactory() {
            @Override
            public Command newCommand(int index, String name, String... args) {
                if ("test".equals(name)) {
                    return new TestCommand(index, name, args, args.length);
                } else {
                    return null;
                }
            }
        });
        // runner.run(...);

        // only for test.
        cf.setProc(new CustomCommandProcessor("http://localhost/", manager.get()));
        assertThat(cf.newCommand(1, "test"), is(instanceOf(TestCommand.class)));
        assertThat(cf.newCommand(2, "echo", "test"), is(instanceOf(Echo.class)));
    }

    /**
     * Register user-defined command factory.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void registerUDCF() {
        Runner runner = new Runner();
        CommandFactory cf = runner.getCommandFactory();
        cf.registerCommandFactory(new ICommandFactory() {
            @Override
            public ICommand newCommand(int index, String name, String... args) {
                if ("test".equals(name)) {
                    return new TestCommandNew(index, name, args);
                } else {
                    return null;
                }
            }
        });
        TestCase testCase = Binder.newTestCase("dummy", "dummy", "http://localhost/");
        testCase.addCommand(cf, "test");
        testCase.addCommand(cf, "echo", "test");
        CommandList commandList = testCase.getCommandList();
        assertThat(commandList.toArray(), is(array(instanceOf(TestCommandNew.class), instanceOf(Echo.class))));
    }
}
