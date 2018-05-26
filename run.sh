#!/bin/sh

set -eu

if [ x"$1" = x--build ]; then
  shift
  mvn -P package
  opt=-jar
  param="target/selenese-runner.jar"
  main=""
else
  dir="$(dirname "$0")/tools"
  opt="-cp"
  param="$("$dir"/classpath.sh)"
  main="jp.vmi.selenium.selenese.Main"
fi

rm -rf tmp/img tmp/xml tmp/html logs/run.log
mkdir -p tmp/img/all tmp/img/failed tmp/xml tmp/html logs

log_file=run-`date +'%Y%m%d_%H%M%S'`.log
( cd logs
  touch $log_file
  ln -s $log_file run.log )

do_script() {
  local file="$1"; shift
  set -x
  case "$OSTYPE" in
    linux*|cygwin*)
      script -c "$*" "$file"
      ;;
    darwin*|*bsd*)
      script "$file" "$@"
      ;;
  esac
}

do_script logs/$log_file java -Dsrj.log.level=DEBUG \
  "$opt" "$param" $main \
  --config tools/run.config "$@"
