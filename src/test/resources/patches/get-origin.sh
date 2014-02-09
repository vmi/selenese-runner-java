#!/bin/bash

set -eux

if [ "${1:-}" = -f ]; then
  rm -f *.java
fi

### Selenium

pkg=com.thoughtworks.selenium.webdriven
java=WebDriverCommandProcessor.java

if [ ! -f "$java" ]; then
  wget https://selenium.googlecode.com/git/java/client/src/${pkg//./\/}/$java
fi

### GhostDriver

pkg=org.openqa.selenium.phantomjs
java=PhantomJSDriverService.java

if [ ! -f "$java" ]; then
  wget https://raw.github.com/detro/ghostdriver/master/binding/java/src/main/java/${pkg//./\/}/$java
fi
