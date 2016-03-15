package jp.vmi.selenium.testutils;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@SuppressWarnings("javadoc")
public class WebResourcesTest {

    @Rule
    public final WebServerResouce wsr = new WebServerResouce();

    @Rule
    public final WebProxyResource wpr = new WebProxyResource();

    // direct access.
    @Test
    public void testWebResources() throws IOException {
        String baseURL = wsr.getBaseURL();
        String expect = IOUtils.toString(getClass().getResource("/htdocs/index.html"), StandardCharsets.UTF_8);
        URL url = new URL(baseURL);
        Object content = url.getContent();
        String actualDirect = IOUtils.toString((InputStream) content, StandardCharsets.UTF_8);
        assertThat(actualDirect, is(expect));
    }

    // proxy access.
    @Test
    public void testWebProxyResources() throws IOException {
        String baseURL = wsr.getBaseURL();
        String expect = IOUtils.toString(getClass().getResource("/htdocs/index.html"), StandardCharsets.UTF_8);
        int proxyPort = wpr.getPort();
        Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(proxyPort));
        URL url = new URL(baseURL);
        URLConnection conn = url.openConnection(proxy);
        Object content = conn.getContent();
        String actualProxy = IOUtils.toString((InputStream) content, StandardCharsets.UTF_8);
        assertThat(actualProxy, is(expect));
        assertThat(wpr.getCount(), is(1));
    }
}
