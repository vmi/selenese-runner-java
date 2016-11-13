// This code is based on:
//
//   com.thoughtworks.selenium.webdriven.commands.GetTable
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.SeleneseRunnerRuntimeException;
import jp.vmi.selenium.selenese.command.ArgumentType;

/**
 * Re-implementation of GetTable.
 */
public class GetTable extends AbstractSubCommand<String> {

    private static final Pattern TABLE_PARTS = Pattern.compile("(.*)\\.(\\d+)\\.(\\d+)");

    private static final int ARG_TABLE_CELL_ADDRESS = 0;

    /**
     * Constructor.
     */
    public GetTable() {
        super(ArgumentType.VALUE);
    }

    @Override
    public String execute(Context context, String... args) {
        Matcher matcher = TABLE_PARTS.matcher(args[ARG_TABLE_CELL_ADDRESS]);
        if (!matcher.matches())
            throw new SeleneseRunnerRuntimeException("Invalid target format. Correct format is tableName.rowNum.columnNum");
        WebDriver driver = context.getWrappedDriver();
        String tableLocator = matcher.group(1);
        WebElement table = context.getElementFinder().findElement(driver, tableLocator);
        long row = Long.parseLong(matcher.group(2));
        long col = Long.parseLong(matcher.group(3));
        Object result = context.getJSLibrary().getTable(driver, table, row, col);
        if (result instanceof WebElement)
            return ((WebElement) result).getText().trim();
        throw new SeleneseRunnerRuntimeException((String) result);
    }
}
