package org.openqa.selenium.browserlaunchers;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;

/**
 * Alternative Proxies class.
 *
 * Proxies class has been removed from Selenium 2.44.0, but PhantomJS driver has NOT been updated yet on Maven repository.
 *
 * FIXME remove this when PhantomJS driver is updated.
 */
@SuppressWarnings("javadoc")
public class Proxies {

    private Proxies() {
    }

    public static Proxy extractProxy(Capabilities desiredCapabilities) {
        return Proxy.extractFrom(desiredCapabilities);
    }
}
