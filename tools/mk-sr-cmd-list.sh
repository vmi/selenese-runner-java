#!/bin/bash

set -eu

cd $(dirname "$0")/..
cp=$(./tools/classpath.sh)

java -cp "$cp" jp.vmi.selenium.selenese.utils.CommandDumper | sed 's/,.*//' | dos2unix > docs/selenese-runner-command-list.txt
