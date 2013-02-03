package jp.vmi.selenium.selenese;

import java.io.File;
import java.util.concurrent.ExecutionException;

import org.openqa.selenium.net.PortProber;
import org.webbitserver.WebServers;
import org.webbitserver.handler.StaticFileHandler;
import org.webbitserver.handler.authentication.BasicAuthenticationHandler;
import org.webbitserver.handler.authentication.InMemoryPasswords;

/**
 * Webserver for unit test.
 * TODO: merge to Proxy.java
 */
public class WebServer {

    int port;

    org.webbitserver.WebServer server;

    /**
     * constructor.
     */
    public WebServer() {
        super();
    }

    /**
     * start web server.
     */
    public void start() {
        port = PortProber.findFreePort();

        File classpath = new File(this.getClass().getResource("").getPath());
        File documentroot = new File(classpath, "htdocs");
        server = WebServers.createWebServer(port)
            .add("/basic", new BasicAuthenticationHandler(new InMemoryPasswords().add("user", "pass")))
            .add(new StaticFileHandler(documentroot))
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
            server.stop().get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * get server name
     * @return server name
     */
    public String getServerNameString() {
        return "localhost:" + port;
    }
}
