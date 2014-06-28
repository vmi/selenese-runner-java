package jp.vmi.selenium.selenese.utils;

import java.io.File;

import org.apache.commons.exec.OS;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@SuppressWarnings("javadoc")
public class PathUtilsTest {

    @Test
    public void testSearchExecutableFiles() {
        String exeName = OS.isFamilyWindows() ? "cmd" : "ls";
        File file = PathUtils.searchExecutableFile(exeName);
        assertThat(file, is(notNullValue()));
    }

    public String sep(String s) {
        return s.replace('/', File.separatorChar);
    }

    @Test
    public void testNormalize() {
        assertThat(PathUtils.normalize("a/b/c"), equalTo(sep("a/b/c")));
        assertThat(PathUtils.normalize("./a/b/c"), equalTo(sep("a/b/c")));
        String userDir = System.getProperty("user.dir");
        String parentDir = new File(userDir).getParent();
        assertThat(PathUtils.normalize("../a/b/c"), equalTo(sep(parentDir + "/a/b/c")));
        assertThat(PathUtils.normalize("a/b/../c"), equalTo(sep("a/c")));
    }
}
