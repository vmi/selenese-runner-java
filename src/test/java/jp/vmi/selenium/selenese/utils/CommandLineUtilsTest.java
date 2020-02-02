package jp.vmi.selenium.selenese.utils;

import org.junit.Test;

import static jp.vmi.selenium.selenese.utils.CommandLineUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

@SuppressWarnings("javadoc")
public class CommandLineUtilsTest {

    @Test
    public void testWin() {
        StringBuilder buffer;
        buffer = new StringBuilder();
        addEscapedArgumentOnWindows(buffer, "abc");
        assertThat(buffer.toString(), is("\"abc\""));
        buffer = new StringBuilder();
        addEscapedArgumentOnWindows(buffer, "a\\b\\c");
        assertThat(buffer.toString(), is("\"a\\b\\c\""));
        buffer = new StringBuilder();
        addEscapedArgumentOnWindows(buffer, "a\\b\\c\\");
        assertThat(buffer.toString(), is("\"a\\b\\c\\\\\""));
        buffer = new StringBuilder();
        addEscapedArgumentOnWindows(buffer, "%abc%");
        assertThat(buffer.toString(), is("%\"abc\"%"));
        buffer = new StringBuilder();
        addEscapedArgumentOnWindows(buffer, "%a\\b\\c\\%");
        assertThat(buffer.toString(), is("%\"a\\b\\c\\\\\"%"));
    }

    @Test
    public void testUnix() {
        StringBuilder buffer;
        buffer = new StringBuilder();
        addEscapedArgumentOnUnix(buffer, "abc");
        assertThat(buffer.toString(), is("'abc'"));
        buffer = new StringBuilder();
        addEscapedArgumentOnUnix(buffer, "a'b'c");
        assertThat(buffer.toString(), is("'a'\\''b'\\''c'"));
        buffer = new StringBuilder();
        addEscapedArgumentOnUnix(buffer, "'abc'");
        assertThat(buffer.toString(), is("\\''abc'\\'"));
    }
}
