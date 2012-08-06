package jp.vmi.selenium.selenese.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.TestCase;

public class Echo extends Command {

    private static final Logger log = LoggerFactory.getLogger(Echo.class);

    private static final int MESSAGE = 0;

    Echo(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args);
    }

    @Override
    public Result doCommand(TestCase testCase) {
        log.info(testCase.replaceVariables(args[MESSAGE]));
        return SUCCESS;
    }
}
