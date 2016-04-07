package jp.vmi.selenium.selenese.command;

import org.apache.commons.io.FilenameUtils;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.InvalidSeleneseException;
import jp.vmi.selenium.selenese.Parser;
import jp.vmi.selenium.selenese.Selenese;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.CommandResult;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "include".
 */
public class Include extends AbstractCommand {

    private static final int ARG_FILENAME = 0;

    Include(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        TestCase current = context.getCurrentTestCase();
        String filename = context.getVarsMap().replaceVars(curArgs[ARG_FILENAME]);
        if (FilenameUtils.getPrefixLength(filename) == 0) {
            String path = FilenameUtils.getFullPathNoEndSeparator(current.getFilename());
            filename = FilenameUtils.concat(path, filename);
        }
        Selenese child = Parser.parse(filename, context.getCommandFactory());
        if (child instanceof TestCase) {
            String seq = context.getCommandListIterator().getCommandSequence().toString();
            StartMarker marker = new StartMarker(this, "Start: " + filename);
            long now = System.currentTimeMillis();
            CommandResult markerResult = new CommandResult(seq, marker, marker.getScreenshots(), marker.getResult(), now, now);
            current.getResultList().add(markerResult);
            try {
                Result result = child.execute(current, context);
                return result == SUCCESS ? new Success("Success: " + filename) : result;
            } catch (InvalidSeleneseException e) {
                return new Error(e);
            }
        } else {
            return new Error("TestCase expected: " + filename);
        }
    }
}
