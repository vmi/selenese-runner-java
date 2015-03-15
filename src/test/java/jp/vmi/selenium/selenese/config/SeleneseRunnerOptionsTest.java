package jp.vmi.selenium.selenese.config;

import java.io.PrintWriter;

import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.Ignore;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class SeleneseRunnerOptionsTest {

    @Test
    @Ignore
    public void testShowHelp() {
        System.setProperty("columns", "256");
        StringBuilderWriter sbw = new StringBuilderWriter();
        PrintWriter pw = new PrintWriter(sbw);
        new SeleneseRunnerOptions().showHelp(pw, "title", "version", "cmdName", "msgs");
        System.out.println(sbw);
    }
}
