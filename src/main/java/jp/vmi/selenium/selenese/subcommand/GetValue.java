// This code is based on:
//
//   com.thoughtworks.selenium.webdriven.commands.GetValue
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

package jp.vmi.selenium.selenese.subcommand;

import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.ArgumentType;

/**
 * Re-implementation of GetValue.
 */
public class GetValue extends AbstractSubCommand<String> {

    private static final int ARG_LOCATOR = 0;

    /**
     * Constructor.
     */
    public GetValue() {
        super(ArgumentType.LOCATOR);
    }

    @Override
    public String execute(Context context, String... args) {
        WebElement element = context.findElement(args[ARG_LOCATOR]);
        // Special-case handling for checkboxes and radio buttons: The Selenium API returns "on" for
        // checked checkboxes and radio buttons and off for unchecked ones. WebDriver will return "null" for
        // the "checked" attribute if the checkbox or the radio button is not-checked, "true" otherwise.
        if (element.getTagName().equals("input")) {
            String type = element.getAttribute("type");
            if ("checkbox".equalsIgnoreCase(type) || "radio".equalsIgnoreCase(type))
                return element.getAttribute("checked") != null ? "on" : "off";
        }
        return element.getAttribute("value");
    }
}
