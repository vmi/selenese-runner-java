// This code is based on:
//
//   com.thoughtworks.selenium.webdriven.commands.AttachFile
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FilenameUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.io.TemporaryFilesystem;

import com.google.common.io.Resources;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Warning;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Re-implementation of AttachFile.
 */
public class AttachFile extends AbstractCommand {

    private static final int ARG_LOCATOR = 0;
    private static final int ARG_FILENAME = 1;

    AttachFile(int index, String name, String... args) {
        super(index, name, args, LOCATOR, VALUE);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String name = curArgs[ARG_FILENAME];
        File outputTo = null;
        if (name.contains("://")) {
            // process (remote) url
            URL url;
            try {
                url = new URL(name);
            } catch (MalformedURLException e) {
                return new Error("Malformed URL: " + name);
            }
            File dir = TemporaryFilesystem.getDefaultTmpFS().createTempDir("attachFile", "dir");
            outputTo = new File(dir, new File(url.getFile()).getName());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outputTo);
                Resources.copy(url, fos);
            } catch (IOException e) {
                return new Error("Can't access file to upload: " + url, e);
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    return new Warning("Unable to close stream used for reading file: " + name, e);
                }
            }
        } else {
            // process file besides testcase file
            outputTo = new File(FilenameUtils.getPath(context.getCurrentTestCase().getFilename()), name);
            if (!outputTo.exists()) {
                return new Error("Can't access file: " + outputTo);
            }
        }

        WebElement element = context.findElement(curArgs[ARG_LOCATOR]);
        try {
            element.clear();
        } catch (Exception e) {
            // ignore exceptions from some drivers when file-input cannot be cleared;
        }
        element.sendKeys(outputTo.getAbsolutePath());
        return SUCCESS;
    }
}
