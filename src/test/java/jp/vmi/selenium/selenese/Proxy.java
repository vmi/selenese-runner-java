package jp.vmi.selenium.selenese;

import java.util.HashMap;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.littleshoot.proxy.DefaultHttpProxyServer;
import org.littleshoot.proxy.HttpFilter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.HttpRequestFilter;

/**
 * Proxy for unit test.
 */
public class Proxy {

    private int count = 0;

    HttpProxyServer server = null;

    int port = 0;

    /**
     * Constructor.
     */
    public Proxy() {
    }

    /**
     * start proxy.
     */
    public void start() {
        port = NetUtils.getUsablePort();
        server = new DefaultHttpProxyServer(port, new HttpRequestFilter() {
            @Override
            public void filter(HttpRequest httpRequest) {
                count++;
            }
        }, new HashMap<String, HttpFilter>());
        server.start();
    }

    /**
     * kill proxy.
     */
    public void kill() {
        server.stop();
    }

    /**
     * get port number
     * @return port number
     */
    public int getPort() {
        return port;
    }

    /**
     * get request count
     * @return request count.
     */
    public int getCount() {
        return count;
    }

    /**
     * get server name (ex. localhost)
     * @return server name
     */
    public String getServerNameString() {
        return "localhost:" + getPort();
    }

}
