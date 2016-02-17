package jp.vmi.selenium.rollup;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.command.CommandList;
import jp.vmi.selenium.testutils.TestBase;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

@SuppressWarnings("javadoc")
public class RollupTest extends TestBase {

    private static final String EXPECTED = "["
        + "Command#1: open(\"/rollup01.html\"), "
        + "Command#2: type(\"username\", \"USERNAME\"), "
        + "Command#3: type(\"password\", \"PASSWORD\"), "
        + "Command#4: click(\"id=login\")"
        + "]";

    @Test
    public void registerRollup() throws Exception {
        setWebDriverFactory(WebDriverManager.HTMLUNIT, new DriverOptions());
        Runner runner = new Runner();
        runner.setDriver(manager.get());
        RollupRules rollupRules = runner.getRollupRules();
        rollupRules.load(getClass().getResourceAsStream("/rollup/user-extention-rollup.js"));
        IRollupRule rule = rollupRules.get("do_login");
        assertThat(rule, is(instanceOf(RollupRule.class)));
        Map<String, String> rollupArgs = new HashMap<>();
        rollupArgs.put("username", "USERNAME");
        rollupArgs.put("password", "PASSWORD");
        CommandList commandList = rule.getExpandedCommands(runner, rollupArgs);
        assertThat(commandList.toString(), is(EXPECTED));
    }
}
