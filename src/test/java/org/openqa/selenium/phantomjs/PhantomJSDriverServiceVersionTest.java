package org.openqa.selenium.phantomjs;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import difflib.DiffUtils;
import difflib.Patch;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@SuppressWarnings("javadoc")
public class PhantomJSDriverServiceVersionTest {

    private static final String ORIG_PJSDS_URL = "https://raw.github.com/detro/ghostdriver/master/binding/java/src/main/java/org/openqa/selenium/phantomjs/PhantomJSDriverService.java";
    private static final String COPY_PJSDS_PATH = "/patches/PhantomJSDriverService.java";

    private List<String> split(String s) {
        return Arrays.asList(s.split("\r?\n"));
    }

    @Test
    public void versionTest() throws Exception {
        List<String> orig = split(IOUtils.toString(URI.create(ORIG_PJSDS_URL)));
        List<String> copy = split(IOUtils.toString(getClass().getResourceAsStream(COPY_PJSDS_PATH)));
        Patch<String> patch = DiffUtils.diff(copy, orig);
        assertThat(patch.getDeltas(), is(empty()));
    }
}
