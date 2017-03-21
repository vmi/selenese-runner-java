Selenese Runner Java Relase Note
================================

### 3.3.0

* Fix to output an error if the specified profile does not exist.
* Add local file detector for file uploading by RemoteWebDriver. (#231)
* Catch up Selenium 3.3.1.
* Update dependency versions.
* Fix FirefoxDriver initialization failed. (#232)
    * Note: Several tests fail with Firefox. I think that this is a problem of Selenium 3.3.1.

### 3.2.0

* Add new option `--geckodriver` which specifies geckodriver binary path. (#226)
* Fix the behavior of `(assert|verify|waitFor|store)Editable` according to Selenium IDE. (#227)
* Re-fix the behavior of `click` command for invisible element. (#224)

### 3.1.0

* Fix `click` command to fail when the element is not visible. (#224)
* Update dependency versions.

### 3.0.1

* Fix a bug that can not get version number of Selenium when executable jar is used.

### 3.0.0

* Catch up Selenium 3.0.1.
* Update dependency versions.
* Fire `change` event at the end of `type` command. (#223)
* Re-implement the following commands for catching up Selenium 3.x. (#218)
    * `addLocationStrategy`
    * `addSelection`
    * `allowNativeXpath`
    * `answerOnNextPrompt`
    * `assignId`
    * `attachFile`
    * `chooseCancelOnNextConfirmation`
    * `chooseOkOnNextConfirmation`
    * `close`
    * `createCookie`
    * `deleteAllVisibleCookies`
    * `deleteCookie`
    * `dragAndDrop`
    * `dragAndDropToObject`
    * `fireEvent`
    * `focus`
    * `goBack`
    * `refresh`
    * `removeAllSelections`
    * `removeSelection`
    * `selectElement`
    * `setCursorPosition`
    * `useXpathLibrary`
    * `windowFocus`
    * `(assert|verify|waitFor|store)AllButtons`
    * `(assert|verify|waitFor|store)AllFields`
    * `(assert|verify|waitFor|store)AllLinks`
    * `(assert|verify|waitFor|store)AllWindowTitles`
    * `(assert|verify|waitFor|store)Attribute`
    * `(assert|verify|waitFor|store)AttributeFromAllWindows`
    * `(assert|verify|waitFor|store)BodyText`
    * `(assert|verify|waitFor|store)Cookie`
    * `(assert|verify|waitFor|store)CookieByName`
    * `(assert|verify|waitFor|store)CursorPosition`
    * `(assert|verify|waitFor|store)ElementHeight`
    * `(assert|verify|waitFor|store)ElementIndex`
    * `(assert|verify|waitFor|store)ElementPositionLeft`
    * `(assert|verify|waitFor|store)ElementPositionTop`
    * `(assert|verify|waitFor|store)ElementWidth`
    * `(assert|verify|waitFor|store)Expression`
    * `(assert|verify|waitFor|store)HtmlSource`
    * `(assert|verify|waitFor|store)Location`
    * `(assert|verify|waitFor|store)SelectOptions`
    * `(assert|verify|waitFor|store)Table`
    * `(assert|verify|waitFor|store)Text`
    * `(assert|verify|waitFor|store)Title`
    * `(assert|verify|waitFor|store)Value`
    * `(assert|verify|waitFor|store)XpathCount`
    * `(assert|verify|waitFor|store)Checked`
    * `(assert|verify|waitFor|store)CookiePresent`
    * `(assert|verify|waitFor|store)Editable`
    * `(assert|verify|waitFor|store)ElementPresent`
    * `(assert|verify|waitFor|store)Ordered`
    * `(assert|verify|waitFor|store)Visible`

### 2.13.0

* Fix that "clickAt" command ignores coordinates parameter. (#221)

### 2.12.0

* Fix failure on `(assert|verify|waitFor|store)Not*` commands. (#220)

### 2.11.0

* Add new option `--interactive` which enables the interactive mode. (#210, #217 by tgianko)

### 2.10.0

* Add new option `--max-time` which limits processing time. (#207 by koichirok)
* Fix incorrect screenshot links in HTML result if target directories do not exist. (#211)
* Re-implement `(assert|verify|waitFor|store)TextPresent` for frame/iframe.
* Re-implement the following commands for handling dialogs: (#203, #206)
    * `altKeyDown`
    * `altKeyUp`
    * `answerOnNextPrompt`
    * `check`
    * `click`
    * `clickAt`
    * `controlKeyDown`
    * `controlKeyUp`
    * `doubleClick`
    * `keyDown`
    * `keyUp`
    * `metaKeyDown`
    * `metaKeyUp`
    * `select`
    * `sendKeys`
    * `shiftKeyDown`
    * `shiftKeyUp`
    * `submit`
    * `type`
    * `typeKeys`
    * `uncheck`
    * `(assert|verify|waitFor|store)Alert`
    * `(assert|verify|waitFor|store)Confirmation`
    * `(assert|verify|waitFor|store)Prompt`
    * `(assert|verify|waitFor|store)AlertPresent`
    * `(assert|verify|waitFor|store)ConfirmationPresent`
    * `(assert|verify|waitFor|store)PromptPresent`

### 2.9.0

* Catch up Selenium 2.53.1.
* Update dependency versions.
* Refine `pom.xml`.
* Re-implement the following commands: (#199)
    * `selectWindow`
    * `selectPopUp`
    * `deselectPopUp`
    * `waitForPopUp`
    * `storeAllWindowNames`

### 2.8.0

* Add millisecond of start/end time in sequence tab of HTML result.
* Add extracting array/collection value in variable substitution.
* Refine code.
* Re-implement `waitForPageToLoad` and `waitForCondition`. (#197)

### 2.7.0

* Re-enabled alert/confirm commands because Selenium supported the feature with `localStorage`. (#32)
* Add new supported commands for prompt: (#192)
    * `answerOnNextPrompt`
    * `assertNotPrompt`
    * `assertPrompt`
    * `assertPromptNotPresent`
    * `assertPromptPresent`
    * `storePrompt`
    * `storePromptPresent`
    * `verifyNotPrompt`
    * `verifyPrompt`
    * `verifyPromptNotPresent`
    * `verifyPromptPresent`
    * `waitForNotPrompt`
    * `waitForPrompt`
    * `waitForPromptNotPresent`
    * `waitForPromptPresent`
* Add new command: `include`. (PR #193 by @RLasinski)
* Re-fix white space normalizing. (#195)
* Update dependency versions.

### 2.6.0

* Catch up Selenium 2.53.0.
* Update dependency versions.
* Refine code.
* Re-implement the test web server.
* Add initialization of pageLoadTimeout. (#190)
* Add white space normalizing. (#191)

### 2.5.0

* Replace command line arguments parser from commons-cli to args4j. **Note:** There are too many changes in `IConfig`, `DefaultConfig`, and `SeleneseRunnerOptions` classes.
* Refine code. (PR #185, #187 by AymanDF)
* Update dependency versions.
* Add utility script.

### 2.4.0

* Replace JSON library from org.json to com.google.code.gson.
* Catch up Selenium 2.52.0.
* Update dependency versions.

### 2.3.1

* Fix problem with path relativize double URI escaping items like spaces. (PR #183 by stevenebutler)
* Catch up Selenium 2.50.1.

### 2.3.0

* Avoid NPE if TestCase has no LogRecorder. (PR #176 by deki)
* Add new API to register custom interceptors for test-case/test-suite execution. (#175)
* Add "class" locator type. (#173, PR #177 by deki)
* Revise implementation of mouse event commands: (#179)
    * `mouseOver`
    * `mouseOut`
    * `mouseMove`
    * `mouseMoveAt`
    * `mouseDown`
    * `mouseDownAt`
    * `mouseUp`
    * `mouseUpAt`
* Catch up Selenium 2.50.0. (PR #181 by deki)
* Update dependency versions.

### 2.2.0

* Change default behaviour of FirefoxDriver: (#172, PR #174 by Harinus)
    * Don't show default welcome page. (show "about:blank" instead)
    * Empty "no proxy" hosts by default. (Firefox default is "localhost, 127.0.0.1")  
      https://developer.mozilla.org/en-US/docs/Mozilla/Preferences/Mozilla_networking_preferences#Proxy

### 2.1.0

* Catch up Selenium 2.48.2.
* Update dependency versions.
* Fix RollupManager crash on latest Java 8. (#168, #170)
* Add command "gotoLabel" which is same as "gotolabel". (#169)

### 2.0.3

* Fix ClassNotFoundException when use user-defined WebDriver factory. (#167)

### 2.0.2

* Manage leading and trailing whitespaces in link_handler in both "exact" and "glob" features. (PR #166 by AndreaColoru)

### 2.0.1

* Fix error on constructing xpath expression in link locator by "glob". (PR #165 by AndreaColoru)

### 2.0.0

* Change the supported Java version to 7.0 or later, because Selenium 2.47.1 changed one.
* Catch up Selenium 2.47.1.
* Update dependency versions.
* Refine travis-ci configuration.
* Fix a bug failed in GLOB matching. (issue #163)
* Fix a bug of link locator in case of "a" tag with nested elements. (suggested by AndreaColoru in issue #162)

### 1.11.0

* Refine log format.
* Refine code.
* Update dependency versions. Note: Cannot update netty's version to 3.10.x because unit tests crashed.
* Add command line options to support Chrome-specific capability (PR #160 by uchida):
    * `--chrome-extension` option to specify chrome extension crx files.
    * `--chrome-experimental-option` option to specify path to json file describe various objects in ChromeOptions.
* Catch up Selenium 2.46.0.
* Optimize the handler of `link` locator. (#162)
* Change dependency to PhantomJSDriver (workaround). see https://github.com/detro/ghostdriver/issues/397

### 1.10.0

* Add strict-exit-code option to isolate selenese results. (#157 by uchida)

### 1.9.1

* Catch up Selenium 2.45.0.

### 1.9.0

* Update dependency versions.
* Replace OptionBuilder for thread-safety and avoiding deprecated. (#148)
* Turn WebDriverManager into non-singleton object if use newInstance() method. (#148)
* Remove the following deprecated methods:
    * WebDriverFactory#getEnvironmentVariables()
    * WebDriverManager#getEnvironmentVariables()
* Add the following feature for HTML result: (#143)
    * You can select information of test-case by tabs.
    * Add each command information (start time, duration, sequence number, and result) ordered by time sequence.

### 1.8.4

* Ignore exceptions on taking a screenshot. (#147 by lukian-tabandzhov)

### 1.8.3

* Make the directory for HTML result, XML result, and screenshot if missing.
* Add new option `--no-exit` for Maven. (#146)

### 1.8.2

* Fix an issue wuth CSS locator recorded by the Selenium IDE. Still the IDE successfuly replays the badly recorded CSS selector by fixing it before the actual execution. (#145 by lukian-tabandzhov)

### 1.8.1

* Fix invalid links to each test-case report in "index.html" of html-result.
* Fix error if use command "clickAt" without coordinates parameter. (#144)

### 1.8.0

* Add new option `--config`, it reads option information from configuration file. (#140)
* Clean up the implementation of handling command line options.

### 1.7.0

* Adopt semantic versioning 2.0.0. see: http://semver.org/spec/v2.0.0.html
* Fix failed to load test-case file without Base URL. (#135)
* Add new option `--cli-args`, it adds command line options at starting up driver binary. It affects only firefox, chrome, and phantomjs. (#134)
* Add new option `--command-factory`, it registers user defined command factory. Note: Use "java -cp ...:selenese-runner.jar Main --command-factory ...". Because "java" command ignores all class path settings, when using "-jar" option. (#137)
* Don't create RollupRules until necessary to be able to work with some Java installations that lack Javascript ScriptEngine support. (#138 by koichirok)
* Use firefox options for capabilities if remote browser is "firefox". (#136 by koichirok & vmi)
* Add IntelliJ IDEA's project files to .gitignore. (#139 by koichirok)

### 1.6.0

* [BACKWARD INCOMPATIBLE CHANGE] Change the default test suite name into the same as the test case name when the passed file is not a test suite but is a test case. (PR #133 by patchpump & vmi)
* Catch up Selenium 2.44.0.
* Update dependency versions.

### 1.5.5

* Add start time to HTML result. (PR #132 by patchpump)
* Add system information and command line arguments to HTML/JUnit result.

### 1.5.4

* Missing number.

### 1.5.3

* Catch up Selenium 2.43.1.
* Add hardcoded UTF-8 encoding for HTML result. (PR #130 by patchpump)
* Add screenshot link for "captureEntirePageScreenshot" in HTML result.
* Add screenshot label in HTML result.

### 1.5.2

* Ignore the expiry when comparing old/new cookie information. (issue #125)
* Add links to screenshot image to HTML result.
* Add new commands: "keyDownNative", "keyUpNative", and "keyPressNative". (on Selenium 2.40 or later)
* Add the feature in which "javascript{...}" can be used in any arguments.
* Fix unhandled exception fired while screensshot is executed. (PR #128 by lukian-tabandzhov)
* Add initial width and height for remote driver. (PR #129 by patchpump)
* Add new option `--firefox`, it specifies firefox binary path. (issue #127)

### 1.5.1

* Missing number.

### 1.5.0

* Add loop count information to log message. (issue #113)
* Restart WebDriver if it crashed. (issue #121)
* Use a built-in executable file finder instead of original one.
* Change Internal APIs for issue #113, #121.
    * ICommand, StartLoop, EndLoop, and classes implementing these interfaces.
    * Context, Runner, WebDriverManager, and TestSuite.

### 1.4.7

* Refine exception message.
* Refine exception handling on screenshot.
* Re-fix issue #122.

### 1.4.6

* Catch up Selenium 2.42.2.
* Logging cookie information only when the cookie is added, modified or deleted. (issue #117)
* Add new option `--cookie-filter`, You can filter cookies to log by the regular expression matching the name. (issue #117)
* Normalize filename separator of test-case/test-suite. (issue #122)

### 1.4.5

* Catch up Selenium 2.42.1.

### 1.4.4

* Change the behavior about initial window size: set default size only if driver is htmlunit or phantomjs (i.e. headless drivers). (issue #110)
* Add generating "failsafe-summary.xml" into xml-result directory. (issue #111)
* Fix issues #87, #107, #108, #109, #114, #115.

### 1.4.3

* Add support initial width and height for browsers excluding mobile. (PR #106 by markkimsal, and refined by vmi)
* Fix issue #100.

### 1.4.2

* Catch up Selenium 2.41.0.
* Fix issue #104.

### 1.4.1

* Fix failure on Java8.

### 1.4.0

* Add support "rollup" command and "user-extention-rollup.js". (issue #100)
* Catch up Selenium 2.40.0. (It changed too many internal API, and removed Android driver)
* Remove Andorid driver support because it is removed from Selenium 2.40.
* Re-implement command handling. TOO MANY internal API is changed.
* Update dependency versions.
* Fix issue #99.

### 1.3.6

* Remove default logging command line option for phantomjs from GhostDriver because it depends on user.dir system property.

### 1.3.5

* Fix issue #96.

### 1.3.4

* The option '-D/--define' accepts not only 'key=value' bu 'key+=value'.
* PrintStream object for logging is changed to the instance variable of Runner from static variable.
* Fix issues #92, #93, #94.

### 1.3.3

* Add ${KEY\_\*} variables. (see: http://blog.reallysimplethoughts.com/2013/09/25/using-special-keys-in-selenium-ide-part-2/ )
* Add new option '-D/--define' for setting capabilities of WebDriver.
* Fix issues #87, #90, #91.
* Update Selenium version to 2.39.0.
* Update dependency versions.

### 1.3.2

* Add --set-spped option. It is same as setSpeed(ms) command first.
* Add Appium support. (by yec)
* Fix issues #76, #77, #78, #82, #85.
* Update Selenium version to 2.38.0.

### 1.3.1

* Fix issue #75.

### 1.3.0

* Revive test-suites in test-suites.
* Update HTML result generator for the above feature.

### 1.2.0

* Add HTML result generator.
* Too many internal changes.

### 1.0.0 - 1.1.5

* Sorry, I was not recording changes...

