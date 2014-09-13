Selenese Runner Java Relase Note
================================

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
* Add new option "--firefox", it specifies firefox binary path. (issue #127)

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
* Add new option "--cookie-filter", You can filter cookies to log by the regular expression matching the name. (issue #117)
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

