package jp.vmi.selenium.testutils;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.function.BiFunction;

import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import jp.vmi.selenium.testutils.proxy.HttpFiltersRegistrar;
import jp.vmi.selenium.testutils.proxy.ParentProxyHandler;
import jp.vmi.selenium.testutils.proxy.ProxyActivityTracker;
import jp.vmi.selenium.testutils.proxy.ProxyBasicAuhthenticator;
import jp.vmi.selenium.testutils.proxy.HttpFiltersRegistrar.ProxyContext;

/**
 * Proxy server for unit test.
 */
public class ProxyServer {

    static final Logger log = LoggerFactory.getLogger(ProxyServer.class);

    /**
     * ProxyServer builder.
     */
    @SuppressWarnings("javadoc")
    public static class Builder {

        private final int port;
        private String userName = null;
        private String password = null;
        private String parentHost = null;
        private int parentPort;
        private String parentUserName = null;
        private String parentPasswrord = null;
        private boolean useShutdownHook = false;
        private BiFunction<ProxyContext, String, InetSocketAddress> proxyToServerResolutionStarted = null;

        private Builder(int port) {
            this.port = port;
            log.info("Proxy server port: {}", port);
        }

        private static String getValueOrNull(String s) {
            return Objects.equals(s, "-") ? null : s;
        }

        private static String getValueOrEmpty(String s) {
            return s == null || s.equals("-") ? "" : s;
        }

        public Builder withUserName(String userName) {
            this.userName = userName = getValueOrNull(userName);
            if (userName != null)
                log.info("Proxy server user name: {}", userName);
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password = getValueOrEmpty(password);
            if (password != null)
                log.info("Proxy server password: {}", password);
            return this;
        }

        public Builder withParentHost(String parentHost) {
            this.parentHost = parentHost = getValueOrNull(parentHost);
            if (parentHost != null)
                log.info("Parent proxy server host: {}", parentHost);
            return this;
        }

        public Builder withParentPort(int parentPort) {
            this.parentPort = parentPort;
            log.info("Parent proxy server port: {}", this.parentPort);
            return this;
        }

        public Builder withParentPort(String parentPort) {
            if (parentPort != null && !parentPort.equals("-")) {
                try {
                    withParentPort(Integer.parseInt(parentPort));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Illegal parent proxy port: " + parentPort, e);
                }
            }
            return this;
        }

        public Builder withParentUserName(String parentUserName) {
            this.parentUserName = parentUserName = getValueOrNull(parentUserName);
            if (parentUserName != null)
                log.info("Parent proxy server user name: {}", parentUserName);
            return this;
        }

        public Builder withParentPassword(String parentPassword) {
            this.parentPasswrord = parentPassword = getValueOrEmpty(parentPasswrord);
            if (parentPassword != null)
                log.info("Parent proxy server password: ********");
            return this;
        }

        public Builder useShutdownHook(boolean useShutdownHook) {
            this.useShutdownHook = useShutdownHook;
            log.info("Use shutdown hook: {}", useShutdownHook ? "YES" : "NO");
            return this;
        }

        public Builder withProxyToServerResolutionStarted(BiFunction<ProxyContext, String, InetSocketAddress> proxyToServerResolutionStarted) {
            this.proxyToServerResolutionStarted = proxyToServerResolutionStarted;
            return this;
        }

        public ProxyServer build() {
            ParentProxyHandler parentProxyHandler = ParentProxyHandler.createIfNeeded(parentHost, parentPort, parentUserName, parentPasswrord);
            ProxyServer proxyServer = new ProxyServer(port, userName, password, parentProxyHandler, useShutdownHook);
            if (proxyToServerResolutionStarted != null)
                proxyServer.setProxyToServerResolutionStarted(proxyToServerResolutionStarted);
            return proxyServer;
        }
    }

    /**
     * Create ProxyServer builder instance.
     *
     * @param port proxy server port.
     * @return ProxyServer builder.
     */
    public static Builder builder(int port) {
        return new Builder(port);
    }

    /**
     * Create ProxyServer builder instance.
     *
     * @param port proxy server port.
     * @return ProxyServer builder.
     */
    public static Builder builder(String port) {
        return new Builder(Integer.parseInt(port));
    }

    private final int port;
    private final String userName;
    private final String password;
    private final ParentProxyHandler parentProxyHandler;
    private final boolean useShutdownHook;
    private ProxyActivityTracker proxyActivityTracker = null;
    private final HttpFiltersRegistrar registrar = new HttpFiltersRegistrar();
    private HttpProxyServer proxyServer = null;
    private Thread shutdownHook = null;

    /**
     * Constructor.
     *
     * @param port proxy server port.
     * @param userName proxy user name or null.
     * @param password proxy password or null.
     * @param parentProxyHandler handler of parnet proxy server or null.
     * @param useShutdownHook true if use shutdown hook.
     */
    public ProxyServer(int port, String userName, String password, ParentProxyHandler parentProxyHandler, boolean useShutdownHook) {
        this.port = port;
        this.userName = userName;
        this.password = password;
        this.parentProxyHandler = parentProxyHandler;
        this.useShutdownHook = useShutdownHook;
    }

    @SuppressWarnings("javadoc")
    public void setProxyToServerResolutionStarted(BiFunction<ProxyContext, String, InetSocketAddress> proxyToServerResolutionStarted) {
        registrar.setProxyToServerResolutionStarted(proxyToServerResolutionStarted);
    }

    @SuppressWarnings("javadoc")
    public void setProxyToServerRequest(BiFunction<ProxyContext, HttpObject, HttpResponse> proxyToServerRequest) {
        registrar.setProxyToServerRequest(proxyToServerRequest);
    }

    /**
     * Start proxy server.
     */
    public synchronized void start() {
        if (proxyServer != null) {
            log.warn("Proxy server has been already started.");
            return;
        }
        proxyActivityTracker = new ProxyActivityTracker();
        HttpProxyServerBootstrap bootstrap = DefaultHttpProxyServer.bootstrap()
            .withAllowLocalOnly(true)
            .withPort(port)
            .plusActivityTracker(proxyActivityTracker)
            .withFiltersSource(new HttpFiltersSourceAdapter() {
                @Override
                public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                    return registrar.newHttpFilters(originalRequest, ctx);
                }
            });
        if (userName != null)
            bootstrap.withProxyAuthenticator(new ProxyBasicAuhthenticator(userName, password));
        if (parentProxyHandler != null) {
            bootstrap.withChainProxyManager(parentProxyHandler.createChainedProxyManager());
            parentProxyHandler.createProxyAuthorizationAppenderIfNeeded().ifPresent(registrar::setProxyToServerRequest);
        }
        proxyServer = bootstrap.start();
        if (useShutdownHook && shutdownHook == null) {
            shutdownHook = new Thread(() -> stop(true));
            Runtime.getRuntime().addShutdownHook(shutdownHook);
            log.info("Add shutdown hook.");
        }
        log.info("Proxy server is started.");
    }

    /**
     * Stop proxy server.
     *
     * @param isCalledByHook true if stop by shutdown hook.
     */
    public synchronized void stop(boolean isCalledByHook) {
        if (proxyServer == null) {
            return;
        }
        proxyServer.stop();
        proxyServer = null;
        if (!isCalledByHook && shutdownHook != null) {
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
            log.info("Remove shutdown hook.");
        }
        shutdownHook = null;
        log.info("Proxy server is stopped.");
    }

    /**
     * Stop proxy server.
     */
    public void stop() {
        stop(false);
    }

    private static void help() {
        System.out.println("Usage: ProxyServer PORT [PROXY_USER PROXY_PASSWORD [PARENT_PROXY_HOST PARENT_PROXY_PORT [PARENT_PROXY_USER PARENT_PROXY_PASSWORD]]]");
        System.out.println();
        System.out.println("* Note: Specify \"-\" if you want to set the parameter to empty.");
        System.exit(1);
    }

    private static class ArgsGetter {
        private final String[] args;
        private int index = 0;

        public ArgsGetter(String[] args) {
            this.args = args;
        }

        public String next() {
            return hasNext() ? args[index++] : null;
        }

        public boolean hasNext() {
            return index < args.length;
        }
    }

    /**
     * Start test proxy server.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        if (args.length == 0)
            help();
        ArgsGetter ag = new ArgsGetter(args);
        Builder builder = builder(ag.next())
            .withUserName(ag.next())
            .withPassword(ag.next())
            .withParentHost(ag.next())
            .withParentPort(ag.next())
            .withParentUserName(ag.next())
            .withParentPassword(ag.next())
            .useShutdownHook(true);
        if (ag.hasNext()) {
            StringBuilder msg = new StringBuilder("* Illegal arguments:");
            do {
                msg.append(" ").append(ag.next());
            } while (ag.hasNext());
            System.out.println(msg);
            System.out.println();
            help();
        }
        ProxyServer server = builder.build();
        server.start();
    }
}
