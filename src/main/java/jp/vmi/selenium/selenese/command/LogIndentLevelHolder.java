package jp.vmi.selenium.selenese.command;

/**
 * Log indent level holder.
 */
public interface LogIndentLevelHolder {

    /**
     * Get log indent level.
     * 
     * @return log indent level.
     */
    int getLogIndentLevel();

    /**
     * Set log indent level.
     * 
     * @param logIndentLevel log indent level.
     */
    void setLogIndentLevel(int logIndentLevel);
}
