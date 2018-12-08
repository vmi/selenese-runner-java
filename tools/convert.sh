#!/bin/sh

set -eu

dir="$(cd "$(dirname "$0")/.." && pwd)"
if [ x"${1:-}" = x--build ]; then
  shift
  mvn -P package
  opt=-jar
  param="target/selenese-runner.jar"
  main=""
else
  opt="-cp"
  param="$("$dir"/tools/classpath.sh)"
  main="jp.vmi.selenium.selenese.Main"
fi

log_file=conv-`date +'%Y%m%d_%H%M%S'`.log
( cd "$dir/logs"
  touch $log_file
  rm -f conv.log
  ln -s $log_file conv.log )

do_script() {
  local file="$1"; shift
  #set -x
  case "$OSTYPE" in
    linux*|cygwin*)
      script -c "$*" "$file"
      ;;
    darwin*|*bsd*)
      script "$file" "$@"
      ;;
  esac
}

do_script "$dir/logs/$log_file" java -Dsrj.log.level=DEBUG \
  "$opt" "$param" $main convert "$@"
