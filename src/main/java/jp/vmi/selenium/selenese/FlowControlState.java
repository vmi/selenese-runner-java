package jp.vmi.selenium.selenese;

/**
 * Interface of flow control state.
 */
public interface FlowControlState {

    /**
     * True if block is already finished.
     *
     * @return true if block is already finished
     */
    boolean isAlreadyFinished();
}
