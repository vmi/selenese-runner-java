#!/bin/bash

set -eux

./mk-cmd-list-of-ide.sh
./mk-cmd-list-of-fc.sh

sort -df ../tmp/cmd-list-of-*.txt > ../docs/seleniumIDE_with_FC-command-list.txt
