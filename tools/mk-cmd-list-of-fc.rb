#!/usr/bin/ruby
# -*- coding: utf-8 -*-

ARGF.each_line do |line|
  if /Selenium\.prototype\.(do|get)(\w+)\s+=\s+/ =~ line
    name = $2
    if $1 == 'do'
      name[0] = name[0].downcase
    elsif $1 =='get'
      name = "store" + name
    end
    puts name
  end
end
