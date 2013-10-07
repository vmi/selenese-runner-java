package jp.vmi.selenium.testutils;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.littleshoot.proxy.DefaultHttpProxyServer;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.HttpRequestFilter;
import org.openqa.selenium.net.PortProber;

/**
 * Proxy for unit test.
 */
public class WebProxy {

    private int count = 0;

    HttpProxyServer server = null;

    int port = 0;

    /**
     * Constructor.
     */
    public WebProxy() {
    }

    /**
     * start proxy.
     */
    public void start() {
        port = PortProber.findFreePort();
        server = new DefaultHttpProxyServer(port, new HttpRequestFilter() {
            @Override
            public void filter(HttpRequest httpRequest) {
                count++;
            }
        });
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
