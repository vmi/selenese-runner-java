package jp.vmi.selenium.selenese.result;

/**
 * Result of Unexecuted.
 */
public class Unexecuted extends Result {

    /** Default unexecuted */
    public static final Unexecuted UNEXECUTED = new Unexecuted();

    private Unexecuted() {
        super("Unexecuted");
    }

    @Override
    public Level getLevel() {
        return Level.UNEXECUTED;
    }

}
