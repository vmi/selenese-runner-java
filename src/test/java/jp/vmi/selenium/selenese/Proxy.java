package jp.vmi.selenium.selenese;

import org.littleshoot.proxy.DefaultHttpProxyServer;
import org.littleshoot.proxy.HttpProxyServer;

/**
 * Proxy for unit test.
 */
public class Proxy {

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
        server = new DefaultHttpProxyServer(NetUtils.getUsablePort());
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
     * get server name (ex. localhost)
     * @return server name
     */
    public String getServerNameString() {
        return "localhost";
    }

}
