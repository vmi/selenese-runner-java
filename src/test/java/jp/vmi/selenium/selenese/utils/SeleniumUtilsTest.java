package jp.vmi.selenium.selenese.utils;

import org.junit.Test;

import static jp.vmi.selenium.selenese.utils.SeleniumUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@SuppressWarnings("javadoc")
public class SeleniumUtilsTest {

    @Test
    public void testNormalizeSpaces() throws Exception {
        assertThat(normalizeSpaces(""), is(""));
        assertThat(normalizeSpaces(" "), is(""));
        assertThat(normalizeSpaces("   "), is(""));
        assertThat(normalizeSpaces(" abc"), is("abc"));
        assertThat(normalizeSpaces("   abc"), is("abc"));
        assertThat(normalizeSpaces("abc "), is("abc"));
        assertThat(normalizeSpaces("abc   "), is("abc"));
        assertThat(normalizeSpaces("abc def"), is("abc def"));
        assertThat(normalizeSpaces("abc   def"), is("abc def"));
        assertThat(normalizeSpaces("abc\u00A0def"), is("abc def"));
        assertThat(normalizeSpaces("abc \u00A0def"), is("abc def"));
        assertThat(normalizeSpaces("\n\n\n"), is("\n\n\n"));
        assertThat(normalizeSpaces(" \n "), is("\n"));
        assertThat(normalizeSpaces(" abc \n def "), is("abc \n def"));
        assertThat(normalizeSpaces("abc\ndef\n"), is("abc\ndef\n"));
        assertThat(normalizeSpaces("abc def\nghi jkl"), is("abc def\nghi jkl"));
    }
}
