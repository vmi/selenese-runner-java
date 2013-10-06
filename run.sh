#!/bin/sh

if [ x"$1" = x--build ]; then
  shift
  mvn -P package
fi

rm -rf tmp/img tmp/xml tmp/html
mkdir -p tmp/img/failed tmp/xml tmp/html

java -jar target/selenese-runner.jar \
  --screenshot-dir tmp/img \
  --screenshot-on-fail tmp/img/failed \
  --xml-result tmp/xml \
  --html-result tmp/html \
  "$@"
