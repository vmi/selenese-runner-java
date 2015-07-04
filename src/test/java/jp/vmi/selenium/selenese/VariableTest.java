package jp.vmi.selenium.selenese;

import org.junit.Before;
import org.junit.Test;

import jp.vmi.selenium.selenese.command.Command;
import jp.vmi.selenium.selenese.command.CommandFactory;
import jp.vmi.selenium.selenese.command.UserDefinedCommandFactory;
import jp.vmi.selenium.selenese.inject.Binder;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.testutils.TestBase;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * Test for {@link TestCase}.
 */
@SuppressWarnings("deprecation")
public class VariableTest extends TestBase {

    private final Runner runner = new Runner();

    /**
     * Initialize.
     */
    @Before
    public void initialize() {
        setWebDriverFactory(WebDriverManager.HTMLUNIT, new DriverOptions());
        runner.setDriver(manager.get());
    }

    /**
     * Test of replaceVariables(String). (old style)
     */
    @Test
    public void replaceVarsOld() {
        TestCase c = Binder.newTestCase(null, null, runner, wsr.getBaseURL());
        c.getProc().setVar("XYZ", "a");
        assertThat(c.getProc().replaceVars("${a}"), is(equalTo("XYZ")));
    }

    /**
     * Test of replaceVariables(String).
     */
    @Test
    public void replaceVars() {
        VarsMap varsMap = runner.getVarsMap();
        varsMap.put("a", "XYZ");
        assertThat(varsMap.replaceVars("${a}"), is(equalTo("XYZ")));
    }

    /**
     * Test of replaceVariables(String[]). (old style)
     */
    @Test
    public void replaceVarsForArrayOld() {
        TestCase c = Binder.newTestCase(null, null, runner, wsr.getBaseURL());
        c.getProc().setVar("XYZ", "a");
        assertThat(c.getProc().replaceVarsForArray(new String[] { "abc", "${a}", "abc${a}bca" }),
            is(arrayContaining("abc", "XYZ", "abcXYZbca")));
    }

    /**
     * Test of replaceVariables(String[]).
     */
    @Test
    public void replaceVarsForArray() {
        VarsMap varsMap = runner.getVarsMap();
        varsMap.put("a", "XYZ");
        assertThat(varsMap.replaceVarsForArray(new String[] { "abc", "${a}", "abc${a}bca" }),
            is(arrayContaining("abc", "XYZ", "abcXYZbca")));
    }

    /**
     * Test of getEval with "storedVars". (old style)
     */
    @Test
    public void evalWithStoredVars() {
        CommandFactory cf = runner.getCommandFactory();
        cf.registerUserDefinedCommandFactory(new UserDefinedCommandFactory() {
            @Override
            public Command newCommand(int index, String name, String... args) {
                if (!"setBoolean".equals(name))
                    return null;
                return new Command(index, name, args, 2) {

                    @Override
                    protected Result doCommandImpl(TestCase testCase, Runner runner) {
                        runner.getVarsMap().put(args[1], Boolean.parseBoolean(args[0]));
                        return Success.SUCCESS;
                    }
                };
            }
        });
        String baseURL = wsr.getBaseURL();
        VarsMap varsMap = runner.getVarsMap();
        String script = "storedVars['logoutpresent'] ? storedVars['link_logout'] : storedVars['body']";
        varsMap.put("link_logout", "result01");
        varsMap.put("body", "result02");
        varsMap.put("logoutpresent", true);
        TestCase testCase = Binder.newTestCase("dummy", "dummy", baseURL);
        testCase.addCommand(cf, "open", "/");
        testCase.addCommand(cf, "storeEval", script, "var01");
        testCase.addCommand(cf, "setBoolean", "false", "logoutpresent");
        testCase.addCommand(cf, "storeEval", script, "var02");
        Result result = runner.execute(testCase);
        assertThat(result, is(instanceOf(Success.class)));
        assertThat((String) varsMap.get("var01"), is("result01"));
        assertThat((String) varsMap.get("var02"), is("result02"));
    }
}
