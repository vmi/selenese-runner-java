package jp.vmi.selenium.selenese;

/**
 * Interface of flow control state.
 */
public interface FlowControlState {

    /**
     * State that this block is finished.
     */
    static final FlowControlState FINISHED_STATE = () -> true;

    /**
     * State that this block is not finished yet.
     */
    static final FlowControlState CONTINUED_STATE = () -> false;

    /**
     * True if block is already finished.
     *
     * @return true if block is already finished
     */
    boolean isAlreadyFinished();
}
