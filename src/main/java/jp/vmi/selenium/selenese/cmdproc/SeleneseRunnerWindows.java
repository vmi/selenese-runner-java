/*
Modified for Selenese Runner by Motonori IWAMURO.

Original:

Copyright 2007-2009 Selenium committers

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package jp.vmi.selenium.selenese.cmdproc;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.internal.seleniumemulation.Windows;

import com.google.common.collect.Maps;
import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.selenese.Context;

/**
 * Substitute of org.openqa.selenium.internal.seleniumemulation.Windows.
 */
public class SeleneseRunnerWindows extends Windows {

    private final Context context;
    private final Map<String, String> lastFrame = Maps.newHashMap();

    /**
     * Constructor.
     * 
     * @param context SeleneseRunner context.
     */
    public SeleneseRunnerWindows(Context context) {
        super(new NullDriver());
        this.context = context;
    }

    @Override
    public void selectWindow(WebDriver driver, String windowID) {
        TargetLocator switchTo = driver.switchTo();
        if (StringUtils.isEmpty(windowID) || "null".equals(windowID)) {
            switchTo.window(context.getInitialWindowHandle());
        } else if ("_blank".equals(windowID)) {
            selectBlankWindow(driver);
        } else {
            String[] pair = windowID.split("=", 2);
            if (pair.length == 2) {
                if ("name".equals(pair[0])) {
                    windowID = pair[1];
                } else if ("title".equals(pair[0])) {
                    selectWindowWithTitle(driver, pair[1]);
                    return;
                }
            }
            try {
                switchTo.window(windowID);
            } catch (NoSuchWindowException e) {
                selectWindowWithTitle(driver, windowID);
            }
        }
        String windowHandle = driver.getWindowHandle();
        if (lastFrame.containsKey(windowHandle)) {
            try {
                selectFrame(driver, lastFrame.get(windowHandle));
            } catch (SeleniumException e) {
                lastFrame.remove(windowHandle);
            }
        }
    }

    @Override
    public void selectPopUp(WebDriver driver, String windowID) {
        if (StringUtils.isEmpty(windowID) || "null".equals(windowID)) {
            Set<String> windowHandles = driver.getWindowHandles();
            windowHandles.remove(context.getInitialWindowHandle());
            if (windowHandles.size() > 0)
                driver.switchTo().window(windowHandles.iterator().next());
            else
                throw new SeleniumException("Unable to find a popup window");
        } else {
            selectWindow(driver, windowID);
        }
    }

    @Override
    public void selectFrame(WebDriver driver, String locator) {
        TargetLocator switchTo = driver.switchTo();
        if ("relative=top".equals(locator)) {
            switchTo.defaultContent();
            lastFrame.remove(driver.getWindowHandle());
            return;
        }
        String[] pair = locator.split("=", 2);
        if (pair.length == 2) {
            if ("index".equals(pair[0])) {
                try {
                    int index = Integer.parseInt(pair[1]);
                    lastFrame.put(driver.getWindowHandle(), locator);
                    switchTo.frame(index);
                    return;
                } catch (NumberFormatException e) {
                    throw new SeleniumException(e.getMessage(), e);
                } catch (NoSuchFrameException e) {
                    throw new SeleniumException(e.getMessage(), e);
                }
            } else if ("id".equals(pair[0]) || "name".equals(pair[0])) {
                locator = pair[1];
            }
        }
        try {
            lastFrame.put(driver.getWindowHandle(), locator);
            switchTo.frame(locator);
        } catch (NoSuchFrameException e) {
            throw new SeleniumException(e.getMessage(), e);
        }
    }

    private void selectWindowWithTitle(WebDriver driver, String title) {
        TargetLocator switchTo = driver.switchTo();
        String current = driver.getWindowHandle();
        for (String handle : driver.getWindowHandles()) {
            switchTo.window(handle);
            if (title.equals(driver.getTitle()))
                return;
        }
        switchTo.window(current);
        throw new SeleniumException("Unable to select window with title: " + title);
    }

    @Override
    public void selectBlankWindow(WebDriver driver) {
        TargetLocator switchTo = driver.switchTo();
        String current = driver.getWindowHandle();
        for (String handle : driver.getWindowHandles()) {
            if (handle.equals(context.getInitialWindowHandle()))
                continue;
            switchTo.window(handle);
            String title = (String) ((JavascriptExecutor) driver).executeScript("return window.name;");
            if (StringUtils.isEmpty(title))
                return;
        }
        switchTo.window(current);
        throw new SeleniumException("Unable to select window _blank");
    }
}
