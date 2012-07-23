package jp.vmi.selenium.selenese;

import static org.junit.Assert.*;

import org.junit.Test;

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

}
