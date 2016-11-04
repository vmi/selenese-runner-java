Selenese Runner Java
====================

This is selenese script interpreter implemented by Java.

It supports test-case and test-suite which are Selenium IDE's native format.

Note: Supported Java version is 7 or later.

[![Build Status](https://travis-ci.org/vmi/selenese-runner-java.svg?branch=master)](https://travis-ci.org/vmi/selenese-runner-java)

Download
--------

You can download the executable jar from:

https://github.com/vmi/selenese-runner-java/releases

Features
--------

* Run test-case and test-suite generated by Selenium IDE from command line.
* Support commands of Selenium IDE: Flow Control.
  https://github.com/davehunt/selenium-ide-flowcontrol
* Log URL/title/Cookies at all commands.
* Take screenshots at all commands. (optional)
* Override base URL.
* Override directory of screenshot path.
* Generate JUnit XML result.
* Generate HTML result.

Usage
-----

    java -jar selenese-runner.jar <option> ... <test-case|test-suite> ...

     --config (-c) <file>                    : load option information from file.
     --driver (-d) <driver>                  : firefox (default) | marionette | chrome | ie | safari | htmlunit | phantomjs | remote | appium | FQCN-of-WebDriverFactory
     --profile (-p) <name>                   : profile name (Firefox only *1)
     --profile-dir (-P) <dir>                : profile directory (Firefox only *1)
     --chrome-experimental-options <file>    : path to json file specify experimental options for chrome (Chrome only *1)
     --chrome-extension <file>               : chrome extension file (multiple, Chrome only *1)
     --proxy <proxy>                         : proxy host and port (HOST:PORT) (excepting IE)
     --proxy-user <user>                     : proxy username (HtmlUnit only *2)
     --proxy-password <password>             : proxy password (HtmlUnit only *2)
     --no-proxy <hosts>                      : no-proxy hosts
     --cli-args <arg>                        : add command line arguments at starting up driver (multiple)
     --remote-url <url>                      : Remote test runner URL (Remote only)
     --remote-platform <platform>            : Desired remote platform (Remote only)
     --remote-browser <browser>              : Desired remote browser (Remote only)
     --remote-version <browser-version>      : Desired remote browser version (Remote only)
     --highlight (-H)                        : highlight locator always.
     --interactive (-i)                      : interactive mode.
     --screenshot-dir (-s) <dir>             : override captureEntirePageScreenshot directory.
     --screenshot-all (-S) <dir>             : take screenshot at all commands to specified directory.
     --screenshot-on-fail <dir>              : take screenshot on fail commands to specified directory.
     --ignore-screenshot-command             : ignore captureEntirePageScreenshot command.
     --baseurl (-b) <baseURL>                : override base URL set in selenese.
     --firefox <path>                        : path to 'firefox' binary. (implies '--driver firefox')
     --marionette <path>                     : path to 'marionette' binary aKa firefox binaries (implies '--driver marionette')
     --chromedriver <path>                   : path to 'chromedriver' binary. (implies '--driver chrome')
     --iedriver <path>                       : path to 'IEDriverServer' binary. (implies '--driver ie')
     --phantomjs <path>                      : path to 'phantomjs' binary. (implies '--driver phantomjs')
     --xml-result <dir>                      : output XML JUnit results to specified directory.
     --html-result <dir>                     : output HTML results to specified directory.
     --timeout (-t) <timeout>                : set timeout (ms) for waiting. (default: 30000 ms)
     --set-speed <speed>                     : same as executing setSpeed(ms) command first.
     --height <height>                       : set initial height. (excluding mobile)
     --width <width>                         : set initial width. (excluding mobile)
     --define (-D) <key=value or key+=value> : define parameters for capabilities. (multiple)
     --rollup <file>                         : define rollup rule by JavaScript. (multiple)
     --cookie-filter <+RE|-RE>               : filter cookies to log by RE matching the name. ("+" is passing, "-" is ignoring)
     --command-factory <FQCN>                : register user defined command factory. (See Note *3)
     --no-exit                               : don't call System.exit at end.
     --strict-exit-code                      : return strict exit code, reflected by selenese command results at end. (See Note *4)
     --max-time <max-time>                   : Maximum time in seconds that you allow the entire operation to take.
     --help (-h)                             : show this message.

    [Note]
    *1 It is available if using "--driver remote --remote-browser firefox".

    *2 If you want to use basic and/or proxy authentication on Firefox, then create new profile, install AutoAuth plugin, configure all settings, access test site with the profile, and specify the profile by --profile option.

    *3 Use "java -cp ...:selenese-runner.jar Main --command-factory ...".
       Because "java" command ignores all class path settings, when using "-jar" option.

    *4 The list of strict exit code is follows:
       - 0: SUCCESS
       - 2: WARNING
       - 3: FAILURE
       - 4: ERROR
       - 5: UNEXECUTED
       - 6: MAX_TIME_EXCEEDED

Requirements
------------

* Java 7 or later.
* Apache Maven 2.x or later to build.

Release Note
------------

The release note is moved to [RELEASENOTE.md](RELEASENOTE.md) file.

Building the Application
------------------------

* Install Apache Maven.
* clone this repository
* run build script
	`mvn -P package`

That will create the *selenese-runner.jar* file within the 'target' directory.

Options
-------

### Configuration file (1.8.0 or later)

You can read option information from the following configuration file by using "--config" option.

You can overwrite the information by additional command line options.

    # configuration file format.

    driver: DRIVER_NAME
    profile: PROFILE_NAME
    profile-dir: /PATH/TO/PROFILE/DIRECTORY
    proxy: PROXY_HOST
    proxy-user: PROXY_USER
    proxy-password: PROXY_PASSWORD
    no-proxy: NO_PROXY_HOSTS
    cli-args: DRIVER_CLI_ARG1
      DRIVER_CLI_ARG2
      DRIVER_CLI_ARG3
    remote-url: http://remote.example.com:4444/wd/hub
    remote-platform: REMOTE_PLATFORM
    remote-browser: REMOTE_BROWSER
    remote-version: REMOTE_VERSION
    # "highlight" parameter is "true" or "false".
    highlight: true
    screenshot-dir: /PATH/TO/SCREENSHOT/DIRECTORY
    screenshot-all: /PATH/TO/SCREENSHOT/DIRECTORY/ALL
    screenshot-on-fail: /PATH/TO/SCREENSHOT/DIRECTORY/ON/FAIL
    # "ignore-screenshot-command" parameter is "true" or "false".
    ignore-screenshot-command: true
    baseurl: http://baseurl.example.com/
    firefox: /PATH/TO/FIREFOX/BINARY
    chromedriver: /PATH/TO/CHROMEDRIVER/BINARY
    iedriver: /PATH/TO/IEDRIVER/BINARY
    phantomjs: /PATH/TO/PHANTOMJS/BINARY
    xml-result: /PATH/TO/XML/RESULT/DIRECTORY
    html-result: /PATH/TO/HTML/RESULT/DIRECTORY
    # The unit of "timeout" parameter is millisecounds.
    timeout: 30000
    # The unit of "set-speed" parameter is millisecounds.
    set-speed: 100
    # The unit of "height" parameter is pixcels.
    height: 1024
    # The unit of "width" parameter is pixcels.
    width: 768
    define: CAPABILITY_KEY1=CAPABILITY_VALUE1
       CAPABILITY_KEY2=CAPABILITY_VALUE2
       CAPABILITY_KEY3+=CAPABILITY_VALUE31
       CAPABILITY_KEY3+=CAPABILITY_VALUE32
       CAPABILITY_KEY3+=CAPABILITY_VALUE33
    rollup: /PATH/TO/ROLLUP/FILE
    cookie-filter: COOKIE_FILTER_REGEXP
    command-factory: full.qualify.class.Name

### Firefox, Chrome and PhantomJS driver

If you want to add command line options to above driver's binary, add following options:

    java -jar selenese-runner.jar --driver DRIVER_NAME \
      --cli-args ARG1 \
      --cli-args ARG2 \
      ...

Example:

* Firefox

        java -jar selenese-runner.jar --driver firefox \
          --cli-args -jsconsole \
          ...

* Chrome

        java -jar selenese-runner.jar --driver chrome \
          --cli-args --incognito \
          --cli-args --ignore-certificate-errors \
          ...

* PhantomJS

        java -jar selenese-runner.jar --driver phantomjs \
          --cli-args --ssl-certificates-path=/PATH/TO/CERTS-DIR/ \
          ...

### Rollup

"--rollup" option and "rollup" command are used for a definition and execution of a user-defined command.

Refer to the following for how to write "rollup" script:

* Using the rollup feature of Selenium
http://sanjitmohanty.wordpress.com/2012/07/06/using-the-rollup-feature-of-selenium/
* Selenium Tutorial : Testing Strategies
https://thenewcircle.com/static/bookshelf/selenium_tutorial/testing_strategies.html

However, this feature has the following limitations:

* supported properties of rollup rule:
** name
** args
** expandedCommands or getExpandedCommans
* cannot access any browser object.

### Cookie filter

You can filter cookies to log by the regular expression matching the name.

Example:

* logging the cookie whose name ends with "ID":

        java -jar selenese-runner.jar --cookie-filter +'ID$' ...

* don't logging the cookie whose name contains "__utm":

        java -jar selenese-runner.jar --cookie-filter -__utm ...

### User defined command factory

You can register user defined command factory:

    java -cp YOUR_CLASS_PATH:selenese-runner.jar Main \
      --command-factory your.command.factory.ClassName ...

Note:

* Use the above command line instead of "java -jar ...".
Because "java" command ignores all class path settings, when using "-jar" option.

* Top-level Main class is contained ONLY in stand-alone "selenese-runner.jar",
and is not contained in "selenese-runner-java-X.Y.Z.jar" in maven repository.
Please use "jp.vmi.selenium.selenese.Main" instead of "Main"
if you want to use this feature with the jar in maven repository.

Original Commands
-----------------

### `include`

Usage: `include` FILENAME

This command include and execute FILENAME test-case.

You can use variables in FILENAME.

See [the test-case example](src/test/resources/selenese/testcase_include.html).

License
-------

The Apache License, Version 2.0.

see "LICENSE" file.
