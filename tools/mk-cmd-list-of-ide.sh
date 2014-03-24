#!/bin/bash

set -eux

src=http://selenium.googlecode.com/svn/trunk/ide/main/src/content/selenium-core/iedoc-core.xml
file=$(basename $src)

mkdir -p ../tmp
cd ../tmp

if [ "${1:-}" = "--force" ]; then
  rm -f $file
fi

if [ ! -f $file ]; then
  wget $src
fi

ruby ../tools/mk-cmd-list-of-ide.rb $file | dos2unix | sort -df > cmd-list-of-ide.txt
