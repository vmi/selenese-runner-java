// This code is based on:
//
//   com.thoughtworks.selenium.webdriven.CompoundMutator
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

package jp.vmi.selenium.selenese.mutator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.vmi.selenium.selenese.Context;

/**
 * A class that collects together a group of other mutators and applies them in the order they've
 * been added to any script that needs modification. Any JS to be executed will be wrapped in an
 * "eval" block so that a meaningful return value can be created.
 */
public class SeleneseRunnerMutator implements ScriptMutator {
    // The ordering of mutators matters
    private final List<ScriptMutator> mutators = new ArrayList<>();

    /**
     * Constructor.
     */
    public SeleneseRunnerMutator() {
        // variables
        mutators.add(new VariableDeclaration("selenium", "{}"));
        mutators.add(new VariableDeclaration("selenium.browserbot", "{}"));
        mutators.add(new VariableDeclarationWithDynamicValue(
            "selenium.browserbot.baseUrl", context -> context.getCurrentBaseURL()));
        mutators.add(new VariableDeclaration("browserVersion", "{}"));
        mutators.add(new VariableDeclaration("browserVersion.isFirefox",
            "navigator.userAgent.indexOf('Firefox') != -1 || " +
                "navigator.userAgent.indexOf('Namoroka') != -1 " +
                "|| navigator.userAgent.indexOf('Shiretoko') != -1"));
        mutators.add(new VariableDeclaration("browserVersion.isGecko",
            "navigator.userAgent.indexOf('Firefox') != -1 || " +
                "navigator.userAgent.indexOf('Namoroka') != -1 " +
                "|| navigator.userAgent.indexOf('Shiretoko') != -1"));
        mutators.add(new VariableDeclaration("browserVersion.firefoxVersion",
            "(function() {" +
                "var r = /.*[Firefox|Namoroka|Shiretoko]\\/([\\d\\.]+).*/.exec(navigator.userAgent);" +
                "return r ? r[1] : '';" +
                "})()"));
        mutators.add(new VariableDeclaration("browserVersion.isIE",
            "navigator.appName == 'Microsoft Internet Explorer'"));

        // Functions
        mutators.add(new FunctionDeclaration("selenium.page",
            "if (!selenium.browserbot) { selenium.browserbot = {} }; return selenium.browserbot;"));
        mutators.add(new FunctionDeclaration("selenium.browserbot.getCurrentWindow", "return window;"));
        mutators.add(new FunctionDeclaration("selenium.browserbot.getUserWindow", "return window;"));
        mutators.add(new FunctionDeclaration("selenium.page().getCurrentWindow", "return window;"));
        mutators.add(new FunctionDeclaration("selenium.browserbot.getDocument", "return document;"));
        mutators.add(new FunctionDeclaration("selenium.page().getDocument", "return document;"));
    }

    @Override
    public void mutate(Context context, String script, StringBuilder outputTo) {
        StringBuilder nested = new StringBuilder();
        for (ScriptMutator mutator : mutators)
            mutator.mutate(context, script, nested);
        nested.append(script);

        outputTo.append("return eval('");
        escape(nested, outputTo);
        outputTo.append("');");
    }

    private static final Pattern ESC_RE = Pattern.compile("[\\\\\n']");

    private void escape(CharSequence escape, StringBuilder outputTo) {
        Matcher matcher = ESC_RE.matcher(escape);
        int start = 0;
        while (matcher.find(start)) {
            if (start < matcher.start())
                outputTo.append(escape.subSequence(start, matcher.start()));
            switch (matcher.group()) {
            case "\\":
                outputTo.append("\\\\");
                break;
            case "\n":
                outputTo.append("\\n");
                break;
            case "'":
                outputTo.append("\\'");
                break;
            }
            start = matcher.end();
        }
        if (start < escape.length())
            outputTo.append(escape.subSequence(start, escape.length()));
    }
}
