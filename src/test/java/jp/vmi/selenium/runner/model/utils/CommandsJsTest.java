package jp.vmi.selenium.runner.model.utils;

import java.util.Map;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("javadoc")
public class CommandsJsTest {

    @Test
    public void testLoad() {
        CommandsJs commandsJs = CommandsJs.load();
        Map<String, String> targetTypes = commandsJs.getTargetTypes();
        Map<String, Map<String, String>> commands = commandsJs.getCommands();
        assertThat(targetTypes.get("NONE"), isEmptyString());
        assertThat(targetTypes.get("LOCATOR"), is("locator"));
        assertThat(commands.get("addSelection").get("name"), is("add selection"));
        assertThat(commands.get("echo").get("description"), is("Prints the specified message into the third table cell in your Selenese tables. Useful for debugging."));
        assertThat(commands.get("echo").get("target"), is("ArgTypes.message"));
    }
}
