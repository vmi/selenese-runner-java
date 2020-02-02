package jp.vmi.selenium.selenese.command;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.SourceType;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.inject.Binder;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.testutils.TestBase;

import static jp.vmi.selenium.selenese.result.Success.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * User defined command factoryName test.
 */
public class UserDefinedCommandFactoryTest extends TestBase {

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
        TestCase testCase = Binder.newTestCase(SourceType.SELENESE, "dummy", "dummy", "http://localhost/");
        testCase.addCommand(cf, "test");
        testCase.addCommand(cf, "echo", "test");
        CommandList commandList = testCase.getCommandList();
        List<ICommand> list = new ArrayList<>();
        commandList.forEach(list::add);
        assertThat(list.toArray(), is(array(instanceOf(TestCommandNew.class), instanceOf(Echo.class))));
    }
}
