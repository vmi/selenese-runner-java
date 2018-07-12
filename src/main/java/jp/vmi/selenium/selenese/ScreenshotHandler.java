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
     * Take entire page screenshot to filename. (override directory if --screenshot-dir option specified)
     *
     * @param filename filename.
     * @return screenshot image path.
     * @exception UnsupportedOperationException WebDriver does not supoort capturing screenshot.
     */
    String takeEntirePageScreenshot(String filename) throws UnsupportedOperationException;

    /**
     * Take screenshot to filename. (override directory if --screenshot-dir option specified)
     *
     * @param filename filename.
     * @return screenshot image path.
     * @exception UnsupportedOperationException WebDriver does not supoort capturing screenshot.
     */
    String takeScreenshot(String filename) throws UnsupportedOperationException;

    /**
     * Take screenshot at all commands if --screenshot-all option specified.
     *
     * @param prefix prefix name.
     * @param index command index.
     * @return screenshot image path.
     */
    String takeScreenshotAll(String prefix, int index);

    /**
     * Take screenshot on fail commands if --screenshot-on-fail option specified.
     *
     * @param prefix prefix name.
     * @param index command index.
     * @return screenshot image path.
     */
    String takeScreenshotOnFail(String prefix, int index);
}
