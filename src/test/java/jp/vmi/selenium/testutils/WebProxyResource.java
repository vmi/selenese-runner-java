package jp.vmi.selenium.testutils;

import org.junit.rules.ExternalResource;

/**
 * Web proxy resource class for test.
 *
 * @author hayato
 */
public class WebProxyResource extends ExternalResource {

    private WebProxy proxy;

    @Override
    protected void before() throws Throwable {
        proxy = new WebProxy();
        proxy.start();
    }

    @Override
    protected void after() {
        proxy.stop();
    }

    /**
     * Reset request count.
     */
    public void resetCount() {
        proxy.resetCount();
    }

    /**
     * Get request count.
     *
     * @return request count.
     */
    public int getCount() {
        return proxy.getCount();
    }

    /**
     * Get port number.
     *
     * @return port number.
     */
    public int getPort() {
        return proxy.getPort();
    }

    /**
     * Get server name.
     * 
     * @return server name.
     */
    public String getServerNameString() {
        return proxy.getServerNameString();
    }
}
