package jp.vmi.selenium.rollup;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.command.CommandList;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

@SuppressWarnings("javadoc")
public class RollupTest {

    private static final String EXPECTED = "["
        + "Command#1: open(\"/rollup01.html\"), "
        + "Command#2: type(\"username\", \"USERNAME\"), "
        + "Command#3: type(\"password\", \"PASSWORD\"), "
        + "Command#4: click(\"id=login\")"
        + "]";

    @Test
    public void registerRollup() throws Exception {
        Runner runner = new Runner();
        runner.setDriver(new HtmlUnitDriver(true));
        RollupRules rollupRules = runner.getRollupRules();
        rollupRules.load(getClass().getResourceAsStream("/rollup/user-extention-rollup.js"));
        IRollupRule rule = rollupRules.get("do_login");
        assertThat(rule, is(instanceOf(RollupRule.class)));
        Map<String, String> rollupArgs = new HashMap<String, String>();
        rollupArgs.put("username", "USERNAME");
        rollupArgs.put("password", "PASSWORD");
        CommandList commandList = rule.getExpandedCommands(runner, rollupArgs);
        assertThat(commandList.toString(), is(EXPECTED));
    }
}
