#!/bin/bash

base_dir="$(cd "$(dirname "$0")/.."; pwd)"

if ! cd "$base_dir/../selenium"; then
  echo "No cloned selenium repository."
  exit 1
fi
set -x
./go //javascript/selenium-atoms:fireEvent
cp -v "build/javascript/selenium-atoms/fireEvent.js" \
      "$base_dir/src/main/resources/jp/vmi/selenium/selenese/javascript"
