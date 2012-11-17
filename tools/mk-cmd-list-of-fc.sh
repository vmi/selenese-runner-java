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

perl ../tools/mk-cmd-list-of-fc.pl $file | dos2unix | sort > cmd-list-of-fc.txt
