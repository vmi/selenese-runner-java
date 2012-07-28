package jp.vmi.selenium.webdriver;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.auth.AuthScope;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.WebClient;

import static jp.vmi.selenium.webdriver.DriverOptions.DriverOption.*;

public class HtmlUnitDriverFactory extends WebDriverFactory {

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        DesiredCapabilities capabilities = setupProxy(DesiredCapabilities.htmlUnit(), driverOptions);
        capabilities.setJavascriptEnabled(true);

        final String proxyHost;
        final int proxyPort;
        final String proxyUser;
        final String proxyPassword;
        final String proxy = driverOptions.get(PROXY);
        if (proxy != null) {
            String[] hp = proxy.split(":", 2);
            proxyHost = hp[0];
            proxyPort = NumberUtils.toInt(hp[1]);
            String u = driverOptions.get(PROXY_USER);
            String p = driverOptions.get(PROXY_PASSWORD);
            if (u != null && p == null)
                p = "";
            else if (u == null && p != null)
                u = "";
            proxyUser = u;
            proxyPassword = p;
        } else {
            proxyHost = null;
            proxyPort = 0;
            proxyUser = null;
            proxyPassword = null;
        }

        return new HtmlUnitDriver(capabilities) {
            @Override
            protected WebClient modifyWebClient(WebClient client) {
                if (proxyUser != null) {
                    DefaultCredentialsProvider cp = new DefaultCredentialsProvider();
                    cp.addCredentials(proxyUser, proxyPassword, proxyHost, proxyPort, AuthScope.ANY_REALM);
                    client.setCredentialsProvider(cp);
                }
                return client;
            }

        };
    }
}
