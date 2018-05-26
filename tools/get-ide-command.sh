#!/bin/bash

set -eux

cd $(dirname "$0")/../src/main/resources/selenium-ide
curl -LO https://raw.githubusercontent.com/SeleniumHQ/selenium-ide/master/packages/selenium-ide/src/neo/models/Command.js
