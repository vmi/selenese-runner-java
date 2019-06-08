package jp.vmi.selenium.runner.model;

/**
 * Command interface.
 */
public interface ICommand {

    /**
     * Get id.
     *
     * @return id.
     */
    String getId();

    /**
     * Get comment.
     *
     * @return comment.
     */
    String getComment();

    /**
     * Get command name.
     *
     * @return command name.
     */
    String getCommand();

    /**
     * Get target.
     *
     * @return target.
     */
    String getTarget();

    /**
     * Get value.
     *
     * @return value.
     */
    String getValue();

    /**
     * Check breakpoint at this command.
     *
     * @return true if breakpoint.
     */
    boolean isBreakpoint();

    /**
     * Check this command opens window.
     *
     * @return true if open window.
     */
    boolean isOpensWindow();

    /**
     * Get window handle name for opening window.
     *
     * @return window handle name.
     */
    String getWindowHandleName();

    /**
     * Get window timeoue.
     *
     * @return timeout (ms).
     */
    long getWindowTimeout();

    /**
     * ???
     *
     * @return ???
     */
    boolean isOpensWindowRead();
}
