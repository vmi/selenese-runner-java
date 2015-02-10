#!/bin/bash

set -eux

if [ "${1:-}" = -f ]; then
  rm -f *.java
fi

dir="$(cd "${BASH_SOURCE[0]%/*}"; echo "$PWD")"

### Selenium

javas=(
  com/thoughtworks/selenium/webdriven/WebDriverCommandProcessor.java
  com/thoughtworks/selenium/webdriven/Windows.java
)

selenium_dir="${dir%/selenese-runner-java/*}/selenium"

for java in "${javas[@]}"; do
  f=${java##*/}
  if [ ! -f "$f" ]; then
    cp -v $selenium_dir/java/client/src/$java .
  fi
done

### GhostDriver

java=org/openqa/selenium/phantomjs/PhantomJSDriverService.java
f=${java##*/}

if [ ! -f "$f" ]; then
  wget https://raw.github.com/detro/ghostdriver/master/binding/java/src/main/java/$java
fi
