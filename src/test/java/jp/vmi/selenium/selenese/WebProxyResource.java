package jp.vmi.selenium.selenese;

import org.junit.rules.ExternalResource;

/**
 * Web proxy resource class for test
 *
 * @author hayato
 */
public class WebProxyResource extends ExternalResource {
    private Proxy proxy;

    /**
     * get proxy instance
     * @return proxy server
     */
    public Proxy getProxy() {
        return proxy;
    }

    @Override
    protected void before() throws Throwable {
        proxy = new Proxy();
        proxy.start();
    }

    @Override
    protected void after() {
        proxy.kill();
    }

}
