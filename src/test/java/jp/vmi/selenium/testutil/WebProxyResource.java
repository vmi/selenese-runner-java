package jp.vmi.selenium.testutil;

import org.junit.rules.ExternalResource;

/**
 * Web proxy resource class for test
 *
 * @author hayato
 */
public class WebProxyResource extends ExternalResource {
    private WebProxy proxy;

    /**
     * get proxy instance
     * @return proxy server
     */
    public WebProxy getProxy() {
        return proxy;
    }

    @Override
    protected void before() throws Throwable {
        proxy = new WebProxy();
        proxy.start();
    }

    @Override
    protected void after() {
        proxy.kill();
    }

}
