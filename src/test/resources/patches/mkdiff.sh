#!/bin/bash

set -eux

java=WebDriverCommandProcessor.java

if [ "${1:-}" = -f ]; then
  rm -f "$java"
fi

if [ ! -f "$java" ]; then
  wget https://selenium.googlecode.com/git/java/client/src/org/openqa/selenium/$java
fi

diff -u $java ../../../main/java/org/openqa/selenium/$java > WDCP.diff
