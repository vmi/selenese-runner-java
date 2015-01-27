#!/bin/bash

set -eu

new_filename() {
  local name="${1%.*}"
  local ext="${1##*.}"
  local cnt=0
  local filename=
  case "$name" in
    *_[0-9][0-9])
      cnt=${name##*_}
      name=${name%_[0-9][0-9]}
      ;;
    *)
      ;;
  esac
  while true; do
    cnt=$(($cnt + 1))
    filename="$(printf "%s_%02d.%s" $name $cnt $ext)"
    if [ ! -f "$filename" ]; then
      echo "$filename"
      return
    fi
  done
}

clean_log() {
    local file="$1"
    local newfile="$2"
    perl -e '
      $/ = undef;
      $_ = <>;
      s/[ \t]*\e\[K//gs;
      s/[\r\n]+/\n/gs;
      s/^remote:.*$//gm;
      s/^Receiving objects:.*$//gm;
      s/^Resolving deltas:.*$//gm;
      s/^Download.*$//gm;
      s/^(\d+\/\d+\s+K?B\s*)+$//gm;
      s/([ \t]*\n)+/\n/gs;
      s/\ntravis_fold:start:before_install(?:\.\d+)?\n.*?travis_fold:end:before_install(?:\.\d+)?//gs;
      print;
' $file > $newfile
    echo "Clean: $file -> $newfile"
}

for f in "$@"; do
    clean_log "$f" "$(new_filename "$f")"
done
