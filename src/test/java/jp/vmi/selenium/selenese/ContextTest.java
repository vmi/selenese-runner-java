package jp.vmi.selenium.selenese;

import org.junit.Test;

import static org.junit.Assert.*;

public class ContextTest {

    @Test
    public void replaceVariable() {
        Context c = new Context(null, null);
        c.setVariable("XYZ", "a");
        assertEquals("XYZ", c.replaceVariables("${a}"));
    }

    @Test
    public void replaceVariables() {
        Context c = new Context(null, null);
        c.setVariable("XYZ", "a");
        assertArrayEquals(new String[] { "abc", "XYZ", "abcXYZbca" }, c.replaceVariables(new String[] { "abc", "${a}", "abc${a}bca" }));
    }
}
