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

url=http://chromedriver.storage.googleapis.com/
wget -O list.xml $url
path="$(xmllint --pretty 1 list.xml | perl -e '
  $os = $ARGV[0];
  $mjv = $mnv = 0;
  while (<STDIN>) {
    if (m@<Key>((\d+)\.(\d+)/chromedriver_$os.zip)</Key>@) {
      if ($mjv < $2) {
        $mjv = $2, $mnv = $3;
      } elsif ($mjv == $2 && $mnv < $3) {
        $mnv = $3;
      }
    }
  }
  print "$mjv.$mnv/chromedriver_$os.zip\n";
' $os)"
rm -f chromedriver_*.zip
wget $url$path

unzip chromedriver_*.zip

mv -v chromedriver$ext ../chromedriver-original$ext
