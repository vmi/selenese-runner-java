// This code is based on:
//
//   com.thoughtworks.selenium.webdriven.commands.WaitForPageToLoad
//
// in Selenium WebDriver.
//
// The following copyright is copied from original.
// ---
// Licensed to the Software Freedom Conservancy (SFC) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The SFC licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package jp.vmi.selenium.selenese.command;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.SeleneseCommandErrorException;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.Wait;
import jp.vmi.selenium.selenese.utils.Wait.StopCondition;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Re-implementation of "waitForPageToLoad".
 */
public class WaitForPageToLoad extends AbstractCommand {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(WaitForPageToLoad.class);

    private static final int ARG_TIMEOUT = 0;

    WaitForPageToLoad(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        long timeout = 0; // no wait
        if (curArgs.length > 0) {
            String arg = curArgs[ARG_TIMEOUT];
            if (!Strings.isNullOrEmpty(arg)) {
                try {
                    timeout = Long.parseLong(arg);
                } catch (NumberFormatException e) {
                    return new Error(e);
                }
            }
        }
        return execute(context, timeout);
    }

    /**
     * Wait for page to load.
     *
     * @param context context object.
     * @param timeout timeout (ms).
     * @return page is loaded within timeout if result value is Success.
     */
    public static Result execute(Context context, long timeout) {
        if (timeout < 0)
            return new Error("Illegal timeout parameter: " + timeout);
        else if (timeout == 0)
            return SUCCESS;
        long startTime = System.currentTimeMillis();
        WebDriver driver = context.getWrappedDriver();
        if (!(driver instanceof JavascriptExecutor))
            return new Error("WebDriver is not support JavaScript.");
        StopCondition condition = isReadyStateSupported(driver) ? checkByReadyState(driver) : checkByBodyLength(driver);
        if (!Wait.defaultInterval.wait(startTime, timeout, condition))
            return new Error("Failed to load page within " + timeout + " ms");
        return SUCCESS;
    }

    private static boolean isReadyStateSupported(WebDriver driver) {
        try {
            return isReadyStateSupportedInternal(driver);
        } catch (WebDriverException e) {
            // no operation.
        }
        Wait.sleep(250);
        try {
            return isReadyStateSupportedInternal(driver);
        } catch (WebDriverException e) {
            // no operation.
        }
        Wait.sleep(500);
        try {
            return isReadyStateSupportedInternal(driver);
        } catch (WebDriverException e) {
            throw new SeleneseCommandErrorException("Cannot determine whether page supports ready state.");
        }
    }

    private static boolean isReadyStateSupportedInternal(WebDriver driver) {
        Boolean result = (Boolean) ((JavascriptExecutor) driver).executeScript("return !!document['readyState'];");
        return result != null ? result : false;
    }

    private static StopCondition checkByReadyState(final WebDriver driver) {
        return new StopCondition() {
            @Override
            public boolean isSatisfied() {
                try {
                    Boolean result = (Boolean) ((JavascriptExecutor) driver).executeScript("return document.readyState === 'complete';");
                    if (result != null && result)
                        return true;
                } catch (Exception e) {
                    // no operation.
                }
                return false;
            }
        };
    }

    private static StopCondition checkByBodyLength(final WebDriver driver) {
        return new StopCondition() {

            private int prevLen = -1;
            private long prevTime = 0;

            @Override
            public boolean isSatisfied() {
                WebElement body;
                try {
                    body = driver.findElement(By.tagName("body"));
                } catch (NoSuchElementException e) {
                    return false;
                }
                String text = body.getText();
                if (text == null)
                    return false;
                int len = text.length();
                long now = System.currentTimeMillis();
                if (prevLen == len)
                    return now - prevTime > 1000;
                prevLen = len;
                prevTime = now;
                return false;
            }
        };
    }
}
