package jp.vmi.selenium.selenese.command;

/**
 * Interface for beginning-of-block commands.
 */
public interface BlockStart {

    /** Use NO_START_LOOP instaed of null. */
    public static final BlockStart NO_BLOCK_START = new BlockStart() {

        @Override
        public void setBlockEnd(BlockEnd blockEnd) {
            // no operation.
        }

        @Override
        public String toString() {
            return "NO_BLOCK_START";
        }
    };

    /** The separator of reached counts. */
    public static final String REACHED_COUNT_SEPARATOR = "-";

    /**
     * True if the block is loop.
     *
     * @return true if the block is loop
     */
    default boolean isLoopBlock() {
        return false;
    }

    /**
     * Set end-of-block command.
     *
     * @param blockEnd end-of-block command.
     */
    void setBlockEnd(BlockEnd blockEnd);
}
