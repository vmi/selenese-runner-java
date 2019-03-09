#!/bin/bash

set -eu

java -cp $(./tools/classpath.sh) \
  jp.vmi.selenium.selenese.utils.CommandDumper --side-commands-info |
  tee docs/side-commands-info.txt 
