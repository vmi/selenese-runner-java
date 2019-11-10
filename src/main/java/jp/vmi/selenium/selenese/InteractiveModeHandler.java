package jp.vmi.selenium.selenese;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.command.CommandListIterator;
import jp.vmi.selenium.selenese.command.CurrentCommand;

/**
 * Interactive mode handler interface.
 *
 * <p>
 * NOTE: THIS INTERFACE IS EXPERIMENTAL.
 * IT MAY CHANGE IN THE FUTURE.
 * </p>
 */
public interface InteractiveModeHandler {

    /**
     * Instance of always non interactive.
     */
    static InteractiveModeHandler ALWAYS_NON_INTERACTIVE = new InteractiveModeHandler() {
        private final Logger log = LoggerFactory.getLogger(InteractiveModeHandler.class);

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public void setEnabled(boolean isEnabled) {
            if (isEnabled)
                log.warn("Always non interactive mode");
        }

        @Override
        public CurrentCommand handle(Context context, CommandListIterator commandListIterator, CurrentCommand curCmd) {
            return curCmd;
        }

        @Override
        public void enableIfBreakpointReached(CurrentCommand curCmd) {
            // no operation.
        }

        @Override
        public String toString() {
            return "InteractiveModeHandler.ALWAYS_NON_INTERACTIVE";
        }

    };

    /**
     * Get interactive mode is enabled or not.
     *
     * @return true if interactive mode is enabled.
     */
    boolean isEnabled();

    /**
     * Enable or disable interactive mode.
     *
     * @param isEnabled true if enable interactive mode.
     */
    void setEnabled(boolean isEnabled);

    /**
     * Enable if a breakpoint is reached.
     *
     * @param curCmd current command.
     */
    void enableIfBreakpointReached(CurrentCommand curCmd);

    /**
     * Handle interactive mode.
     *
     * @param context context.
     * @param commandListIterator command list iterator.
     * @param curCmd current command.
     * @return curCmd itself or replaced command.
     */
    CurrentCommand handle(Context context, CommandListIterator commandListIterator, CurrentCommand curCmd);
}
