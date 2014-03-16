package jp.vmi.selenium.selenese.cmdproc;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import difflib.DiffUtils;
import difflib.Patch;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Included WDCP (WebDriverCommandProcessor) is copyed from Selenium and patched for me.
 * 
 * This test will fail if original WDCP is changed.
 */
@SuppressWarnings("javadoc")
public class WebDriverCommandProcessorVersionTest {

    private static final String ORIG_PKG_URL = "https://selenium.googlecode.com/git/java/client/src/com/thoughtworks/selenium/webdriven/";
    private static final String[] ORIG_JAVA_FILES = { "WebDriverCommandProcessor.java", "Windows.java" };
    private static final String COPIED_PATH = "/patches/";

    private List<String> split(String s) {
        return Arrays.asList(s.split("\r?\n"));
    }

    @Test
    public void wdcpVersionTest() throws Exception {
        for (String javaFile : ORIG_JAVA_FILES) {
            List<String> orig = split(IOUtils.toString(URI.create(ORIG_PKG_URL + javaFile)));
            String[] splitted = javaFile.split("/");
            String resName = COPIED_PATH + splitted[splitted.length - 1];
            List<String> copy = split(IOUtils.toString(getClass().getResourceAsStream(resName)));
            Patch<String> patch = DiffUtils.diff(copy, orig);
            assertThat(patch.getDeltas(), is(empty()));
        }
    }
}
