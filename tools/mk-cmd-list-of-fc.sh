#!/bin/bash

set -eux

src=https://raw.github.com/davehunt/selenium-ide-flowcontrol/master/content/extensions/goto-sel-ide.js
file=$(basename $src)

mkdir -p ../tmp
cd ../tmp

if [ "${1:-}" = "--force" ]; then
  rm -f $file
fi

if [ ! -f $file ]; then
  wget --no-check-certificate $src
fi

ruby ../tools/mk-cmd-list-of-fc.rb $file | dos2unix | sort -df > cmd-list-of-fc.txt
