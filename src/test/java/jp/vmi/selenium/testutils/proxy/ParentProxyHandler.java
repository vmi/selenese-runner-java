package jp.vmi.selenium.testutils.proxy;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

import org.littleshoot.proxy.ChainedProxyAdapter;
import org.littleshoot.proxy.ChainedProxyManager;

import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import jp.vmi.selenium.testutils.proxy.HttpFiltersRegistrar.ProxyContext;

/**
 * Parent proxy handler.
 */
public class ParentProxyHandler {

    /**
     * Create ParentProxyHandler instance if host is not null.
     *
     * @param host parent proxy host name or null.
     * @param port parent proxy port number.
     * @param userName parent proxy user name.
     * @param password parent proxy password.
     * @return ParentProxyHandler instance or null.
     */
    public static ParentProxyHandler createIfNeeded(String host, int port, String userName, String password) {
        if (host == null)
            return null;
        return new ParentProxyHandler(host, port, userName, password);
    }

    private final InetSocketAddress parentHostAndPort;
    private final String proxyAuth;
    private Pattern noParentProxy = null;

    private ParentProxyHandler(String host, int port, String userName, String password) {
        parentHostAndPort = new InetSocketAddress(host, port);
        if (userName != null) {
            String raw = userName + ":" + Objects.toString(password, "");
            proxyAuth = "Basic " + Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
        } else {
            proxyAuth = null;
        }
    }

    /**
     * Get no parent proxy pattern or null.
     *
     * @return no parent proxy pattern or null.
     */
    public String getNoParentProxy() {
        return noParentProxy != null ? noParentProxy.pattern() : null;
    }

    /**
     * Set no parent proxy pattern or null.
     *
     * @param noParentProxy no parent proxy pattern or null.
     */
    public void setNoParentProxy(String noParentProxy) {
        this.noParentProxy = noParentProxy != null ? Pattern.compile(noParentProxy) : null;
    }

    private boolean matchesNoParentProxy(HttpRequest httpRequest) {
        return noParentProxy != null && noParentProxy.matcher(httpRequest.uri()).find();
    }

    /**
     * Create ChainedProxyManager instance.
     *
     * @return ChainedProxyManager instance.
     */
    public ChainedProxyManager createChainedProxyManager() {
        return (httpRequest, chainedProxies) -> {
            if (matchesNoParentProxy(httpRequest)) {
                chainedProxies.add(ChainedProxyAdapter.FALLBACK_TO_DIRECT_CONNECTION);
            } else {
                chainedProxies.add(new ChainedProxyAdapter() {
                    @Override
                    public InetSocketAddress getChainedProxyAddress() {
                        return parentHostAndPort;
                    }
                });
            }
        };
    }

    /**
     * Create "Proxy-Authorization" appender if needed.
     *
     * @return optional of "Proxy-Authorization" appender as HttpFiltersSource instance.
     */
    public Optional<BiFunction<ProxyContext, HttpObject, HttpResponse>> createProxyAuthorizationAppenderIfNeeded() {
        if (proxyAuth == null)
            return Optional.empty();
        return Optional.of((httpFilters, httpObject) -> {
            if (httpObject instanceof HttpRequest && !matchesNoParentProxy((HttpRequest) httpObject)) {
                HttpRequest request = (HttpRequest) httpObject;
                request.headers().add("Proxy-Authorization", proxyAuth);
            }
            return null;
        });
    }
}
