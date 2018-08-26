package jp.vmi.selenium.selenese;

import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Modifier key state.
 */
@SuppressWarnings("javadoc")
public class ModifierKeyState {

    private static final Logger log = LoggerFactory.getLogger(ModifierKeyState.class);

    private final Context context;
    private boolean metaKeyDown = false;
    private boolean altKeyDown = false;
    private boolean controlKeyDown = false;
    private boolean shiftKeyDown = false;

    public ModifierKeyState(Context context) {
        this.context = context;
    }

    private Actions newActions() {
        return new Actions(context.getWrappedDriver());
    }

    /**
     * Reset all modifier key state.
     */
    public void reset() {
        Actions actions = newActions();
        boolean perform = false;
        StringBuilder buf = new StringBuilder(4);
        if (metaKeyDown) {
            actions.keyUp(Keys.META);
            metaKeyDown = false;
            perform = true;
            buf.append('M');
        }
        if (altKeyDown) {
            actions.keyUp(Keys.ALT);
            altKeyDown = false;
            perform = true;
            buf.append('A');
        }
        if (controlKeyDown) {
            actions.keyUp(Keys.CONTROL);
            controlKeyDown = false;
            perform = true;
            buf.append('C');
        }
        if (shiftKeyDown) {
            actions.keyUp(Keys.SHIFT);
            shiftKeyDown = false;
            perform = true;
            buf.append('S');
        }
        if (perform) {
            actions.build().perform();
            log.debug("Reset modifier keys: {}", buf);
        }
    }

    public boolean isMetaKeyDown() {
        return metaKeyDown;
    }

    public void metaKeyDown() {
        if (metaKeyDown)
            return;
        newActions().keyDown(Keys.META).perform();
        metaKeyDown = true;
        log.debug("Meta key down: {}", getStateString());
    }

    public void metaKeyUp() {
        if (!metaKeyDown)
            return;
        newActions().keyUp(Keys.META).perform();
        metaKeyDown = false;
        log.debug("Meta key up: {}", getStateString());
    }

    public boolean isAltKeyDown() {
        return altKeyDown;
    }

    public void altKeyDown() {
        if (altKeyDown)
            return;
        newActions().keyDown(Keys.ALT).perform();
        altKeyDown = true;
        log.debug("Alt key down: {}", getStateString());
    }

    public void altKeyUp() {
        if (!altKeyDown)
            return;
        newActions().keyUp(Keys.ALT).perform();
        altKeyDown = false;
        log.debug("Alt key up: {}", getStateString());
    }

    public boolean isControlKeyDown() {
        return controlKeyDown;
    }

    public void controlKeyDown() {
        if (controlKeyDown)
            return;
        newActions().keyDown(Keys.CONTROL).perform();
        controlKeyDown = true;
        log.debug("Control key down: {}", getStateString());
    }

    public void controlKeyUp() {
        if (!controlKeyDown)
            return;
        newActions().keyUp(Keys.CONTROL).perform();
        controlKeyDown = false;
        log.debug("Control key up: {}", getStateString());
    }

    public boolean isShiftKeyDown() {
        return shiftKeyDown;
    }

    public void shiftKeyDown() {
        if (shiftKeyDown)
            return;
        newActions().keyDown(Keys.SHIFT).perform();
        shiftKeyDown = true;
        log.debug("Shift key down: {}", getStateString());
    }

    public void shiftKeyUp() {
        if (!shiftKeyDown)
            return;
        newActions().keyUp(Keys.SHIFT).perform();
        shiftKeyDown = false;
        log.debug("Shift key up: {}", getStateString());
    }

    public String getStateString() {
        StringBuilder buf = new StringBuilder(4);
        if (metaKeyDown)
            buf.append('M');
        if (altKeyDown)
            buf.append('A');
        if (controlKeyDown)
            buf.append('C');
        if (shiftKeyDown)
            buf.append('S');
        return buf.toString();
    }
}
