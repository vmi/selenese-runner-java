package jp.vmi.selenium.testutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import static jp.vmi.selenium.webdriver.WebDriverManager.*;

/**
 * Utility for Test.
 */
public final class TestUtils {

    private TestUtils() {
        // no operation
    }

    /**
     * Get script file.
     *
     * @param name script name. (without extension)
     * @return script file path.
     */
    public static String getScriptFile(String name) {
        String html = "/selenese/" + name + ".html";
        URL resource = TestUtils.class.getResource(html);
        if (resource == null)
            throw new RuntimeException(new FileNotFoundException(html));
        try {
            return new File(resource.toURI()).getCanonicalPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate parameter list for JUnit parameterized test.
     *
     * @param params parameters.
     * @return list of single element array of each paramter.
     */
    @SafeVarargs
    public static <T> List<Object[]> toParamList(T... params) {
        List<Object[]> list = new ArrayList<>();
        for (T param : params)
            list.add(new Object[] { param });
        return list;
    }

    /**
     * Get WebDriverFactory's for JUnit parameterized test.
     *
     * @return list of single element array of WebDriverFactory.
     */
    public static List<Object[]> getWebDriverFactories() {
        String[] drivers;
        String prop = System.getProperty("test.drivers");
        if (StringUtils.isBlank(prop)) {
            drivers = new String[] {
                HTMLUNIT,
                FIREFOX,
                CHROME,
                PHANTOMJS,
                IE,
                // SAFARI, // FIXME: On Selenium 2.46.0, SafariDriver does not work.
            };
        } else {
            System.err.println("### Test Drivers: " + prop);
            drivers = prop.split("\\s*,\\s*");
        }
        return toParamList(drivers);
    }
}
