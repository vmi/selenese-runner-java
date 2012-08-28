package jp.vmi.selenium.selenese;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Test for {@link TestCaseParser}.
 */
public class TestCaseParserTest {

    /**
     * Test of empty file.
     *
     * @throws IOException exception.
     */
    @Ignore("want to remove FirefoxDriver dependency.")
    @Test
    public void emptyFile() throws IOException {
        Runner runner = new Runner();
        runner.setDriver(new FirefoxDriver());
        String scriptFile = TestUtils.getScriptFile(CommandRunnerTest.class, "Simple");
        TestCase testCase = (TestCase) Parser.parse(new File(scriptFile), runner);
        testCase.execute(null);
    }
}
