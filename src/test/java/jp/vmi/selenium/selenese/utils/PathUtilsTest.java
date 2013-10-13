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
}
