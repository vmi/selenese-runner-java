package jp.vmi.selenium.selenese.command;

import java.net.URI;
import java.net.URISyntaxException;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "open".
 */
public class Open extends Command {

    private static final int URL = 0;

    Open(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args, 1);
    }

    @Override
    protected Result doCommandImpl(TestCase testCase, Runner runner) {
        String url = runner.getVarsMap().replaceVars(args[URL]);
        if (!url.contains("://")) {
            try {
                url = new URI(runner.getCurrentBaseURL()).resolve(url).toASCIIString();
            } catch (URISyntaxException e) {
                throw new SeleniumException(e);
            }
        }
        runner.getWrappedDriver().get(url);
        return SUCCESS;
    }
}
