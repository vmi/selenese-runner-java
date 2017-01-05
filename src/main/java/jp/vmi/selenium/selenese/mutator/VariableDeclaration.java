// This code is based on:
//
//   com.thoughtworks.selenium.webdriven.VariableDeclaration
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

import java.util.regex.Pattern;

import jp.vmi.selenium.selenese.Context;

/**
 * Prepend a variable declaration to a script.
 */
public class VariableDeclaration implements ScriptMutator {

    private final Pattern pattern;
    private final String declaration;

    /**
     * @param varName variable name.
     * @param value value.
     */
    public VariableDeclaration(String varName, String value) {
        pattern = MutatorUtils.generatePatternForCodePresence(varName);
        if (varName.indexOf('.') < 0)
            declaration = "var " + varName + " = " + value + ";";
        else
            declaration = varName + " = " + value + ";";
    }

    @Override
    public void mutate(Context context, String script, StringBuilder outputTo) {
        if (pattern.matcher(script).find())
            outputTo.append(declaration);
    }
}
