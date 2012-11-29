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

    public void start() {
        port = NetUtils.getUsablePort();
        server = new DefaultHttpProxyServer(NetUtils.getUsablePort());
    }

    public void kill() {
        server.stop();
    }

    public int getPort() {
        return port;
    }

    public String getServerNameString() {
        return "localhost";
    }

}
