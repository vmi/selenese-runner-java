package jp.vmi.selenium.testutils;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.junit.rules.ExternalResource;
import org.openqa.selenium.net.PortProber;

/**
 * Web proxy resource class for test.
 *
 * @author hayato
 */
public class WebProxyResource extends ExternalResource {

    /** Dummy URI for proxy test */
    public static final String DUMMY_URI = "http://dummy.example.com/";

    private int proxyPort;
    private int localPort;
    private int count;
    private final ProxyServer proxyServer;

    /**
     * Constructor.
     */
    public WebProxyResource() {
        InetAddress localHost = InetAddress.getLoopbackAddress();
        proxyPort = PortProber.findFreePort();
        proxyServer = ProxyServer.builder(proxyPort)
            .withProxyToServerResolutionStarted((context, resolvingServerHostAndPort) -> {
                String[] sp = resolvingServerHostAndPort.split(":", 2);
                if (!sp[0].endsWith(".example.com"))
                    return null;
                synchronized (WebProxyResource.this) {
                    count++;
                }
                // redirect to local web server from dummy URI.
                return new InetSocketAddress(localHost, localPort);
            })
            .useShutdownHook(true)
            .build();
        proxyServer.start();
    }

    /**
     * Set port number of local web server.
     *
     * @param localPort port number.
     */
    public synchronized void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    /**
     * Reset request count.
     */
    public synchronized void resetCount() {
        count = 0;
    }

    /**
     * Get request count.
     *
     * @return request count.
     */
    public synchronized int getCount() {
        return count;
    }

    /**
     * Get proxyPort number.
     *
     * @return proxyPort number.
     */
    public int getPort() {
        return proxyPort;
    }

    /**
     * Get server name.
     *
     * @return server name.
     */
    public String getServerNameString() {
        return "localhost:" + proxyPort;
    }
}
