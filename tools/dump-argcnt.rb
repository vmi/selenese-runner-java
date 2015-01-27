#!/usr/bin/ruby

require 'xmlhash'

xml = File.read('../tmp/iedoc-core.xml')

doc = Xmlhash.parse(xml)
r = doc['function'].map do |f|
  name = f["name"]
  param = f["param"]
  locs = []
  if param.nil?
    cnt = 0
  elsif param.is_a? Array
    cnt = param.length
    param.each_with_index do |e, i|
      if /locator/i =~ e["name"]
        locs.push(i)
      end
    end
  else
    cnt = 1
    if /locator/i =~ param["name"]
      locs.push(0)
    end
  end
  "        m.put(" + (["\"#{name}\"", cnt] + locs).join(", ") + ");\n"
end

print r.sort.join
