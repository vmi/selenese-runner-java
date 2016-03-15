package jp.vmi.selenium.selenese.cmdproc;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import difflib.DiffUtils;
import difflib.Patch;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 * Included WDCP (WebDriverCommandProcessor) is copyed from Selenium and patched for me.
 *
 * This test will fail if original WDCP is changed.
 */
@SuppressWarnings("javadoc")
public class WebDriverCommandProcessorVersionTest {

    private static final Logger log = LoggerFactory.getLogger(WebDriverCommandProcessorVersionTest.class);

    private static final String ORIG_PKG_DIR = "java/client/src/com/thoughtworks/selenium/webdriven";
    private static final String[] ORIG_JAVA_FILES = { "WebDriverCommandProcessor.java", "Windows.java" };
    private static final String COPIED_PATH = "/patches/";

    private List<String> split(String s) {
        return Arrays.asList(s.split("\r?\n"));
    }

    @Test
    public void wdcpVersionTest() throws Exception {
        // git pull if selenium working copy exists.
        String home = System.getProperty("user.home");
        File seleniumDir = new File(home, "git/selenium");
        assumeTrue("Missing Selenium working copy", seleniumDir.isDirectory());
        ProcessBuilder pb = new ProcessBuilder("git", "pull");
        pb.directory(seleniumDir);
        pb.redirectErrorStream(true);
        Process gitPull = pb.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(gitPull.getInputStream()));
        String line;
        log.info("git pull");
        while ((line = r.readLine()) != null)
            log.info("[{}]", line);
        assertThat(gitPull.waitFor(), is(0));
        // check diff.
        File pkgDir = new File(seleniumDir, ORIG_PKG_DIR);
        for (String javaFile : ORIG_JAVA_FILES) {
            List<String> orig = split(IOUtils.toString(new File(pkgDir, javaFile).toURI(), StandardCharsets.UTF_8));
            String[] splitted = javaFile.split("/");
            String resName = COPIED_PATH + splitted[splitted.length - 1];
            List<String> copy = split(IOUtils.toString(getClass().getResourceAsStream(resName), StandardCharsets.UTF_8));
            Patch<String> patch = DiffUtils.diff(copy, orig);
            assertThat(patch.getDeltas(), is(empty()));
        }
    }
}
