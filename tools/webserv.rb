#!/usr/bin/ruby

require 'webrick'
include WEBrick

# src/test/resources/jp/vmi/selenium/selenese/htdocs

htdocs = File.dirname($0) + '/../src/test/resources/htdocs'

Dir.chdir(htdocs)
$serv = HTTPServer.new(
  :Port => 80,
  :DocumentRoot => '.',
  :RequestCallback => lambda do |req, res|
    if req.path =~ %r{^/basic/}
      HTTPAuth.basic_auth(req, res, 'REALM') do |username, password|
        username == 'USERNAME' && password == 'PASSWORD'
      end
    end
  end
)

def esc_html(s)
  s.to_s.gsub(/[<>&\"]/) do
    case $&
    when '<'; '&lt;'
    when '>'; '&gt;'
    when '&'; '&amp;'
    when '"'; '&quot;'
    end
  end
end

class PostHandler < HTTPServlet::DefaultFileHandler
  def do_POST(req, res)
    if req.addr[2] != 'localhost'
      raise HTTPStatus::Forbidden
    end
    q = req.query
    body = IO.read(@local_path)
    body.gsub!(/\$\{(\w+)\}/) do
        esc_html(q[$1])
    end
    mtype = HTTPUtils::mime_type(@local_path, @config[:MimeTypes])
    res['content-type'] = mtype
    res['content-length'] = body.size
    res.body = body
  end
end
HTTPServlet::FileHandler.add_handler('html', PostHandler)
trap('INT') {$serv.shutdown}
$serv.start
