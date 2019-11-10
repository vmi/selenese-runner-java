package jp.vmi.selenium.testutils.proxy;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.net.ssl.SSLSession;

import org.littleshoot.proxy.ActivityTrackerAdapter;
import org.littleshoot.proxy.FlowContext;
import org.littleshoot.proxy.FullFlowContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * Proxy activity tracker.
 */
public class ProxyActivityTracker extends ActivityTrackerAdapter {

    private static final Logger log = LoggerFactory.getLogger(ProxyActivityTracker.class);

    /**
     * Constructor.
     */
    public ProxyActivityTracker() {
    }

    @Override
    public void requestReceivedFromClient(FlowContext flowContext, HttpRequest httpRequest) {
        log.info("[C->P] {} {} {}", httpRequest.method(), httpRequest.uri(), httpRequest.protocolVersion());
    }

    @Override
    public void requestSentToServer(FullFlowContext flowContext, HttpRequest httpRequest) {
        log.info("[P->S] {} {} {}", httpRequest.method(), httpRequest.uri(), httpRequest.protocolVersion());
    }

    private String getAdditionalInfo(HttpResponse httpResponse) {
        List<String> info = new ArrayList<>();
        HttpHeaders headers = httpResponse.headers();
        String loc = headers.get("Location");
        if (loc != null)
            info.add("Location: " + loc);
        String cType = headers.get("Content-Type");
        if (cType != null)
            info.add("Content-Type: " + cType);
        String cLen = headers.get("Content-Length");
        if (cLen != null)
            info.add("Content-Length: " + cLen);
        if (info.isEmpty())
            return "";
        return info.stream().collect(Collectors.joining(" / ", " (", ")"));
    }

    @Override
    public void responseReceivedFromServer(FullFlowContext flowContext, HttpResponse httpResponse) {
        log.info("[P<-S] {}{}", httpResponse.status(), getAdditionalInfo(httpResponse));
    }

    @Override
    public void responseSentToClient(FlowContext flowContext, HttpResponse httpResponse) {
        log.info("[C<-P] {}{}", httpResponse.status(), getAdditionalInfo(httpResponse));
    }

    @Override
    public void clientConnected(InetSocketAddress clientAddress) {
        log.info("[C] Connected: {}", clientAddress);
    }

    @Override
    public void clientDisconnected(InetSocketAddress clientAddress, SSLSession sslSession) {
        log.info("[C] Disconnected: {}", clientAddress);
    }
}
