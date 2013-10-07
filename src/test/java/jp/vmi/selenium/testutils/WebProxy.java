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

    private int port;

    private HttpProxyServer server;

    private int count = 0;

    /**
     * Start proxy server.
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
     * Stop proxy server.
     */
    public void stop() {
        server.stop();
    }

    /**
     * Get request count.
     *
     * @return request count.
     */
    public int getCount() {
        return count;
    }

    /**
     * Get port number.
     *
     * @return port number.
     */
    public int getPort() {
        return port;
    }

    /**
     * Get server name.
     *
     * @return server name.
     */
    public String getServerNameString() {
        return "localhost:" + port;
    }
}
