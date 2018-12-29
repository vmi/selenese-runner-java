package jp.vmi.selenium.testutils.proxy;

import java.net.InetSocketAddress;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.littleshoot.proxy.HttpFilters;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * Http filters registrar.
 */
@SuppressWarnings("javadoc")
public class HttpFiltersRegistrar {

    @FunctionalInterface
    public interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
    }

    public static Consumer<ProxyContext> emptyConsumer() {
        return t -> {
            // no operation.
        };
    }

    public static <U> BiConsumer<ProxyContext, U> emptyBiConsumer() {
        return (t, u) -> {
            // no operation.
        };
    }

    public static <U, V> TriConsumer<ProxyContext, U, V> emptyTriConsumer() {
        return (t, u, v) -> {
            // no operation.
        };
    }

    private BiFunction<ProxyContext, HttpObject, HttpResponse> clientToProxyRequest = (context, httpObject) -> null;
    private BiFunction<ProxyContext, HttpObject, HttpResponse> proxyToServerRequest = (context, httpObject) -> null;
    private Consumer<ProxyContext> proxyToServerRequestSending = emptyConsumer();
    private Consumer<ProxyContext> proxyToServerRequestSent = emptyConsumer();
    private BiFunction<ProxyContext, HttpObject, HttpObject> serverToProxyResponse = (context, httpObject) -> httpObject;
    private Consumer<ProxyContext> serverToProxyResponseTimedOut = emptyConsumer();
    private Consumer<ProxyContext> serverToProxyResponseReceiving = emptyConsumer();
    private Consumer<ProxyContext> serverToProxyResponseReceived = emptyConsumer();
    private BiFunction<ProxyContext, HttpObject, HttpObject> proxyToClientResponse = (context, httpObject) -> httpObject;
    private Consumer<ProxyContext> proxyToServerConnectionQueued = emptyConsumer();
    private BiFunction<ProxyContext, String, InetSocketAddress> proxyToServerResolutionStarted = (context, httpObject) -> null;
    private BiConsumer<ProxyContext, String> proxyToServerResolutionFailed = emptyBiConsumer();
    private TriConsumer<ProxyContext, String, InetSocketAddress> proxyToServerResolutionSucceeded = emptyTriConsumer();
    private Consumer<ProxyContext> proxyToServerConnectionStarted = emptyConsumer();
    private Consumer<ProxyContext> proxyToServerConnectionSSLHandshakeStarted = emptyConsumer();
    private Consumer<ProxyContext> proxyToServerConnectionFailed = emptyConsumer();
    private BiConsumer<ProxyContext, ChannelHandlerContext> proxyToServerConnectionSucceeded = emptyBiConsumer();

    public BiFunction<ProxyContext, HttpObject, HttpResponse> getClientToProxyRequest() {
        return clientToProxyRequest;
    }

    public void setClientToProxyRequest(BiFunction<ProxyContext, HttpObject, HttpResponse> clientToProxyRequest) {
        this.clientToProxyRequest = clientToProxyRequest;
    }

    public BiFunction<ProxyContext, HttpObject, HttpResponse> getProxyToServerRequest() {
        return proxyToServerRequest;
    }

    public void setProxyToServerRequest(BiFunction<ProxyContext, HttpObject, HttpResponse> proxyToServerRequest) {
        this.proxyToServerRequest = proxyToServerRequest;
    }

    public Consumer<ProxyContext> getProxyToServerRequestSending() {
        return proxyToServerRequestSending;
    }

    public void setProxyToServerRequestSending(Consumer<ProxyContext> proxyToServerRequestSending) {
        this.proxyToServerRequestSending = proxyToServerRequestSending;
    }

    public Consumer<ProxyContext> getProxyToServerRequestSent() {
        return proxyToServerRequestSent;
    }

    public void setProxyToServerRequestSent(Consumer<ProxyContext> proxyToServerRequestSent) {
        this.proxyToServerRequestSent = proxyToServerRequestSent;
    }

    public BiFunction<ProxyContext, HttpObject, HttpObject> getServerToProxyResponse() {
        return serverToProxyResponse;
    }

    public void setServerToProxyResponse(BiFunction<ProxyContext, HttpObject, HttpObject> serverToProxyResponse) {
        this.serverToProxyResponse = serverToProxyResponse;
    }

    public Consumer<ProxyContext> getServerToProxyResponseTimedOut() {
        return serverToProxyResponseTimedOut;
    }

    public void setServerToProxyResponseTimedOut(Consumer<ProxyContext> serverToProxyResponseTimedOut) {
        this.serverToProxyResponseTimedOut = serverToProxyResponseTimedOut;
    }

    public Consumer<ProxyContext> getServerToProxyResponseReceiving() {
        return serverToProxyResponseReceiving;
    }

    public void setServerToProxyResponseReceiving(Consumer<ProxyContext> serverToProxyResponseReceiving) {
        this.serverToProxyResponseReceiving = serverToProxyResponseReceiving;
    }

    public Consumer<ProxyContext> getServerToProxyResponseReceived() {
        return serverToProxyResponseReceived;
    }

    public void setServerToProxyResponseReceived(Consumer<ProxyContext> serverToProxyResponseReceived) {
        this.serverToProxyResponseReceived = serverToProxyResponseReceived;
    }

    public BiFunction<ProxyContext, HttpObject, HttpObject> getProxyToClientResponse() {
        return proxyToClientResponse;
    }

    public void setProxyToClientResponse(BiFunction<ProxyContext, HttpObject, HttpObject> proxyToClientResponse) {
        this.proxyToClientResponse = proxyToClientResponse;
    }

    public Consumer<ProxyContext> getProxyToServerConnectionQueued() {
        return proxyToServerConnectionQueued;
    }

    public void setProxyToServerConnectionQueued(Consumer<ProxyContext> proxyToServerConnectionQueued) {
        this.proxyToServerConnectionQueued = proxyToServerConnectionQueued;
    }

    public BiFunction<ProxyContext, String, InetSocketAddress> getProxyToServerResolutionStarted() {
        return proxyToServerResolutionStarted;
    }

    public void setProxyToServerResolutionStarted(BiFunction<ProxyContext, String, InetSocketAddress> proxyToServerResolutionStarted) {
        this.proxyToServerResolutionStarted = proxyToServerResolutionStarted;
    }

    public BiConsumer<ProxyContext, String> getProxyToServerResolutionFailed() {
        return proxyToServerResolutionFailed;
    }

    public void setProxyToServerResolutionFailed(BiConsumer<ProxyContext, String> proxyToServerResolutionFailed) {
        this.proxyToServerResolutionFailed = proxyToServerResolutionFailed;
    }

    public TriConsumer<ProxyContext, String, InetSocketAddress> getProxyToServerResolutionSucceeded() {
        return proxyToServerResolutionSucceeded;
    }

    public void setProxyToServerResolutionSucceeded(TriConsumer<ProxyContext, String, InetSocketAddress> proxyToServerResolutionSucceeded) {
        this.proxyToServerResolutionSucceeded = proxyToServerResolutionSucceeded;
    }

    public Consumer<ProxyContext> getProxyToServerConnectionStarted() {
        return proxyToServerConnectionStarted;
    }

    public void setProxyToServerConnectionStarted(Consumer<ProxyContext> proxyToServerConnectionStarted) {
        this.proxyToServerConnectionStarted = proxyToServerConnectionStarted;
    }

    public Consumer<ProxyContext> getProxyToServerConnectionSSLHandshakeStarted() {
        return proxyToServerConnectionSSLHandshakeStarted;
    }

    public void setProxyToServerConnectionSSLHandshakeStarted(Consumer<ProxyContext> proxyToServerConnectionSSLHandshakeStarted) {
        this.proxyToServerConnectionSSLHandshakeStarted = proxyToServerConnectionSSLHandshakeStarted;
    }

    public Consumer<ProxyContext> getProxyToServerConnectionFailed() {
        return proxyToServerConnectionFailed;
    }

    public void setProxyToServerConnectionFailed(Consumer<ProxyContext> proxyToServerConnectionFailed) {
        this.proxyToServerConnectionFailed = proxyToServerConnectionFailed;
    }

    public BiConsumer<ProxyContext, ChannelHandlerContext> getProxyToServerConnectionSucceeded() {
        return proxyToServerConnectionSucceeded;
    }

    public void setProxyToServerConnectionSucceeded(BiConsumer<ProxyContext, ChannelHandlerContext> proxyToServerConnectionSucceeded) {
        this.proxyToServerConnectionSucceeded = proxyToServerConnectionSucceeded;
    }

    public HttpFilters newHttpFilters(HttpRequest originalRequest, ChannelHandlerContext ctx) {
        return new ProxyContext(originalRequest, ctx);
    }

    public class ProxyContext implements HttpFilters {

        public final HttpRequest originalRequest;
        public final ChannelHandlerContext ctx;

        private ProxyContext(HttpRequest originalRequest, ChannelHandlerContext ctx) {
            this.originalRequest = originalRequest;
            this.ctx = ctx;
        }

        @Override
        public HttpResponse clientToProxyRequest(HttpObject httpObject) {
            return clientToProxyRequest.apply(this, httpObject);
        }

        @Override
        public HttpResponse proxyToServerRequest(HttpObject httpObject) {
            return proxyToServerRequest.apply(this, httpObject);
        }

        @Override
        public void proxyToServerRequestSending() {
            proxyToServerRequestSending.accept(this);
        }

        @Override
        public void proxyToServerRequestSent() {
            proxyToServerRequestSent.accept(this);
        }

        @Override
        public HttpObject serverToProxyResponse(HttpObject httpObject) {
            return serverToProxyResponse.apply(this, httpObject);
        }

        @Override
        public void serverToProxyResponseTimedOut() {
            serverToProxyResponseTimedOut.accept(this);
        }

        @Override
        public void serverToProxyResponseReceiving() {
            serverToProxyResponseReceiving.accept(this);
        }

        @Override
        public void serverToProxyResponseReceived() {
            serverToProxyResponseReceived.accept(this);
        }

        @Override
        public HttpObject proxyToClientResponse(HttpObject httpObject) {
            return proxyToClientResponse.apply(this, httpObject);
        }

        @Override
        public void proxyToServerConnectionQueued() {
            proxyToServerConnectionQueued.accept(this);
        }

        @Override
        public InetSocketAddress proxyToServerResolutionStarted(String resolvingServerHostAndPort) {
            return proxyToServerResolutionStarted.apply(this, resolvingServerHostAndPort);
        }

        @Override
        public void proxyToServerResolutionFailed(String hostAndPort) {
            proxyToServerResolutionFailed.accept(this, hostAndPort);
        }

        @Override
        public void proxyToServerResolutionSucceeded(String serverHostAndPort, InetSocketAddress resolvedRemoteAddress) {
            proxyToServerResolutionSucceeded.accept(this, serverHostAndPort, resolvedRemoteAddress);
        }

        @Override
        public void proxyToServerConnectionStarted() {
            proxyToServerConnectionStarted.accept(this);
        }

        @Override
        public void proxyToServerConnectionSSLHandshakeStarted() {
            proxyToServerConnectionSSLHandshakeStarted.accept(this);
        }

        @Override
        public void proxyToServerConnectionFailed() {
            proxyToServerConnectionFailed.accept(this);
        }

        @Override
        public void proxyToServerConnectionSucceeded(ChannelHandlerContext serverCtx) {
            proxyToServerConnectionSucceeded.accept(this, serverCtx);
        }
    }
}
