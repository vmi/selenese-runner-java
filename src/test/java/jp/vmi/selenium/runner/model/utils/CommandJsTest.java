package jp.vmi.selenium.runner.model.utils;

import java.util.Map;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@SuppressWarnings("javadoc")
public class CommandJsTest {

    @Test
    public void testLoad() {
        CommandJs commandJs = CommandJs.load();
        Map<String, String> targetTypes = commandJs.getTargetTypes();
        Map<String, Map<String, String>> argTypes = commandJs.getArgTypes();
        Map<String, Map<String, String>> commandList = commandJs.getCommandList();
        Map<String, String> controlFlowCommandNames = commandJs.getControlFlowCommandNames();
        assertThat(targetTypes.get("NONE"), isEmptyString());
        assertThat(targetTypes.get("LOCATOR"), is("locator"));
        assertThat(argTypes.get("attributeLocator").get("name"), is("attribute locator"));
        assertThat(argTypes.get("attributeLocator").get("description"), is("An element locator followed by an @ sign and then the name of the attribute, e.g. \"foo@bar\"."));
        assertThat(commandList.get("addSelection").get("name"), is("add selection"));
        assertThat(commandList.get("echo").get("description"), is("Prints the specified message into the third table cell in your Selenese tables. Useful for debugging."));
        assertThat(commandList.get("echo").get("target"), is("ArgTypes.message"));
        assertThat(controlFlowCommandNames.get("repeatIf"), is("repeatIf"));
    }
}
