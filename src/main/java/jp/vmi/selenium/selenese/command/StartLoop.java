package jp.vmi.selenium.selenese.command;

/**
 * Interface for beginning-of-loop commands.
 */
public interface StartLoop {

    /** Use NO_START_LOOP instaed of null. */
    public static final StartLoop NO_START_LOOP = new StartLoop() {

        @Override
        public void setEndLoop(EndLoop endLoop) {
            // no operation.
        }

        @Override
        public String toString() {
            return "NO_START_LOOP";
        }
    };

    /** The separator of reached counts. */
    public static final String REACHED_COUNT_SEPARATOR = "-";

    /**
     * Set end-of-loop command.
     *
     * @param endLoop end-of-loop command.
     */
    void setEndLoop(EndLoop endLoop);
}
