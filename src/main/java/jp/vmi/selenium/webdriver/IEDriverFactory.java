package jp.vmi.selenium.webdriver;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;

import static jp.vmi.selenium.webdriver.DriverOptions.DriverOption.*;

/**
 * Factory of {@link InternetExplorerDriver}.
 */
public class IEDriverFactory extends WebDriverFactory {

    private static Logger log = LoggerFactory.getLogger(IEDriverFactory.class);

    private static final String IE_DRIVER_SERVER_EXE = "IEDriverServer.exe";

    // see: http://code.google.com/p/selenium/wiki/InternetExplorerDriver

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        Platform platform = Platform.getCurrent();
        log.info("Platform: {}", platform);
        switch (platform) {
        case WINDOWS:
        case XP:
        case VISTA:
            break;
        default:
            throw new UnsupportedOperationException("Unsupported platform: " + platform);
        }

        // DesiredCapabilities capabilities = setupProxy(DesiredCapabilities.internetExplorer(), driverOptions);
        // return new InternetExplorerDriver(capabilities);
        if (driverOptions.has(DriverOption.PROXY))
            log.warn("No support proxy with InternetExprolerDriver. Please set proxy to IE in advance.");

        File ieds;
        if (driverOptions.has(IEDRIVER)) {
            ieds = new File(driverOptions.get(IEDRIVER));
            if (!ieds.canExecute())
                throw new IllegalArgumentException("Missing " + IE_DRIVER_SERVER_EXE + ": " + ieds);
        } else {
            ieds = searchIEDriverServer();
        }
        InternetExplorerDriverService is = new InternetExplorerDriverService.Builder()
            .usingAnyFreePort()
            .usingDriverExecutable(ieds)
            .build();
        return new InternetExplorerDriver(is);
    }

    private File searchIEDriverServer() {
        List<String> pathList = new ArrayList<String>();
        pathList.add(System.getProperty("user.dir"));
        String envVarPath = System.getenv("PATH");
        if (envVarPath != null) {
            String[] paths = envVarPath.split(Pattern.quote(File.pathSeparator));
            Collections.addAll(pathList, paths);
        }
        for (String path : pathList) {
            File ieds = new File(path, IE_DRIVER_SERVER_EXE);
            if (ieds.canExecute())
                return ieds;
        }
        throw new IllegalArgumentException(IE_DRIVER_SERVER_EXE + " is not found.");
    }
}
