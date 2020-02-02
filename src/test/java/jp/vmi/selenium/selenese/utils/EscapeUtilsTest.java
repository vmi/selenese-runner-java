package jp.vmi.selenium.selenese.utils;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

@SuppressWarnings("javadoc")
public class EscapeUtilsTest {

    @Test
    public void testEscapeHtml1() {
        String expected = "&lt;abc&gt;&amp;&quot;&#39;&#92;<br>\n";
        String actual = EscapeUtils.escapeHtml("<abc>&\"'\\\r\n", true);
        System.out.println("* HTML1\n  exp: [" + expected + "]\n  act: [" + actual + "]");
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testEscapeHtml2() {
        String expected = "&lt;abc&gt;&amp;&quot;&#39;&#92;\n";
        String actual = EscapeUtils.escapeHtml("<abc>&\"'\\\r\n", false);
        System.out.println("* HTML2\n  exp: [" + expected + "]\n  act: [" + actual + "]");
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testEncodeUri() {
        String expected = "%09%0d%0a%20!%22#$%25&'()*+,-./0123456789:;%3c=%3e?@ABCDEFGHIJKLMNOPQRSTUVWXYZ%5b%5c%5d%5e_%60abcdefghijklmnopqrstuvwxyz%7b%7c%7d~%e3%81%82";
        String actual = EscapeUtils.encodeUri("\t\r\n !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u3042");
        System.out.println("* URI\n  exp: [" + expected + "]\n  act: [" + actual + "]");
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testEscapeJSString() {
        String expected = "\\u0000\\b\\t\\n\\v\\f\\r\\\"\\'\\\\\\u2028\\u2029";
        String actual = EscapeUtils.escapeJSString("\u0000\b\t\n\u000b\f\r\"\'\\\u2028\u2029");
        System.out.println("* JSString\n  exp: [" + expected + "]\n  act: [" + actual + "]");
        assertThat(actual, equalTo(expected));
    }
}
