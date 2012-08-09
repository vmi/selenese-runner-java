package jp.vmi.selenium.selenese;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Test for {@link Proxy}.
 */
public class ProxyTest {

    @Test
    public void continuouslyInvoke() {
        for (int i = 0; i < 20; i++) {
            Proxy proxy = new Proxy();
            proxy.start();
            proxy.kill();
        }
    }

    @Test(timeout = 10000)
    public void startAndKill() {
        Proxy proxy = new Proxy();
        proxy.start();
        proxy.kill();
        assertThat(NetUtil.canUse(proxy.getPort()), is(true));
    }
}
