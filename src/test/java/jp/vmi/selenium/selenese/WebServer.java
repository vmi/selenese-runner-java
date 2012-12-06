package jp.vmi.selenium.selenese;

import java.io.File;

import org.webbitserver.WebServers;
import org.webbitserver.handler.StaticFileHandler;

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
        port = NetUtils.getUsablePort();

        File classpath = new File(this.getClass().getResource("").getPath());
        File documentroot = new File(classpath, "htdocs");
        server = WebServers.createWebServer(port)
            //.add(new BasicAuthenticationHandler(new InMemoryPasswords().add("user", "pass")))
            .add(new StaticFileHandler(documentroot))
            .add("/redirect", new RedirectHandler("http://" + getServerNameString() + "/index.html"));
        server.start();
    }

    /**
     * stop web server.
     */
    public void stop() {
        server.stop();
    }

    /**
     * get server name
     * @return server name
     */
    public String getServerNameString() {
        return "localhost:" + port;
    }
}
