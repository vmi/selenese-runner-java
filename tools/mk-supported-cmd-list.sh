#!/bin/bash

set -eux

cd ../docs

mk() {
perl ../tools/mk-supported-cmd-list.pl \
  seleniumIDE_with_FC-command-list.txt \
  selenese-runner-command-list.txt \
  $1 > $2
}

mk -s supported-command-list.txt
mk -u unsupported-command-list.txt
