package jp.vmi.selenium.selenese.command;

import org.apache.commons.io.FilenameUtils;

import jp.vmi.selenium.selenese.Parser;
import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.Selenese;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Error;
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

        String filename = context.getVarsMap().replaceVars(curArgs[ARG_FILENAME]);
        String path = FilenameUtils.getFullPathNoEndSeparator(context.getCurrentTestCase().getFilename());

        if (FilenameUtils.getPrefixLength(filename) == 0)
            filename = FilenameUtils.concat(path, filename);

        Selenese child = Parser.parse(filename, context.getCommandFactory());
        if (child instanceof TestCase) {
            CommandList commandList = ((TestCase)child).getCommandList();
            Result result = commandList.execute(context);
            return result == SUCCESS ? new Success("Success: " + filename) : result;
        }

        return new Error("TestCase expected: " + filename);
    }
}
