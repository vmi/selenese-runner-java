#!/bin/bash

base_dir="$(cd "$(dirname "$0")/.."; pwd)"
cv="./tmp/classver"

cd "$base_dir"
if [ ! -f "$cv" ]; then
  curl -Lo "$cv" https://github.com/vmi/classver/raw/master/classver
  chmod +x "$cv"
fi
mvn -Dmdep.outputFile=tmp/classpath dependency:build-classpath
$cv $(cat tmp/classpath) | tee tmp/classver.list
sort tmp/classver.list
