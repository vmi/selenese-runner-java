#!/usr/bin/ruby
# -*- coding: utf-8 -*-

require 'xmlhash'

xml = File.read('../tmp/iedoc-core.xml')

doc = Xmlhash.parse(xml)
doc['function'].each do |f|
  name = f["name"]
  param = f["param"]
  if param.nil?
    params = ''
  elsif param.is_a? Array
    params = param.map {|e| e["name"]}.join(", ")
  else
    params = param["name"]
  end
  case name
  when /^(assert|get)([A-Z].*)/
    n = $2
    ["assert", "verify", "waitFor"].each do |pfx|
      puts pfx + n
      puts pfx + "Not" + n
    end
    puts "store#{n}"
  when /^is([A-Z].*?)(Present)?$/
    n1 = $1
    n2 = $2
    ["assert", "verify", "waitFor"].each do |pfx|
      puts "#{pfx}#{n1}#{n2}"
      if n2
        puts "#{pfx}#{n1}Not#{n2}"
      else
        puts "#{pfx}Not#{n1}"
      end
    end
    puts "store#{n1}#{n2}"
  else
    puts name
  end
end
