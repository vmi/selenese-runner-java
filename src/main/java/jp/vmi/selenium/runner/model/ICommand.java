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
     * Get comment.
     *
     * @return comment.
     */
    String getComment();
}
