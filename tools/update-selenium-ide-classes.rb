#!/usr/bin/ruby

require 'yaml'

main = File.expand_path("#{__dir__}/../src/main")
ARG_TYPES_JS = "#{main}/resources/selenium-ide/ArgTypes.js"
COMMANDS_JS = "#{main}/resources/selenium-ide/Commands.js"
ARG_TYPES = "#{main}/java/jp/vmi/selenium/runner/model/ArgTypes.java"

class Command

  attr_reader :arg_types, :commands

  class Lines

    def initialize(*args)
      @lines = args.dup
      @state = :js
    end

    def dq(s)
      s.gsub(/\"/, '\"')
    end

    def push(line)
      line.strip!
      case @state
      when :js
        line.gsub!(/\`(.*?)\`/) { '"' + dq($1) + '"' }
        if line.include?("`")
          line.sub!(/\`(.*)$/) { '"' + dq($1) }
          @state = :mlstr
        end
      when :mlstr
        if line.include?("`")
          line.sub!(/(.*?)\`/) { dq($1) + '"' }
          if line.include?("`")
            raise "Unsupported format."
          end
          @state = :js
        else
          line = dq(line)
        end
      end
      @lines.push(line)
    end

    def join
      @lines.join(' ')
    end

  end

  def start_reading(mstr)
    $stderr.puts("* Start reading #{mstr}")
  end

  def end_reading(mstr)
    $stderr.puts("* End reading #{mstr}")
  end

  def read_top_level(list, line)
    # no operation
  end

  def read_commands(list, line)
    next_reader = nil
    case line
    when /^\]\s*$/
      list.push(']')
      end_reading("Commands")
      next_reader = :read_top_level
    else
      list.push(line)
    end
    next_reader
  end

  def read_arg_types_js(parts)
    list = parts[:read_arg_types] = Lines.new
    File.foreach(ARG_TYPES_JS) do |line|
      case line
      when /^export\s+const\s+ArgTypes\s+=\s+\{\s*$/
        start_reading("ArgTypes")
        list.push('{')
      when /^\}\s*$/
        list.push('}')
        end_reading("ArgTypes")
      else
        list.push(line)
      end
    end
  end

  def read_commands_js
    parts = {}
    read_arg_types_js(parts)
    reader = :read_top_level
    File.foreach(COMMANDS_JS) do |line|
      case line
      when /^export\s+const\s+Commands\s+=\s+\[\s*$/
        reader = :read_commands
        parts[reader] = Lines.new('[')
        start_reading("Commands")
      else
        if next_reader = method(reader).call(parts[reader], line)
          reader = next_reader
        end
      end
    end
    parts
  end

  def parse_string(list)
    YAML.load(list.join)
  end

  def load
    parts = read_commands_js
    @arg_types = {}
    parse_string(parts[:read_arg_types]).each do |key, info|
      @arg_types[key] = info
    end
    @commands = {}
    parse_string(parts[:read_commands]).each do |item|
      key, info = *item
      @commands[key] = info
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
  $stderr.puts "* Update #{ARG_TYPES}"
  boa = "// BEGINNING OF ArgTypes"
  eoa = "// END OF ArgTypes"
  lines = {}
  mode = :prologue
  lines[mode] = []
  File.foreach(ARG_TYPES) do |line|
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
  open(ARG_TYPES, 'wb') do |io|
    io.puts lines.values_at(:prologue, :items, :epilogue)
  end
end

cmd = Command.new
cmd.load
update_arg_types(cmd)
