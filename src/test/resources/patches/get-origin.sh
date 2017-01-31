#!/bin/bash

set -eux

if [ "${1:-}" = -f ]; then
  rm -f *.java
fi

dir="$(cd "${BASH_SOURCE[0]%/*}"; echo "$PWD")"

### GhostDriver

java=org/openqa/selenium/phantomjs/PhantomJSDriverService.java
f=${java##*/}

ghostdriver_path=codeborne/ghostdriver
if [ ! -f "$f" ]; then
  curl -LO https://raw.github.com/$ghostdriver_path/master/binding/java/src/main/java/$java
fi
