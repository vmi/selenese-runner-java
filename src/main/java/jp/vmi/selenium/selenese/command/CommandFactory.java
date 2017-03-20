package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.SeleneseRunnerRuntimeException;
import jp.vmi.selenium.selenese.subcommand.ISubCommand;
import jp.vmi.selenium.selenese.subcommand.SubCommandMap;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Factory of selenese command.
 */
public class CommandFactory implements ICommandFactory {

    private static final Map<String, Constructor<? extends ICommand>> constructorMap = new HashMap<>();

    private static void addConstructor(Class<? extends ICommand> cmdClass, String... aliases) {
        try {
            String name = StringUtils.uncapitalize(cmdClass.getSimpleName());
            Constructor<? extends ICommand> constructor;
            constructor = cmdClass.getDeclaredConstructor(int.class/*index*/, String.class/*name*/, String[].class/*args*/);
            constructorMap.put(name, constructor);
            for (String alias : aliases)
                constructorMap.put(alias, constructor);
        } catch (Exception e) {
            throw new SeleneseRunnerRuntimeException(e);
        }
    }

    static {
        addConstructor(AddLocationStrategy.class);
        addConstructor(AddSelection.class);
        addConstructor(AllowNativeXpath.class);
        addConstructor(AltKeyDown.class);
        addConstructor(AltKeyUp.class);
        addConstructor(AnswerOnNextPrompt.class);
        addConstructor(AssignId.class);
        addConstructor(AttachFile.class);
        addConstructor(CaptureEntirePageScreenshot.class);
        addConstructor(Check.class);
        addConstructor(ChooseCancelOnNextConfirmation.class);
        addConstructor(ChooseOkOnNextConfirmation.class);
        addConstructor(Click.class);
        addConstructor(IfElementThenClick.class);
        addConstructor(GetElementByBackgroundColor.class);
        addConstructor(ClickAt.class);
        addConstructor(Close.class);
        addConstructor(ControlKeyDown.class);
        addConstructor(ControlKeyUp.class);
        addConstructor(CreateCookie.class);
        addConstructor(DeleteAllVisibleCookies.class);
        addConstructor(DeleteCookie.class);
        addConstructor(DeselectPopUp.class);
        addConstructor(DoubleClick.class);
        addConstructor(DragAndDrop.class, "dragdrop");
        addConstructor(DragAndDropToObject.class);
        addConstructor(Echo.class);
        addConstructor(FireEvent.class);
        addConstructor(Focus.class);
        addConstructor(GoBack.class);
        addConstructor(Highlight.class);
        addConstructor(KeyDown.class);
        addConstructor(KeyPress.class);
        addConstructor(KeyUp.class);
        addConstructor(MetaKeyDown.class);
        addConstructor(MetaKeyUp.class);
        addConstructor(Open.class);
        addConstructor(OpenWindow.class);
        addConstructor(Pause.class);
        addConstructor(Refresh.class);
        addConstructor(RemoveAllSelections.class);
        addConstructor(RemoveSelection.class);
        addConstructor(Rollup.class);
        addConstructor(RunScript.class);
        addConstructor(Select.class);
        addConstructor(SelectFrame.class);
        addConstructor(SelectPopUp.class);
        addConstructor(SelectWindow.class);
        addConstructor(SetCursorPosition.class);
        addConstructor(SetSpeed.class);
        addConstructor(SetTimeout.class);
        addConstructor(ShiftKeyDown.class);
        addConstructor(ShiftKeyUp.class);
        addConstructor(Submit.class);
        addConstructor(Type.class);
        addConstructor(TypeKeys.class, "sendKeys");
        addConstructor(Uncheck.class);
        addConstructor(UseXpathLibrary.class);
        addConstructor(WaitForCondition.class);
        addConstructor(WaitForPageToLoad.class);
        addConstructor(WaitForPopUp.class);
        addConstructor(WindowFocus.class);
        addConstructor(WindowMaximize.class);

        // commands of selenium-ide-flowcontrol
        // https://github.com/davehunt/selenium-ide-flowcontrol
        addConstructor(While.class);
        addConstructor(EndWhile.class);
        addConstructor(AddCollection.class);
        addConstructor(AddToCollection.class);
        addConstructor(StoreFor.class);
        addConstructor(EndFor.class);
        addConstructor(Label.class);
        addConstructor(Gotolabel.class, "gotoLabel");
        addConstructor(GotoIf.class);

        // commands for comment
        addConstructor(Comment.class);

        // commands for include
        addConstructor(Include.class);
    }

    private static final String AND_WAIT = "AndWait";

    private static final Pattern COMMAND_PATTERN = Pattern.compile(
        "(?:(assert|verify|waitFor)(Not)?|store)(?:(.+?)(?:(Not)?(Present))?)?",
        Pattern.CASE_INSENSITIVE);

    private static final int ASSERTION = 1;
    private static final int IS_INVERSE = 2;
    private static final int TARGET = 3;
    private static final int IS_PRESENT_INVERSE = 4;
    private static final int PRESENT = 5;

    private final List<ICommandFactory> commandFactories = new ArrayList<>();

    private Context context = null;

    /**
     * Constructor.
     *
     * @param context Selenese Runner context.
     */
    public CommandFactory(Context context) {
        this.context = context;
    }

    /**
     * Register user defined comman factory.
     *
     * @param factory ICommandFactory object.
     */
    public void registerCommandFactory(ICommandFactory factory) {
        commandFactories.add(factory);
    }

    @Override
    public ICommand newCommand(int index, String name, String... args) {
        // user defined command.
        for (ICommandFactory factory : commandFactories) {
            ICommand command = factory.newCommand(index, name, args);
            if (command != null)
                return command;
        }

        boolean andWait = name.endsWith(AND_WAIT);
        String realName = andWait ? name.substring(0, name.length() - AND_WAIT.length()) : name;

        // command supported by the c ICommand without BuiltInCommand
        Constructor<? extends ICommand> constructor = constructorMap.get(realName);
        if (constructor != null) {
            try {
                return constructor.newInstance(index, name, args);
            } catch (Exception e) {
                throw new SeleneseRunnerRuntimeException(e);
            }
        }

        // command supported by WebDriverCommandProcessor
        SubCommandMap subCommandMap = context.getSubCommandMap();
        ISubCommand<?> subCommand = subCommandMap.get(realName);
        if (subCommand != null)
            return new BuiltInCommand(index, name, args, subCommand, andWait);

        // See: http://selenium.googlecode.com/svn/trunk/ide/main/src/content/selenium-core/reference.html
        // Assertion or Accessor
        Matcher matcher = COMMAND_PATTERN.matcher(name);
        if (!matcher.matches())
            throw new SeleneseRunnerRuntimeException("No such command: " + name);
        String assertion = matcher.group(ASSERTION);
        String target = matcher.group(TARGET);
        if (target == null)
            target = "Expression";
        if (matcher.group(PRESENT) != null)
            target += "Present";
        boolean isBoolean = false;
        String getter = "get" + target;
        ISubCommand<?> getterSubCommand = subCommandMap.get(getter);
        if (getterSubCommand == null) {
            getter = "is" + target;
            getterSubCommand = subCommandMap.get(getter);
            if (getterSubCommand == null)
                throw new SeleneseRunnerRuntimeException("No such command: " + name);
            isBoolean = true;
        }
        if (assertion != null) {
            boolean isInverse = matcher.group(IS_INVERSE) != null || matcher.group(IS_PRESENT_INVERSE) != null;
            return new Assertion(index, name, args, assertion, getterSubCommand, isBoolean, isInverse);
        } else { // Accessor
            return new Store(index, name, args, getterSubCommand);
        }
    }

    /**
     * Get command entries.
     *
     * @return command entries.
     */
    public static Set<Entry<String, Constructor<? extends ICommand>>> getCommandEntries() {
        return Collections.unmodifiableSet(constructorMap.entrySet());
    }
}
