package jp.vmi.selenium.selenese;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class ProxyTest {

    @Test
    public void usablePort() {
        for (int i = 0; i < 1000; i++) {
            int port = Proxy.getUsablePort();
            assertTrue(1024 < port);
            assertTrue(port < 65536);
        }
    }

    @Test
    public void 連続起動() {
        for (int i = 0; i < 20; i++) {
            Proxy proxy = new Proxy();
            proxy.start();
            proxy.stop();
        }
    }

    @Test
    public void testCanUseMethod() {
        Proxy proxy = new Proxy();
        proxy.start();
        try {
            assertThat(Proxy.canUse(proxy.getPort()), is(not(true)));
        } finally {
            proxy.stop();
        }
    }

    @Test(timeout = 10000)
    public void startAndKill() {
        Proxy proxy = new Proxy();
        proxy.start();
        proxy.kill();
        assertThat(Proxy.canUse(proxy.getPort()), is(true));
    }
}
