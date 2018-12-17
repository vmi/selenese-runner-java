package jp.vmi.selenium.selenese.command;

/**
 * interface for end-of-loop commands.
 *
 * @deprecated use {@link BlockEnd} instaed.
 */
@Deprecated
public interface EndLoop extends BlockEnd {

    /**
     * Set beginning-of-loop command.
     *
     * @param startLoop beginning-of-loop command.
     *
     * @deprecated use {@link BlockEnd} instead.
     */
    @Deprecated
    void setStartLoop(StartLoop startLoop);
}
