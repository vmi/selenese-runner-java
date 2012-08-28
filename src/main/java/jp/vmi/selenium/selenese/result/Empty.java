package jp.vmi.selenium.selenese.result;

/**
 * Result of success.
 */
public class Empty extends Success {

    /** Default success */
    public static final Empty EMPTY = new Empty();

    private Empty() {
        super("Empty");
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
