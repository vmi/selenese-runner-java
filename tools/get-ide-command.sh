#!/bin/bash

set -eu

base_url="https://raw.githubusercontent.com/SeleniumHQ/selenium-ide/master/packages/selenium-ide-extension/src/neo/models/Command"

cd $(dirname "$0")/../src/main/resources/selenium-ide
for f in ArgTypes.js Commands.js; do
  ( set -x; curl -LO $base_url/$f )
done
