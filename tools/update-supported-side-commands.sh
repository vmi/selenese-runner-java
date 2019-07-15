#!/bin/bash

set -eu

dir="$(dirname "$0")"
cd "$dir/.."

java -cp $(./tools/classpath.sh) \
  jp.vmi.selenium.selenese.utils.CommandDumper --side |
  fgrep -v '[main]' | tee docs/supported-side-commands.txt 
