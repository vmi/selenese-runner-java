package org.openqa.selenium.phantomjs;

import java.io.File;
import java.util.Collection;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.browserlaunchers.Proxies;
import org.openqa.selenium.phantomjs.PhantomJSDriverService.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hayato
 */
public class PhantomJSDriverServiceWrapper {

    private static final Logger log = LoggerFactory.getLogger(PhantomJSDriverServiceWrapper.class);

    private static final String PHANTOMJS_DOC_LINK = "https://github.com/ariya/phantomjs/wiki";
    private static final String PHANTOMJS_DOWNLOAD_LINK = "http://phantomjs.org/download.html";
    private static final String GHOSTDRIVER_DOC_LINK = "https://github.com/detro/ghostdriver/blob/master/README.md";
    private static final String GHOSTDRIVER_DOWNLOAD_LINK = "https://github.com/detro/ghostdriver/downloads";

    /**
     * create no logging PhantomJSDriverSerivice.
     * @see PhantomJSDriverService
     */
    public static PhantomJSDriverService createDefaultService(Capabilities desiredCapabilities) {
        // Look for Proxy configuration within the Capabilities
        Proxy proxy = null;
        if (desiredCapabilities != null) {
            proxy = Proxies.extractProxy(desiredCapabilities);
        }

        // Find PhantomJS executable
        File phantomjsfile = PhantomJSDriverService.findPhantomJS(desiredCapabilities, PHANTOMJS_DOC_LINK,
            PHANTOMJS_DOWNLOAD_LINK);

        // Find GhostDriver main JavaScript file
        File ghostDriverfile = PhantomJSDriverService.findGhostDriver(desiredCapabilities, GHOSTDRIVER_DOC_LINK,
            GHOSTDRIVER_DOWNLOAD_LINK);

        // Build & return service
        return new Builder()
            .usingPhantomJSExecutable(phantomjsfile)
            .usingGhostDriver(ghostDriverfile)
            .usingAnyFreePort()
            //            .withLogFile(new File(PHANTOMJS_DEFAULT_LOGFILE))
            .withProxy(proxy)
            .usingCommandLineArguments(
                findCLIArgumentsFromCaps(desiredCapabilities, PhantomJSDriverService.PHANTOMJS_CLI_ARGS))
            .usingGhostDriverCommandLineArguments(
                findCLIArgumentsFromCaps(desiredCapabilities,
                    PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_CLI_ARGS))
            .build();
    }

    private static String[] findCLIArgumentsFromCaps(Capabilities desiredCapabilities, String capabilityName) {
        if (desiredCapabilities != null) {
            Object cap = desiredCapabilities.getCapability(capabilityName);
            if (cap != null) {
                if (cap instanceof String[]) {
                    return (String[]) cap;
                } else if (cap instanceof Collection) {
                    try {
                        @SuppressWarnings("unchecked")
                        Collection<String> capCollection = (Collection<String>) cap;
                        return capCollection.toArray(new String[capCollection.size()]);
                    } catch (Exception e) {
                        // If casting fails, log an error and assume no CLI arguments are provided
                        log.warn(String.format(
                            "Unable to set Capability '%s' as it was neither a String[] or a Collection<String>",
                            capabilityName));
                    }
                }
            }
        }
        return new String[] {}; // nothing found: return an empty array of arguments
    }

}
