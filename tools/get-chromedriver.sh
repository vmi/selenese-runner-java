#!/bin/bash

set -eu

cd "$(dirname "$0")/../tmp"

# os=win32, mac32, linux32, linux64
case "$OSTYPE" in
  cygwin)
    os=win32
    ext=.exe
    ;;
  darwin*)
    os=mac32
    ext=
    ;;
  linux*)
    case "$MACHTYPE" in
      x86_64*)
        os=linux64
        ;;
      *)
        os=linux32
        ;;
    esac
    ext=
    ;;
  *)
    echo "Unknown OS ($OSTYPE)"
    exit 1
    ;;
esac

rm -f chromedriver_*.zip
url=http://chromedriver.storage.googleapis.com
version="$(curl $url/LATEST_RELEASE)"
curl -O "$url/$version/chromedriver_$os.zip"
unzip chromedriver_$os.zip
mv -v chromedriver$ext ../drivers
