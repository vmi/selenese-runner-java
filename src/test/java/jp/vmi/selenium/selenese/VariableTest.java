package jp.vmi.selenium.selenese;

import org.junit.Before;
import org.junit.Test;

import jp.vmi.selenium.testutils.TestBase;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * Test for {@link TestCase}.
 */
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
     * Test of replaceVariables(String).
     */
    @Test
    public void replaceVars() {
        VarsMap varsMap = runner.getVarsMap();
        varsMap.put("a", "XYZ");
        assertThat(varsMap.replaceVars(false, "${a}"), is(equalTo("XYZ")));
        assertThat(varsMap.replaceVars(true, "${a}"), is(equalTo("\"XYZ\"")));
        assertThat(varsMap.replaceVars(true, "\"${a}\""), is(equalTo("\"XYZ\"")));
    }

    /**
     * Test of replaceVariables(String[]).
     */
    @SuppressWarnings("deprecation")
    @Test
    public void replaceVarsForArray() {
        VarsMap varsMap = runner.getVarsMap();
        varsMap.put("a", "XYZ");
        assertThat(varsMap.replaceVarsForArray(new String[] { "abc", "${a}", "abc${a}bca" }),
            is(arrayContaining("abc", "XYZ", "abcXYZbca")));
    }

    /**
     * Test of replaceVariables(Number).
     */
    @Test
    public void replaceVarsForNumber() {
        VarsMap varsMap = runner.getVarsMap();
        varsMap.put("a1", 1);
        varsMap.put("a2", 1.0);
        varsMap.put("a3", 1.5);
        assertThat(varsMap.replaceVars(false, "${a1}"), is(equalTo("1")));
        assertThat(varsMap.replaceVars(false, "${a2}"), is(equalTo("1")));
        assertThat(varsMap.replaceVars(false, "${a3}"), is(equalTo("1.5")));
    }
}
