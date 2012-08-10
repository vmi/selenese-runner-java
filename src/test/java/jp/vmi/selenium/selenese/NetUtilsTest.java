package jp.vmi.selenium.selenese;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
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
            assertTrue(NetUtils.PORTNUM_MIN < port);
            assertTrue(port < NetUtils.PORTNUM_MAX);
        }
    }

    /**
     * Test of canUse().
     */
    @Test
    public void testCanUse() {
        Proxy proxy = new Proxy();
        proxy.start();
        try {
            assertThat(NetUtils.canUse(proxy.getPort()), is(not(true)));
        } finally {
            proxy.kill();
        }
    }
}
