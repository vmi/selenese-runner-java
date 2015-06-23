#!/bin/bash

set -eu

# [Safari]
# http://selenium-release.storage.googleapis.com/index.html?path=2.45/
#   SafariDriver.safariextz

cd "$(dirname "$0")/../tmp"

base_url=http://selenium-release.storage.googleapis.com
index=index.xml

if [ "${1:-}" = "-f" ]; then
  rm -f $index
fi

if [ ! -f $index ]; then
  curl -o $index $base_url/
fi

file=$(sed -E $'s@<[^<>]+>|[^<>]+@&\\\n@g' $index \
  | awk '
/\/SafariDriver\.safariextz/ {
  ver = $0 - 0
  if (latest_ver < ver) {
    latest_ver = ver
    file = $0
  }
}
END {
  print file
}')

name="${file##*/}"
cd ../drivers
if [ ! -f "$name" ]; then
  curl -O $base_url/$file
fi

echo "$file"
