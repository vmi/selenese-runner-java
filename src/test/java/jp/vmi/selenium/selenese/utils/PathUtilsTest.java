package jp.vmi.selenium.selenese.utils;

import java.io.File;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@SuppressWarnings("javadoc")
public class PathUtilsTest {

    private static String sep(String s) {
        return s.replace('/', File.separatorChar);
    }

    @Test
    public void testNormalize() {
        assertThat(PathUtils.normalizeSeparator("a/b/c"), equalTo(sep("a/b/c")));
        assertThat(PathUtils.normalizeSeparator("a\\b\\c"), equalTo(sep("a/b/c")));
        assertThat(PathUtils.normalize("a/b/c"), equalTo(sep("a/b/c")));
        assertThat(PathUtils.normalize("./a/b/c"), equalTo(sep("a/b/c")));
        String userDir = System.getProperty("user.dir");
        String parentDir = new File(userDir).getParent();
        assertThat(PathUtils.normalize("../a/b/c"), equalTo(sep(parentDir + "/a/b/c")));
        assertThat(PathUtils.normalize("a/b/../c"), equalTo(sep("a/c")));
        assertThat(PathUtils.concat("a", "b"), equalTo(sep("a/b")));
        assertThat(PathUtils.concat("a/", "b"), equalTo(sep("a/b")));
        assertThat(PathUtils.concat("./a", "b"), equalTo(sep("a/b")));
        assertThat(PathUtils.concat("a", "../b"), equalTo(sep("b")));
        assertThat(PathUtils.concat("../a", "b"), equalTo(sep(parentDir + "/a/b")));
        assertThat(PathUtils.concat("../a", "../b"), equalTo(sep(parentDir + "/b")));
    }
}
