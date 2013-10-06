package jp.vmi.selenium.testutil;

import java.io.File;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.net.PortProber;
import org.webbitserver.WebServers;
import org.webbitserver.handler.StaticFileHandler;
import org.webbitserver.handler.authentication.BasicAuthenticationHandler;
import org.webbitserver.handler.authentication.InMemoryPasswords;

import jp.vmi.selenium.selenese.RedirectHandler;

/**
 * Webserver for unit test.
 * TODO: merge to Proxy.java
 */
public class WebServer {

    private static final int STOP_RETRY_COUNT = 3;

    private int port;

    private org.webbitserver.WebServer server;

    /**
     * start web server.
     */
    public void start() {
        port = PortProber.findFreePort();
        File htdocs = FileUtils.toFile(getClass().getResource("/htdocs"));
        server = WebServers.createWebServer(port)
            .add(new StaticFileHandler(htdocs))
            .add("/basic", new BasicAuthenticationHandler(new InMemoryPasswords().add("user", "pass")))
            .add("/redirect", new RedirectHandler("http://" + getServerNameString() + "/index.html"))
            .add("/basic/redirect", new RedirectHandler("http://" + getServerNameString() + "/basic/index.html"));
        try {
            server.start().get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * stop web server.
     */
    public void stop() {
        try {
            int retry = STOP_RETRY_COUNT;
            while (true) {
                try {
                    server.stop().get();
                    break;
                } catch (AssertionError e) {
                    // WebServer.stop() throw AssertionError frequently, and retry stopping.
                    if (--retry > 0)
                        throw e;
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get server name.
     *
     * @return server name
     */
    public String getServerNameString() {
        return "localhost:" + port;
    }
}
