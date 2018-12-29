#!/bin/bash

set -eu

help() {
  echo "Usage: $0 PORT [USER PASSWORD]"
  exit 1
}

if [ $# = 0 ]; then
  help
fi

cd $(dirname "$0")/..

port="$1"; shift
if [ "$OSTYPE" != "cygwin" -a "$port" -lt 1024 ]; then
  sudo=sudo
else
  sudo=""
fi

$sudo java -cp "$(./tools/classpath.sh)" \
      -Dlogback.configurationFile=src/shade/resources/logback.xml \
      -Dsrj.log.level=DEBUG \
      jp.vmi.selenium.testutils.ProxyServer $port "$@"
