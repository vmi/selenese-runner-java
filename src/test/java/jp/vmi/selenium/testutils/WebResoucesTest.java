package jp.vmi.selenium.testutils;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@SuppressWarnings("javadoc")
public class WebResoucesTest {

    @Rule
    public final WebServerResouce wsr = new WebServerResouce();

    @Rule
    public final WebProxyResource wpr = new WebProxyResource();

    @Test
    public void testWebResources() throws IOException {
        String baseURL = wsr.getServer().getBaseURL();
        // direct access.
        URL url = new URL(baseURL);
        Object content = url.getContent();
        String actualDirect = IOUtils.toString((InputStream) content);
        String expect = IOUtils.toString(getClass().getResource("/htdocs/index.html"));
        assertThat(actualDirect, is(expect));
        // proxy access.
        int proxyPort = wpr.getProxy().getPort();
        Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(proxyPort));
        url = new URL(baseURL);
        URLConnection conn = url.openConnection(proxy);
        content = conn.getContent();
        String actualProxy = IOUtils.toString((InputStream) content);
        assertThat(actualProxy, is(expect));
        assertThat(wpr.getProxy().getCount(), is(1));
    }
}
