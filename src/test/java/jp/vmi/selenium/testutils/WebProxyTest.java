package jp.vmi.selenium.testutils;

import org.junit.Test;
import org.openqa.selenium.net.PortProber;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Test for {@link WebProxy}.
 */
public class WebProxyTest {

    /**
     * Test of start() and kill() continuously.
     */
    @Test
    public void continuouslyInvoke() {
        for (int i = 0; i < 20; i++) {
            WebProxy proxy = new WebProxy();
            proxy.start();
            proxy.kill();
        }
    }

    /**
     * Test of start() and kill().
     */
    @Test(timeout = 30000)
    public void startAndKill() {
        WebProxy proxy = new WebProxy();
        proxy.start();
        proxy.kill();
        assertThat(PortProber.pollPort(proxy.getPort()), is(true));
    }
}
