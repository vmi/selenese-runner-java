package jp.vmi.selenium.testutils;

import org.junit.rules.ExternalResource;

/**
 * Web server for unit test.
 */
public class WebServerResouce extends ExternalResource {

    private WebServer server;

    /**
     * Get webserver.
     *
     * @return webserver.
     */
    public WebServer getServer() {
        return server;
    }

    /**
     * Get webcome page URL.
     * 
     * @return URL.
     */
    public String getUrl() {
        return "http://" + server.getServerNameString() + "/";
    }

    @Override
    protected void before() throws Throwable {
        server = new WebServer();
        server.start();
    }

    @Override
    protected void after() {
        server.stop();
    }
}
