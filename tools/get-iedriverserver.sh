#!/bin/bash

set -eu

# [IE]
# http://selenium-release.storage.googleapis.com/index.html
# http://selenium-release.storage.googleapis.com/index.html?path=2.46/
#   IEDriverServer_Win32_2.46.0.zip
#   IEDriverServer_x64_2.46.0.zip

cd "$(dirname "$0")/../tmp"

base_url=http://selenium-release.storage.googleapis.com
index=index.xml

if [ "${1:-}" = "-f" ]; then
  rm -f $index
fi

if [ ! -f $index ]; then
  curl -o $index $base_url/
fi

files=$(sed -E $'s@<[^<>]+>|[^<>]+@&\\\n@g' $index \
  | awk '
/\/IEDriverServer_/ {
  ver = $0 - 0
  if (latest_ver < ver)
    latest_ver = ver
  if (latest_ver == ver) {
    if (match($0, /Win32/))
      win32 = $0
    else
      x64 = $0
  }
}
END {
  print win32
  print x64
}')

for f in $files; do
  n="${f##*/}"
  if [ ! -f "$n" ]; then
    curl -O $base_url/$f
  fi
  unzip $n -d ../drivers
  case "$n" in
    *_Win32_*)
      ( cd ../drivers; mv -fv IEDriverServer.exe IEDriverServer32.exe )
    ;;
    *_x64_*)
      ( cd ../drivers; mv -fv IEDriverServer.exe IEDriverServer64.exe )
    ;;
  esac
done
