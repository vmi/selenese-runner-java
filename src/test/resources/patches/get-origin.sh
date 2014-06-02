#!/bin/bash

set -eux

if [ "${1:-}" = -f ]; then
  rm -f *.java
fi

### Selenium

javas=(
  com/thoughtworks/selenium/webdriven/WebDriverCommandProcessor.java
  com/thoughtworks/selenium/webdriven/Windows.java
)

for java in "${javas[@]}"; do
  f=${java##*/}
  if [ ! -f "$f" ]; then
    wget https://selenium.googlecode.com/git/java/client/src/$java
  fi
done

### GhostDriver

java=org/openqa/selenium/phantomjs/PhantomJSDriverService.java
f=${java##*/}

if [ ! -f "$f" ]; then
  wget https://raw.github.com/detro/ghostdriver/master/binding/java/src/main/java/$java
fi
