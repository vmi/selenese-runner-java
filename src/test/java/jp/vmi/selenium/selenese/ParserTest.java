package jp.vmi.selenium.selenese;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@SuppressWarnings("javadoc")
public class ParserTest {

    private static final String WITHOUT_BASE_URL = "/selenese/withoutBaseURL.html";
    private static final String TEST_SUITE = "/selenese/testSuite.html";

    @Test
    public void parseTestCaseWithoutBaseURL() {
        Runner runner = new Runner();
        runner.setDriver(new HtmlUnitDriver(true));
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream(WITHOUT_BASE_URL);
            Selenese selenese = Parser.parse(WITHOUT_BASE_URL, is, runner.getCommandFactory());
            assertThat(selenese, is(instanceOf(TestCase.class)));
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @Test
    public void parseTestSuite() {
        Runner runner = new Runner();
        runner.setDriver(new HtmlUnitDriver(true));
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream(TEST_SUITE);
            Selenese selenese = Parser.parse(TEST_SUITE, is, runner.getCommandFactory());
            assertThat(selenese, is(instanceOf(TestSuite.class)));
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
}
