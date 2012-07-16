package jp.vmi.selenium.webdriver;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.auth.AuthScope;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.WebClient;

import jp.vmi.selenium.selenese.InvalidConfigurationException;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;

public class HtmlUnitDriverFactory extends WebDriverFactory {

    private static final Logger log = LoggerFactory.getLogger(HtmlUnitDriverFactory.class);

    private final String proxyHost;
    private final int proxyPort;
    private final String proxyUser;
    private final String proxyPassword;

    HtmlUnitDriverFactory(DriverOptions options) throws InvalidConfigurationException {
        super(options);
        String proxy = options.get(DriverOption.PROXY);
        if (proxy != null) {
            String[] hp = proxy.split(":", 2);
            proxyHost = hp[0];
            proxyPort = NumberUtils.toInt(hp[1]);
            String u = options.get(DriverOption.PROXY_USER);
            String p = options.get(DriverOption.PROXY_PASSWORD);
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
        capabilities.setJavascriptEnabled(true);
    }

    @Override
    protected DesiredCapabilities defaultCapabilities() {
        return DesiredCapabilities.htmlUnit();
    }

    @Override
    public WebDriver initDriver() {
        HtmlUnitDriver driver = new HtmlUnitDriver(capabilities) {

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

        log.info("HtmlUnitDriver initialized.");
        return driver;
    }
}
