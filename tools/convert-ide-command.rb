#!/usr/bin/ruby

require 'yaml'

main = File.expand_path("#{__dir__}/../src/main")
COMMAND_JS = "#{main}/resources/selenium-ide/Command.js"
ARG_TYPE = "#{main}/java/jp/vmi/selenium/runner/model/ArgType.java"

class Command

  attr_reader :arg_types, :command_list

  def start_reading(mstr)
    $stderr.puts("* Start reading #{mstr}")
  end

  def end_reading(mstr)
    $stderr.puts("* End reading #{mstr}")
  end

  def push_line(list, line)
    line.strip!
    if list[-1].end_with?('\\')
      list[-1].sub!(/\\$/, line)
    else
      list.push(line)
    end
  end

  def read_top_level(list, line)
    # no operation
  end

  def read_arg_types(list, line)
    next_reader = nil
    if /^\}\s*$/ =~ line
      list.push('}')
      end_reading("ArgTypes")
      next_reader = :read_top_level
    else
      push_line(list, line)
    end
    next_reader
  end

  def read_command_list(list, line)
    next_reader = nil
    if list.empty?
      case line
      when /^\s*list\s+=\s+new\s+Map\(\[\s*$/
        list.push('[')
      else
        # skip
      end
    else
      case line
      when /^\s*\]\)\s*$/
        list.push(']')
        end_reading("CommandList")
        next_reader = :read_top_level
      else
        push_line(list, line)
      end
    end
    next_reader
  end

  def read_command_js
    parts = {}
    reader = :read_top_level
    File.foreach(COMMAND_JS) do |line|
      case line
      when /^export\s+const\s+ArgTypes\s+=\s+\{\s*$/
        reader = :read_arg_types
        parts[reader] = ['{']
        start_reading("ArgTypes")
      when /^class\s+CommandList\s*\{\s*/
        reader = :read_command_list
        parts[reader] = []
        start_reading("CommandList")
      else
        if next_reader = method(reader).call(parts[reader], line)
          reader = next_reader
        end
      end
    end
    parts
  end

  def parse_string(list)
    YAML.load(list.join(' '))
  end

  def load
    parts = read_command_js
    @arg_types = {}
    parse_string(parts[:read_arg_types]).each do |key, info|
      @arg_types[key] = info
    end
    @command_list = {}
    parse_string(parts[:read_command_list]).each do |item|
      key, info = *item
      @command_list[key] = info
    end
  end
end

def to_const(s)
  s.gsub(/[A-Z]/, '_\&').upcase
end

def quote(s)
  s.gsub(/\\/, '\\\\').gsub(/"/, '\"')
end

def update_arg_types(cmd)
  $stderr.puts "* Update #{ARG_TYPE}"
  boa = "// BEGINNING OF ArgTypes"
  eoa = "// END OF ArgTypes"
  lines = {}
  mode = :prologue
  lines[mode] = []
  File.foreach(ARG_TYPE) do |line|
    case line
    when /#{boa}/
      lines[mode].push(line, '')
      mode = :items
      lines[mode] = []
    when /#{eoa}/
      mode = :epilogue
      lines[mode] = [line]
    else
      lines[mode].push(line)
    end
  end
  items = lines[:items] = []
  cmd.arg_types.each do |key, info|
    name = info["name"]
    desc = info["description"] || info["value"]
    items.push(<<-EOF)
    /** #{name} */
    #{to_const(key)}("#{quote(key)}", "#{quote(name)}",
        "#{quote(desc)}"),

    EOF
  end
  open(ARG_TYPE, 'wb') do |io|
    io.puts lines.values_at(:prologue, :items, :epilogue)
  end
end

cmd = Command.new
cmd.load
update_arg_types(cmd)
