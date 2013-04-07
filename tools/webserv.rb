#!/usr/bin/ruby

require 'webrick'
include WEBrick

# src/test/resources/jp/vmi/selenium/selenese/htdocs

htdocs = File.dirname($0) + "/../src/test/resources/jp/vmi/selenium/testutil/htdocs"

Dir.chdir(htdocs)
serv = HTTPServer.new(:Port => 80, :DocumentRoot => '.')
trap("INT") {serv.shutdown}
serv.start
