Selenese Runner Java
====================

This is selenese script interpreter implemented by Java.

It supports test-case and test-suite which are Selenium IDE's native format.

Usage
-----

    java -jar selenese-runner.jar <option> ... <testcase|testsuite> ...
    
     -d,--driver <driver>             firefox (default) | chrome | ie | safari | htmlunit | FQCN-of-WebDriverFactory
     -p,--profile <name>              profile name (Firefox only)
     -P,--profile-dir <dir>           profile directory (Firefox only)
        --proxy <proxy>               proxy host and port (HOST:PORT) (excepting IE)
        --proxy-user <user>           proxy username (HtmlUnit only *)
        --proxy-password <password>   proxy password (HtmlUnit only *)
        --no-proxy <no-proxy>         no-proxy hosts
     -s,--screenshot-dir <dir>        directory for screenshot images. (default: current directory)
     -S,--screenshot-all              take screenshot at all commands.
     -b,--baseurl <baseURL>           override base URL set in selenese.
        --chromedriver <path>         path to 'chromedriver' binary. (implies '--driver chrome')
        --result-dir <dir>            output XML JUnit results to specified directory. (default: no output)
     -h,--help                        show this message.

Requirements
------------

* Java 6 or later.
* Apache Maven 2.x or later.

Building the Application
------------------------

* Install Apache Maven.
* clone this repository
* run build script
	`mvn -P package`

That will create the *selenese-runner.jar* file within the 'target' directory.

License
-------

The Apache License, Version 2.0.

see "LICENSE" file.
