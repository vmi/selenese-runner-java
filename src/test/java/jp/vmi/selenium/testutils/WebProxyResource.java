package jp.vmi.selenium.testutils;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.junit.rules.ExternalResource;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.openqa.selenium.net.PortProber;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

/**
 * Web proxy resource class for test.
 *
 * @author hayato
 */
public class WebProxyResource extends ExternalResource {

    /** Dummy URI for proxy test */
    public static final String DUMMY_URI = "http://dummy.example.com/";

    private final int proxyPort;
    private final HttpProxyServer proxyServer;
    private InetAddress localHost;
    private int localPort;
    private int count;

    /**
     * Constructor.
     */
    public WebProxyResource() {
        localHost = InetAddress.getLoopbackAddress();
        proxyPort = PortProber.findFreePort();
        proxyServer = DefaultHttpProxyServer.bootstrap()
            .withPort(proxyPort)
            .withFiltersSource(new HttpFiltersSourceAdapter() {
                @Override
                public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                    return new HttpFiltersAdapter(originalRequest) {
                        @Override
                        public InetSocketAddress proxyToServerResolutionStarted(String resolvingServerHostAndPort) {
                            String[] sp = resolvingServerHostAndPort.split(":", 2);
                            if (!sp[0].endsWith(".example.com"))
                                return null;
                            synchronized (WebProxyResource.this) {
                                count++;
                            }
                            // redirect to local web server from dummy URI.
                            return new InetSocketAddress(localHost, localPort);
                        }
                    };
                }
            }).start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> proxyServer.stop()));
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
