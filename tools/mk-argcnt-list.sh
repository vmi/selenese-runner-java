#!/bin/bash

set -eu

tools="$(cd "$(dirname "$0")"; pwd)"

force=false
if [ "${1:-}" = "-f" ]; then
  force=true
fi

# download iedoc-core.xml of IDE.

cd $tools/../tmp

if [ ! -f iedoc-core.xml -o "$force" = true ]; then
  rm -f iedoc-core.xml
  wget http://selenium.googlecode.com/svn/trunk/ide/main/src/content/selenium-core/iedoc-core.xml
fi

$tools/dump-argcnt.rb > result.java

diff -u <(grep '^ *m\.put(' ../src/main/java/jp/vmi/selenium/selenese/cmdproc/CustomCommandProcessor.java | sort) result.java
