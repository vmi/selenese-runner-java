// This code is based on:
//
//   com.thoughtworks.selenium.webdriven.commands.CreateCookie
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.Cookie;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Re-implementation of CreateCookie.
 */
public class CreateCookie extends AbstractCommand {

    private static final int ARG_NAME_VALUE_PAIR = 0;
    private static final int ARG_OPTIONS_STRING = 1;

    CreateCookie(int index, String name, String... args) {
        super(index, name, args, VALUE, VALUE);
    }

    private static final Pattern NAME_VALUE_PAIR_PATTERN = Pattern.compile("([^\\s=\\[\\]\\(\\),\"\\/\\?@:;]+)=([^\\[\\]\\(\\),\"\\/\\?@:;]*)");
    private static final Pattern MAX_AGE_PATTERN = Pattern.compile("max_age=(\\d+)");
    private static final Pattern PATH_PATTERN = Pattern.compile("path=([^\\s,]+)[,]?");

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String nameValuePair = curArgs[ARG_NAME_VALUE_PAIR];
        String optionsString = curArgs[ARG_OPTIONS_STRING];

        Matcher nameValuePairMatcher = NAME_VALUE_PAIR_PATTERN.matcher(nameValuePair);
        if (!nameValuePairMatcher.find())
            return new Error("Invalid parameter: " + nameValuePair);

        String name = nameValuePairMatcher.group(1);
        String value = nameValuePairMatcher.group(2);

        Matcher maxAgeMatcher = MAX_AGE_PATTERN.matcher(optionsString);
        Date maxAge = null;

        if (maxAgeMatcher.find())
            maxAge = new Date(System.currentTimeMillis() + Integer.parseInt(maxAgeMatcher.group(1)) * 1000);

        String path = null;
        Matcher pathMatcher = PATH_PATTERN.matcher(optionsString);
        if (pathMatcher.find()) {
            path = pathMatcher.group(1);
            try {
                if (path.startsWith("http"))
                    path = new URL(path).getPath();
            } catch (MalformedURLException e) {
                // Fine.
            }
        }

        Cookie cookie = new Cookie(name, value, path, maxAge);
        context.getWrappedDriver().manage().addCookie(cookie);

        return SUCCESS;
    }
}
