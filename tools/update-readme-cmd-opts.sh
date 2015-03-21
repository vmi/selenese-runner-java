#!/bin/bash

jar="target/selenese-runner.jar"

if [ ! -f "$jar" ]; then
  mvn -P package
fi

case "$OSTYPE" in
  cygwin)
    ps=';'
    ;;
  *)
    ps=':'
    ;;
esac

(
sed '/^Usage$/,$d' README.md

echo 'Usage'
echo '-----'
echo ''
COLUMNS=256 java -cp "target/classes$ps$jar" Main --help | perl -ne '
while (<>) {
  s/\s+\z//s;
  if (/^Usage:\s*(.*)$/) {
    print "    $1\n";
    last;
  }
}
while (<>) {
  s/\s+\z//s;
  last if (/\[INFO\]\s+Exit\s+code:/);
  print "    $_\n";
}
'
echo ''

echo 'Requirements'
sed '1,/^Requirements$/d' README.md
) > README.md.new

diff -u README.md README.md.new

mv -v README.md.new README.md
