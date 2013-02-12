#!/bin/bash

{
sed '/^Usage$/,$d' README.md

echo 'Usage'
echo '-----'
echo ''
COLUMNS=256 java -jar target/selenese-runner.jar --help | perl -ne '
while (<>) {
  s/\s+\z//s;
  if (/^Usage:\s*(.*)$/) {
    print "    $1\n";
    last;
  }
}
while (<>) {
  s/\s+\z//s;
  print "    $_\n";
  last if (/--help/);
}
'
echo ''

echo 'Requirements'
sed '1,/^Requirements$/d' README.md
} > README.md.new

mv -v README.md.new README.md
