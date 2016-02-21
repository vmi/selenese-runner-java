/*
The original code is PhantomJSDriverService in GhostDriver <https://github.com/detro/ghostdriver>

Patched by Hayato ITO <haya10.ito@gmail.com>

The following copyright is copied from original.
---

This file is part of the GhostDriver by Ivan De Marino <http://ivandemarino.me>.

Copyright (c) 2012-2014, Ivan De Marino <http://ivandemarino.me>
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.openqa.selenium.phantomjs;

import java.io.File;
import java.util.Collection;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.phantomjs.PhantomJSDriverService.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.openqa.selenium.phantomjs.PhantomJSDriverService.*;

/**
 * Substitution of PhantomJSDriverService#createDefaultService(Capabilities).
 */
public class CustomPhantomJSDriverServiceFactory {

    private static final Logger log = LoggerFactory.getLogger(CustomPhantomJSDriverServiceFactory.class);

    private static final String PHANTOMJS_DOC_LINK = "https://github.com/ariya/phantomjs/wiki";
    private static final String PHANTOMJS_DOWNLOAD_LINK = "http://phantomjs.org/download.html";
    private static final String GHOSTDRIVER_DOC_LINK = "https://github.com/detro/ghostdriver/blob/master/README.md";
    private static final String GHOSTDRIVER_DOWNLOAD_LINK = "https://github.com/detro/ghostdriver/downloads";

    private CustomPhantomJSDriverServiceFactory() {
    }

    /**
     * Configures and returns a new {@link PhantomJSDriverService} using the default configuration without logging.
     *
     * @see PhantomJSDriverService#createDefaultService(Capabilities)
     *
     * @param desiredCapabilities DesiredCapabilities object.
     * @return PhantomJSDriverService object.
     */
    public static PhantomJSDriverService createDefaultService(Capabilities desiredCapabilities) {
        // Look for Proxy configuration within the Capabilities
        Proxy proxy = null;
        if (desiredCapabilities != null) {
            proxy = Proxy.extractFrom(desiredCapabilities);
        }

        // Find PhantomJS executable
        File phantomjsfile = findPhantomJS(desiredCapabilities, PHANTOMJS_DOC_LINK, PHANTOMJS_DOWNLOAD_LINK);

        // Find GhostDriver main JavaScript file
        File ghostDriverfile = findGhostDriver(desiredCapabilities, GHOSTDRIVER_DOC_LINK, GHOSTDRIVER_DOWNLOAD_LINK);

        // Build & return service
        return new Builder().usingPhantomJSExecutable(phantomjsfile)
            .usingGhostDriver(ghostDriverfile)
            .usingAnyFreePort()
            // Note: remove log file option from oritinal code.
            // .withLogFile(new File(PHANTOMJS_DEFAULT_LOGFILE))
            .withProxy(proxy)
            .usingCommandLineArguments(
                findCLIArgumentsFromCaps(desiredCapabilities, PHANTOMJS_CLI_ARGS))
            .usingGhostDriverCommandLineArguments(
                findCLIArgumentsFromCaps(desiredCapabilities, PHANTOMJS_GHOSTDRIVER_CLI_ARGS))
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
                        // Note: change logger variable name and type from original code.
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
