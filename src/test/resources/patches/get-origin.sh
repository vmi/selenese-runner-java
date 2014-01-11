#!/bin/bash

set -eux

pkg=com.thoughtworks.selenium.webdriven
java=WebDriverCommandProcessor.java

if [ "${1:-}" = -f ]; then
  rm -f "$java"
fi

if [ ! -f "$java" ]; then
  wget https://selenium.googlecode.com/git/java/client/src/${pkg//./\/}/$java
fi
