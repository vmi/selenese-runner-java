package jp.vmi.selenium.selenese.utils;

import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

@SuppressWarnings("javadoc")
@Ignore
public class LangUtilsTest {

    @Test
    public void capitalizeTest() {
        assertThat(LangUtils.capitalize(null), is(nullValue()));
        assertThat(LangUtils.capitalize(""), is(""));
        assertThat(LangUtils.capitalize("cat"), is("Cat"));
        assertThat(LangUtils.capitalize("cAt"), is("CAt"));
        assertThat(LangUtils.capitalize("'cat'"), is("'cat'"));
    }

    @Test
    public void uncapitalizeTest() {
        assertThat(LangUtils.uncapitalize(null), is(nullValue()));
        assertThat(LangUtils.uncapitalize(""), is(""));
        assertThat(LangUtils.uncapitalize("cat"), is("cat"));
        assertThat(LangUtils.uncapitalize("Cat"), is("cat"));
        assertThat(LangUtils.uncapitalize("CAT"), is("cAT"));
    }

    @Test
    public void containsIgnoreCaseTest() {
        assertThat(LangUtils.containsIgnoreCase(null, "abc"), is(false));
        assertThat(LangUtils.containsIgnoreCase("abc", null), is(false));
        assertThat(LangUtils.containsIgnoreCase("", ""), is(true));
        assertThat(LangUtils.containsIgnoreCase("abc", ""), is(true));
        assertThat(LangUtils.containsIgnoreCase("abc", "a"), is(true));
        assertThat(LangUtils.containsIgnoreCase("abc", "z"), is(false));
        assertThat(LangUtils.containsIgnoreCase("abc", "A"), is(true));
        assertThat(LangUtils.containsIgnoreCase("abc", "Z"), is(false));
    }

}
