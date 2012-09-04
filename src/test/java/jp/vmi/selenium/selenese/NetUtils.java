package jp.vmi.selenium.selenese;

import org.openqa.selenium.net.PortProber;

/**
 * Network utilities.
 */
public class NetUtils {
    protected static int getUsablePort() {
        return PortProber.findFreePort();
    }
}
