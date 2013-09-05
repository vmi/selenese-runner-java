package jp.vmi.selenium.selenese.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

/**
 * Utililities for Selenium. 
 */
public class SeleniumUtils {

    /**
     * Get Selenium version.
     *
     * @return version string. (X.Y.Z format)
     */
    public static String getVersion() {
        Properties props = new Properties();
        InputStream is = null;
        try {
            is = SeleniumUtils.class.getResourceAsStream("/META-INF/maven/org.seleniumhq.selenium/selenium-java/pom.properties");
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
        return props.getProperty("version", "<unknown>");
    }
}
