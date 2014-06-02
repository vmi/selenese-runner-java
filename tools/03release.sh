#!/bin/bash

set -eux

mvn release:perform
url=https://oss.sonatype.org/index.html
if which open > /dev/null 2>&1; then
  open $url
else
  echo "Go to $url"
fi
