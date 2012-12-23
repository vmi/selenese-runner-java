package jp.vmi.selenium.selenese;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;

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
     * @param clazz target class.
     * @param suffixes target suffixes.
     * @return script file.
     */
    public static String getScriptFile(Class<?> clazz, String... suffixes) {
        String suffix = StringUtils.join(suffixes);
        String html = "/" + clazz.getCanonicalName().replace('.', '/') + suffix + ".html";
        URL resource = clazz.getResource(html);
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

}
