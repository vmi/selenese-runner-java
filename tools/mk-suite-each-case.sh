#!/bin/bash

dir="$(dirname "$0")"
conf="${0%.*}.conf"

mkdir -pv "$dir/../tmp/suites"

for f in $(comm -2 -3 <(cd "$dir/../src/test/resources/selenese"; ls -1 *.html) $conf); do
    path=../../src/test/resources/selenese/$f
    name=${f%.*}
    echo "- $name"
    cat <<EOF > "$dir/../tmp/suites/suite-$f"
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <meta content="text/html; charset=UTF-8" http-equiv="content-type" />
  <title>Test Suite</title>
</head>
<body>
<table id="suiteTable" cellpadding="1" cellspacing="1" border="1" class="selenium"><tbody>
<tr><td><b>Test Suite</b></td></tr>
<tr><td><a href="$path">$name</a></td></tr>
</tbody></table>
</body>
</html>
EOF
done
