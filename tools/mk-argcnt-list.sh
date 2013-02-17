#!/bin/bash

set -eu

tools="$(cd "$(dirname "$0")"; pwd)"

force=false
if [ "${1:-}" = "-f" ]; then
  force=true
fi

# download reference.html of IDE.

cd $tools/../tmp

if [ ! -f reference.html -o "$force" = true ]; then
  rm -f reference.html
  wget http://selenium.googlecode.com/svn/trunk/ide/main/src/content/selenium-core/reference.html
fi

$tools/dump-argcnt.pl reference.html | sort > result.java

diff -u <(grep '^ *m\.put(' ../src/main/java/jp/vmi/selenium/selenese/cmdproc/CustomCommandProcessor.java | sort) result.java

