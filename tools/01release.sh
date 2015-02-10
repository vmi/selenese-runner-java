#!/bin/bash

set -eux

mvn clean
mvn release:clean
mvn -P package release:prepare
