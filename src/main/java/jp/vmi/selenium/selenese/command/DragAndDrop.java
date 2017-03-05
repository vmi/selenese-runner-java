// This code is based on:
//
//   com.thoughtworks.selenium.webdriven.commands.DragAndDrop
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

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Re-implementation of DragAndDrop.
 */
public class DragAndDrop extends AbstractCommand {

    private static final int ARG_LOCATOR = 0;
    private static final int ARG_MOVEMENTS_STRING = 1;

    DragAndDrop(int index, String name, String... args) {
        super(index, name, args, LOCATOR, VALUE);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String[] parts = curArgs[ARG_MOVEMENTS_STRING].split("\\s*,\\s*", 2);
        int xDelta;
        int yDelta;
        try {
            xDelta = Integer.parseInt(parts[0].trim());
            yDelta = Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return new Error("Invalid movements: " + curArgs[ARG_MOVEMENTS_STRING]);
        }
        WebDriver driver = context.getWrappedDriver();
        WebElement element = context.getElementFinder().findElement(driver, curArgs[ARG_LOCATOR]);
        new Actions(driver).dragAndDropBy(element, xDelta, yDelta).perform();
        return SUCCESS;
    }
}
