package jp.vmi.selenium.selenese;

import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.ModifierKeyState;
import jp.vmi.selenium.selenese.utils.JSFunction;

/**
 * Event utilities.
 */
public class EventUtils {

    /**
     * Event type.
     */
    @SuppressWarnings("javadoc")
    public enum EventType {
        KEYDOWN, KEYUP;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    /**
     * Event utilities itself.
     */
    public static EventUtils eventUtils = new EventUtils();

    private final JSFunction triggerKeyEvent;

    private EventUtils() {
        Map<String, JSFunction> functions = JSFunction.load(EventUtils.class.getResourceAsStream("EventUtils.js"));
        triggerKeyEvent = functions.get("triggerKeyEvent");
    }

    /**
     * Trigger key event.
     *
     * @param driver WebDriver object.
     * @param element target element.
     * @param eventType event type.
     * @param keySequence key sequence.
     * @param keyState modifier key state.
     */
    public void triggerKeyEvent(WebDriver driver, WebElement element, EventType eventType, String keySequence, ModifierKeyState keyState) {
        int keyCode;
        if (keySequence.codePointCount(0, keySequence.length()) == 1) {
            keyCode = keySequence.codePointAt(0);
        } else {
            try {
                if (keySequence.startsWith("\\"))
                    keyCode = Integer.parseInt(keySequence.substring(1));
                else
                    keyCode = Integer.parseInt(keySequence);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("invalid keySequence");
            }
        }
        triggerKeyEvent.call(driver, element, eventType.toString(), keyCode,
            keyState.isControlKeyDown(), keyState.isAltKeyDown(), keyState.isShiftKeyDown(), keyState.isMetaKeyDown());
    }
}
