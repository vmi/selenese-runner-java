package jp.vmi.selenium.selenese;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

/**
 * Modifier key state.
 */
@SuppressWarnings("javadoc")
public class ModifierKeyState {

    private final Actions action;
    private boolean metaKeyDown = false;
    private boolean altKeyDown = false;
    private boolean controlKeyDown = false;
    private boolean shiftKeyDown = false;

    public ModifierKeyState(WebDriver driver) {
        action = new Actions(driver);
    }

    /**
     * Reset all modifier key state.
     */
    public void reset() {
        metaKeyDown = false;
        altKeyDown = false;
        controlKeyDown = false;
        shiftKeyDown = false;
    }

    public boolean isMetaKeyDown() {
        return metaKeyDown;
    }

    public void metaKeyDown() {
        this.metaKeyDown = true;
        action.keyDown(Keys.META).build().perform();
    }

    public void metaKeyUp() {
        this.metaKeyDown = false;
        action.keyUp(Keys.META).build().perform();
    }

    public boolean isAltKeyDown() {
        return altKeyDown;
    }

    public void altKeyDown() {
        this.altKeyDown = true;
        action.keyDown(Keys.ALT).build().perform();
    }

    public void altKeyUp() {
        this.altKeyDown = false;
        action.keyUp(Keys.ALT).build().perform();
    }

    public boolean isControlKeyDown() {
        return controlKeyDown;
    }

    public void controlKeyDown() {
        this.controlKeyDown = true;
        action.keyDown(Keys.CONTROL).build().perform();
    }

    public void controlKeyUp() {
        this.controlKeyDown = false;
        action.keyUp(Keys.CONTROL).build().perform();
    }

    public boolean isShiftKeyDown() {
        return shiftKeyDown;
    }

    public void shiftKeyDown() {
        this.shiftKeyDown = true;
        action.keyDown(Keys.SHIFT).build().perform();
    }

    public void shiftKeyUp() {
        this.shiftKeyDown = false;
        action.keyUp(Keys.SHIFT).build().perform();
    }
}
