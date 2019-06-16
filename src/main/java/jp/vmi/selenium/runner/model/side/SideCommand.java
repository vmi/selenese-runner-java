package jp.vmi.selenium.runner.model.side;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import jp.vmi.selenium.runner.model.ICommand;

/**
 * "command" element of side format.
 *
 * <p>
 * See: https://github.com/SeleniumHQ/selenium-ide/blob/v3.8.1/packages/selenium-ide/src/neo/models/Command/index.js
 * </p>
 */
@SuppressWarnings("javadoc")
public class SideCommand implements ICommand {

    private static final long DEFAULT_NEW_WINDOW_TIMEOUT = 2000;

    private String id;
    private String comment = "";
    private String command;
    private String target;
    private List<List<String>> targets = Collections.emptyList();
    private String value;
    private boolean isBreakpoint = false;
    private boolean opensWindow = false;
    private String windowHandleName = "";
    private long windowTimeout = DEFAULT_NEW_WINDOW_TIMEOUT;
    private boolean opensWindowRead = false;

    public SideCommand(boolean isGen) {
        if (isGen)
            id = UUID.randomUUID().toString();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public List<List<String>> getTargets() {
        return targets;
    }

    public void setTargets(List<List<String>> targets) {
        this.targets = targets;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean isBreakpoint() {
        return isBreakpoint;
    }

    public void setBreakpoint(boolean isBreakpoint) {
        this.isBreakpoint = isBreakpoint;
    }

    @Override
    public boolean isOpensWindow() {
        return opensWindow;
    }

    public void setOpensWindow(boolean opensWindow) {
        this.opensWindow = opensWindow;
    }

    @Override
    public String getWindowHandleName() {
        return windowHandleName;
    }

    public void setWindowHandleName(String windowHandleName) {
        this.windowHandleName = windowHandleName;
    }

    @Override
    public long getWindowTimeout() {
        return windowTimeout;
    }

    public void setWindowTimeout(long windowTimeout) {
        this.windowTimeout = windowTimeout;
    }

    @Override
    public boolean isOpensWindowRead() {
        return opensWindowRead;
    }

    public void setOpensWindowRead(boolean opensWindowRead) {
        this.opensWindowRead = opensWindowRead;
    }
}
