#!/bin/bash

set -eux

cd "$(dirname "$0")/.."

mvn -P package
java -cp target/selenese-runner.jar \
     jp.vmi.selenium.selenese.utils.CommandDumper | tee tmp/commands.csv
