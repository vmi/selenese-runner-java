package jp.vmi.selenium.testutils;

import org.junit.rules.ExternalResource;

/**
 * Web server for unit test.
 */
public class WebServerResouce extends ExternalResource {

    private WebServer server;

    @Override
    protected void before() throws Throwable {
        server = new WebServer();
        server.start();
    }

    @Override
    protected void after() {
        server.stop();
    }

    /**
     * Get server name.
     * 
     * @return server name.
     */
    public String getServerNameString() {
        return server.getServerNameString();
    }

    /**
     * Get base URL.
     * 
     * @return URL.
     */
    public String getBaseURL() {
        return server.getBaseURL();
    }

    public void setFqdn(String fqdn) {
        server.setFqdn(fqdn);
    }
}
