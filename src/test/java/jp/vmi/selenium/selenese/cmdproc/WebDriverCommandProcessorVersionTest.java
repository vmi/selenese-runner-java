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

    private static final String ORIG_WDCP_URL = "https://selenium.googlecode.com/git/java/client/src/com/thoughtworks/selenium/webdriven/WebDriverCommandProcessor.java";
    private static final String COPY_WDCP_PATH = "/patches/WebDriverCommandProcessor.java";

    private List<String> split(String s) {
        return Arrays.asList(s.split("\r?\n"));
    }

    @Test
    public void wdcpVersionTest() throws Exception {
        List<String> orig = split(IOUtils.toString(URI.create(ORIG_WDCP_URL)));
        List<String> copy = split(IOUtils.toString(getClass().getResourceAsStream(COPY_WDCP_PATH)));
        Patch<String> patch = DiffUtils.diff(copy, orig);
        assertThat(patch.getDeltas(), is(empty()));
    }
}
