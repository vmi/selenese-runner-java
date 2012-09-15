package jp.vmi.selenium.selenese;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Test for NetUtils.
 */
public class NetUtilsTest {

    /**
     * Test of getUsablePort().
     */
    @Test
    public void testGetUsablePort() {
        for (int i = 0; i < 1000; i++) {
            int port = NetUtils.getUsablePort();
            assertThat(port, is(greaterThan(1024)));
            assertThat(port, is(lessThan(65536)));
        }
    }
}
