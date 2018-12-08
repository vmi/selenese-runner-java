package jp.vmi.selenium.selenese.side;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import jp.vmi.selenium.runner.model.side.Side;
import jp.vmi.selenium.runner.model.side.SideSuite;
import jp.vmi.selenium.runner.model.side.SideTest;
import jp.vmi.selenium.selenese.InvalidSeleneseException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@SuppressWarnings("javadoc")
public class SideReaderTest {

    @Test
    public void test() throws IOException, InvalidSeleneseException {
        String filename = "/selenese/testsuite_simple.side";
        try (InputStream is = SideReaderTest.class.getResourceAsStream(filename)) {
            Side side = Side.parse(filename, is);
            List<SideSuite> suites = side.getSuites();
            assertThat(suites.size(), greaterThan(0));
            SideSuite suite = suites.get(0);
            List<String> testIds = suite.getTests();
            assertThat(testIds.size(), greaterThan(0));
            SideTest test = side.getTestMap().get(testIds.get(0));
            assertThat(test.getCommands().get(0).getCommand(), is("open"));
        }
    }
}
