package jp.vmi.selenium.selenese.subcommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.ArgumentType;
import jp.vmi.selenium.selenese.utils.MouseUtils;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Mouse event handler.
 */
public class MouseEventHandler implements ISubCommand<Void> {

    private static final Logger log = LoggerFactory.getLogger(MouseEventHandler.class);

    @SuppressWarnings("javadoc")
    public static enum MouseEventType {
        MOUSE_OVER(LOCATOR), // -
        MOUSE_OUT(LOCATOR), // -
        MOUSE_MOVE(LOCATOR), // -
        MOUSE_MOVE_AT(LOCATOR, VALUE), // VALUE = x,y
        MOUSE_DOWN(LOCATOR), // -
        MOUSE_DOWN_AT(LOCATOR, VALUE), // VALUE = x,y
        MOUSE_UP(LOCATOR), // -
        MOUSE_UP_AT(LOCATOR, VALUE), // VALUE = x,y
        ;

        private String commandName;
        private String eventName;
        private ArgumentType[] argTypes;
        private boolean hasCoord;

        private MouseEventType(ArgumentType... argTypes) {
            String[] words = name().split("_");
            StringBuilder commandName = new StringBuilder(words[0].toLowerCase());
            StringBuilder eventName = new StringBuilder(words[0].toLowerCase());
            for (int i = 1; i < words.length; i++) {
                String word = words[i];
                commandName.append(word.charAt(0));
                if (word.length() > 1)
                    commandName.append(word.substring(1).toLowerCase());
                if (i < words.length - 1 || !word.equals("AT"))
                    eventName.append(word.toLowerCase());
            }
            this.commandName = commandName.toString();
            this.eventName = eventName.toString();
            this.argTypes = argTypes;
            this.hasCoord = argTypes.length == 2;
        }
    }

    // Use createEvent & initMouseEvent for PhantomJS.
    private static final String NEW_MOUSE_EVENT = "var newMouseEvent = function(element, event, init) {"
        + "try {"
        + "return new MouseEvent(event, init);"
        + "} catch (e) {"
        + "var e = document.createEvent('MouseEvents');"
        + "e.initMouseEvent(event, true, true, window, 0, "
        + "init.screenX, init.screenY, init.clientX, init.clientY, "
        + "init.ctrlKey, init.altKey, init.shiftKey, init.metaKey, init.button, element);"
        + "return e;"
        + "}"
        + "};";

    private static final String FIRE_MOUSE_OVER_EVENT = "(function(element) {"
        + NEW_MOUSE_EVENT
        + "element.dispatchEvent(newMouseEvent(element, 'mouseover', {}));"
        + "element.dispatchEvent(newMouseEvent(element, 'mouseenter', {}));"
        + "})(arguments[0])";

    private static final String FIRE_MOUSE_OUT_EVENT = "(function(element) {"
        + NEW_MOUSE_EVENT
        + "element.dispatchEvent(newMouseEvent(element, 'mouseleave', {}));"
        + "element.dispatchEvent(newMouseEvent(element, 'mouseout', {}));"
        + "})(arguments[0])";

    private static final String FIRE_MOUSE_EVENT = "(function(element, event, init) {"
        + NEW_MOUSE_EVENT
        + "element.dispatchEvent(newMouseEvent(element, event, init));"
        + "}).apply(null, arguments)";

    private static final int ARG_LOCATOR = 0;
    private static final int ARG_COORD = 1;

    private final MouseEventType eventType;

    /**
     * Constructor.
     *
     * @param eventType mouse event eventType.
     */
    public MouseEventHandler(MouseEventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public String getName() {
        return eventType.commandName;
    }

    @Override
    public ArgumentType[] getArgumentTypes() {
        return eventType.argTypes;
    }

    @SuppressWarnings("unchecked")
    private static <T> T eval(WebDriver driver, String script, Object... args) {
        return (T) ((JavascriptExecutor) driver).executeScript(script, args);
    }

    private static Point coordToPoint(String coordString) {
        String[] pair = coordString.trim().split("\\s*,\\s*");
        int x = (int) Double.parseDouble(pair[0]);
        int y = (int) Double.parseDouble(pair[1]);
        return new Point(x, y);
    }

    private static Point calcOffset(int vpWidth, int vpHeight, Point elemLocation, Dimension elemSize) {
        int xOffset = elemSize.width / 2;
        int yOffset = elemSize.height / 2;
        if (elemLocation.x + elemSize.width <= 0 || elemLocation.x >= vpWidth)
            /* out of viewport */;
        else if (elemLocation.x + xOffset < 0)
            xOffset = 0;
        else if (elemLocation.x + xOffset >= vpWidth)
            xOffset = vpWidth - 1;
        if (elemLocation.y + elemSize.height <= 0 || elemLocation.y >= vpHeight)
            /* out of viewport */;
        else if (elemLocation.y + yOffset < 0)
            yOffset = 0;
        else if (elemLocation.y + yOffset >= vpHeight)
            yOffset = vpHeight - 1;
        return new Point(xOffset, yOffset);
    }

    private static Point calcOffsetOutsideElement(int vpWidth, int vpHeight, Point elemLocation, Dimension elemSize) {
        int xOffset, yOffset;
        int inside = 0;
        if (elemLocation.x - 1 >= 0) {
            xOffset = -1;
        } else if (elemLocation.x + elemSize.width < vpWidth) {
            xOffset = elemSize.width;
        } else {
            xOffset = vpWidth / 2;
            inside++;
        }
        if (elemLocation.y - 1 >= 0) {
            yOffset = -1;
        } else if (elemLocation.y + elemSize.height < vpHeight) {
            yOffset = elemSize.height;
        } else {
            yOffset = vpHeight / 2;
            inside++;
        }
        return (inside < 2) ? new Point(xOffset, yOffset) : null;
    }

    @Override
    public Void execute(Context context, String... args) {
        log.debug("Mouse event: {}", eventType.commandName);
        WebDriver driver = context.getWrappedDriver();
        List<Long> viewportSize = eval(driver,
            "return [document.documentElement.clientWidth, document.documentElement.clientHeight];");
        int vpWidth = viewportSize.get(0).intValue();
        int vpHeight = viewportSize.get(1).intValue();
        WebElement element = context.findElement(args[ARG_LOCATOR]);
        Dimension elemSize = element.getSize();
        Point elemLocation = element.getLocation();
        log.debug("Viewport Size: ({}, {}) / Element Location: {} / Element Size: {}",
            vpWidth, vpHeight, elemLocation, elemSize);
        Function<Actions, Actions> afterMovingMouse;
        Point coord;
        switch (eventType) {
        case MOUSE_OVER:
        case MOUSE_MOVE:
            coord = calcOffset(vpWidth, vpHeight, elemLocation, elemSize);
            afterMovingMouse = actions -> actions;
            break;
        case MOUSE_OUT:
            coord = calcOffsetOutsideElement(vpWidth, vpHeight, elemLocation, elemSize);
            if (coord != null) {
                log.debug("Move to: ({}, {}) on {}", coord.x, coord.y, element);
                afterMovingMouse = actions -> actions;
            } else {
                log.debug("Fire \"mouseleave\" and \"mouseout\" events by JS.");
                eval(driver, FIRE_MOUSE_OUT_EVENT, element);
                return null;
            }
            break;
        case MOUSE_MOVE_AT:
            coord = coordToPoint(args[ARG_COORD]);
            afterMovingMouse = actions -> actions;
            break;
        case MOUSE_DOWN:
            coord = calcOffset(vpWidth, vpHeight, elemLocation, elemSize);
            afterMovingMouse = actions -> actions.clickAndHold();
            break;
        case MOUSE_DOWN_AT:
            coord = coordToPoint(args[ARG_COORD]);
            afterMovingMouse = actions -> actions.clickAndHold();
            break;
        case MOUSE_UP:
            coord = calcOffset(vpWidth, vpHeight, elemLocation, elemSize);
            afterMovingMouse = actions -> actions.release();
            break;
        case MOUSE_UP_AT:
            coord = coordToPoint(args[ARG_COORD]);
            afterMovingMouse = actions -> actions.release();
            break;
        default:
            throw new UnsupportedOperationException("Unsupported command: " + eventType.commandName);
        }
        try {
            afterMovingMouse.apply(MouseUtils.moveTo(context, element, coord)).build().perform();
        } catch (MoveTargetOutOfBoundsException e) {
            log.warn("Cannot mouse pointer move to element: {}", e.getMessage());
            log.warn("Only fire \"{}\" event by JS.", eventType.eventName);
            if (eventType == MouseEventType.MOUSE_OVER) {
                eval(driver, FIRE_MOUSE_OVER_EVENT);
            } else if (eventType.hasCoord) {
                // https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent/MouseEvent
                Map<String, Object> init = new HashMap<>();
                init.put("clientX", coord.x);
                init.put("clientY", coord.y);
                eval(driver, FIRE_MOUSE_EVENT, element, eventType.eventName, init);
            } else {
                eval(driver, FIRE_MOUSE_EVENT, element, eventType.eventName);
            }
        }
        return null;
    }

    @Override
    public int getArgumentCount() {
        return eventType.argTypes.length;
    }
}
