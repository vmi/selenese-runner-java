#!/usr/bin/ruby

# for proxy server test

require 'webrick'
require 'webrick/httpproxy'
include WEBrick

opts = { :Port => 18080 }
if ARGV.length == 2
  proxy_user, proxy_pass = *ARGV
  opts[:ProxyAuthProc] = Proc.new {|req, res|
    HTTPAuth.proxy_basic_auth(req, res, 'proxy') {|user, pass|
      user == proxy_user and pass == proxy_pass
    }
  }
end
s = WEBrick::HTTPProxyServer.new(opts)
trap("INT") { s.shutdown }
s.start
