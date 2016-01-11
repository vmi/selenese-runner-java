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

def esc_js(s)
  s.to_s.gsub(/[\x00-\x1f\x7f\\'"]/) do
    '\\x%02x' % $&.ord
  end
end

class PostHandler < HTTPServlet::DefaultFileHandler
  def do_POST(req, res)
    if req.addr[2] != 'localhost'
      raise HTTPStatus::Forbidden
    end
    q = req.query
    body = IO.read(@local_path)
    body.gsub!(/\$\{(\w+)(:\w+)?\}/) do
      case $2
      when nil, 'h', 'html'
        esc_html(q[$1])
      when 'j', 'js'
        esc_js(q[$1])
      when 'r', 'raw'
        q[$1]
      end
    end
    body.sub!(/([ \t]*)<!-- QUERY -->(?:[ \t]*\n?)/m) do
      indent = $1
      form = %{#{indent}<form id="QUERY">\n}
      q.each do |k, v|
        form += %{#{indent}  <input type="hidden" name="#{esc_html(k)}" value="#{esc_html(v)}">\n}
      end
      form + %{#{indent}</form>\n}
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
