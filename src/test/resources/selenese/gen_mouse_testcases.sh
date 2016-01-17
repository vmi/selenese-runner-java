#!/bin/bash

set -eux

for i in {1..5}; do
  perl -pe "s/\\\$\\{i\\}/$i/g" testcase_mouse_tmpl.html > testcase_mouse$i.html
done
