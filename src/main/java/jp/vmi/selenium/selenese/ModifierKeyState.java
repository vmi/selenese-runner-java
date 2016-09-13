package jp.vmi.selenium.selenese;

/**
 * Modifier key state.
 */
@SuppressWarnings("javadoc")
public class ModifierKeyState {

    private boolean metaKeyDown = false;
    private boolean altKeyDown = false;
    private boolean controlKeyDown = false;
    private boolean shiftKeyDown = false;

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
    }

    public void metaKeyUp() {
        this.metaKeyDown = false;
    }

    public boolean isAltKeyDown() {
        return altKeyDown;
    }

    public void altKeyDown() {
        this.altKeyDown = true;
    }

    public void altKeyUp() {
        this.altKeyDown = false;
    }

    public boolean isControlKeyDown() {
        return controlKeyDown;
    }

    public void controlKeyDown() {
        this.controlKeyDown = true;
    }

    public void controlKeyUp() {
        this.controlKeyDown = false;
    }

    public boolean isShiftKeyDown() {
        return shiftKeyDown;
    }

    public void shiftKeyDown() {
        this.shiftKeyDown = true;
    }

    public void shiftKeyUp() {
        this.shiftKeyDown = false;
    }
}
