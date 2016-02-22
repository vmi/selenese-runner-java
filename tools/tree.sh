#!/bin/bash

set -eu

cd "$(dirname "$0")/.."

( set -x
  mvn -DoutputFile=tmp/tree.txt -Dverbose=true dependency:tree )
cat tmp/tree.txt
echo ""
echo "[$(ls -lh tmp/tree.txt)]"
