package jp.vmi.selenium.selenese;

import org.openqa.selenium.Alert;

/**
 * action class for <i>native</i> alert dialog.
 */
public interface AlertActionListener {
    /**
     * Set whether or not to accept next alert dialog. <code>true</code> for accept, <code>false</code> for dismiss.
     *
     * @param accept result of dialog.
     */
    void setAccept(boolean accept);

    /**
     * Set the answer for <i>native</i> alert dialog.
     *
     * @param answer result of dialog.
     */
    void setAnswer(String answer);

    /**
     * Performs the action to <code>alert</code>. After perform, instance behaviour should be reset.
     *
     * @param alert Alert instance of Selenium.
     */
    void actionPerformed(Alert alert);
}
