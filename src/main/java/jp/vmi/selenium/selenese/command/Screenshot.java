package jp.vmi.selenium.selenese.command;

/**
 * Screenshot information.
 */
@SuppressWarnings("javadoc")
public class Screenshot {

    public final String path;
    public final String label;

    public Screenshot(String path, String label) {
        this.path = path;
        this.label = label;
    }
}
