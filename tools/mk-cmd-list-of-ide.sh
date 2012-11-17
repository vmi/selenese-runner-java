#!/bin/bash

set -eux

src=http://selenium.googlecode.com/svn/trunk/ide/main/src/content/selenium-core/reference.html
file=$(basename $src)

mkdir -p ../tmp
cd ../tmp

if [ "${1:-}" = "--force" ]; then
  rm -f $file
fi

if [ ! -f $file ]; then
  wget $src
fi

perl ../tools/mk-cmd-list-of-ide.pl $file | dos2unix | sort > cmd-list-of-ide.txt
