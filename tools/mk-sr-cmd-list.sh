#!/bin/bash

set -eux

cd ..
mvn -P package

java -cp target/selenese-runner.jar jp.vmi.selenium.selenese.utils.CommandList | dos2unix > docs/selenese-runner-command-list.txt
