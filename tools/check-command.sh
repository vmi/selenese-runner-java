#!/bin/bash

dir="src/main/java/jp/vmi/selenium/selenese/command"

cd "$dir"

diff -U 0 \
     <(egrep -l "extends (AbstractCommand|[A-Za-z0-9]+LoopImpl)" *.java | sed 's/\.java$//' | egrep -v '^Assertion$|^BuiltInCommand$|^Store$|LoopImpl' | sort) \
     <(sed -En 's/^[ \t]*addConstructor\(([A-Za-z0-9]+)\.class.*/\1/p' CommandFactory.java | sort) \
     | fgrep -v '@@'
