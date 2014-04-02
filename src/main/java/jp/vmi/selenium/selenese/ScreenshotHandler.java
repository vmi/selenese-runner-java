package jp.vmi.selenium.selenese;

/**
 * Handle screenshot.
 */
public interface ScreenshotHandler {

    /**
     * Get ignore screenshot command flag.
     *
     * @return flag to ignore "captureEntirePageScreenshot"
     */
    boolean isIgnoredScreenshotCommand();

    /**
     * Take screenshot to filename. (override directory if --screenshot-dir option specified)
     *
     * @param filename filename.
     * @exception UnsupportedOperationException WebDriver does not supoort capturing screenshot.
     */
    void takeScreenshot(String filename) throws UnsupportedOperationException;

    /**
     * Take screenshot at all commands if --screenshot-all option specified.
     *
     * @param prefix prefix name.
     * @param index command index.
     */
    void takeScreenshotAll(String prefix, int index);

    /**
     * Take screenshot on fail commands if --screenshot-on-fail option specified.
     *
     * @param prefix prefix name.
     * @param index command index.
     */
    void takeScreenshotOnFail(String prefix, int index);
}
