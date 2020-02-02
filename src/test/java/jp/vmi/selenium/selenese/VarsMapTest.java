package jp.vmi.selenium.selenese;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

@SuppressWarnings("javadoc")
public class VarsMapTest {

    @Test
    public void test() {
        VarsMap varsMap = new VarsMap();
        varsMap.put("abc", "ABC");
        varsMap.put("def", "ABC\nXYZ");
        assertThat(varsMap.replaceVars(true, "***${abc}+++${def}---"), is("***\"ABC\"+++\"ABC\\nXYZ\"---"));
        assertThat(varsMap.replaceVars(false, "***${abc}+++${def}---"), is("***ABC+++ABC\nXYZ---"));
    }
}
