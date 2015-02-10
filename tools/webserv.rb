#!/usr/bin/ruby

require 'webrick'
include WEBrick

# src/test/resources/jp/vmi/selenium/selenese/htdocs

htdocs = File.dirname($0) + "/../src/test/resources/htdocs"

Dir.chdir(htdocs)
serv = HTTPServer.new(
  :Port => 80,
  :DocumentRoot => '.',
  :RequestCallback => lambda do |req, res|
    if req.path =~ %r{^/basic/}
      HTTPAuth.basic_auth(req, res, "REALM") do |username, password|
        username == 'USERNAME' && password == 'PASSWORD'
      end
    end
  end
  )
trap("INT") {serv.shutdown}
serv.start
