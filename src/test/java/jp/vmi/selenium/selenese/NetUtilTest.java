package jp.vmi.selenium.selenese;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class NetUtilTest {

    @Test
    public void usablePort() {
        for (int i = 0; i < 1000; i++) {
            int port = NetUtil.getUsablePort();
            assertTrue(NetUtil.PORTNUM_MIN < port);
            assertTrue(port < NetUtil.PORTNUM_MAX);
        }
    }

    @Test
    public void testCanUseMethod() {
        Proxy proxy = new Proxy();
        proxy.start();
        try {
            assertThat(NetUtil.canUse(proxy.getPort()), is(not(true)));
        } finally {
            proxy.kill();
        }
    }
}
