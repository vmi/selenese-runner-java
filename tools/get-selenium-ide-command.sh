#!/bin/bash

set -eu

version="v3.17.0"

base_url="https://raw.githubusercontent.com/SeleniumHQ/selenium-ide/$version/packages/selenium-ide/src/neo/models/Command"

cd $(dirname "$0")/../src/main/resources/selenium-ide
for f in ArgTypes.js Commands.js; do
  ( set -x; curl -LO $base_url/$f )
done
