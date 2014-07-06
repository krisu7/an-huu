#!/usr/bin/env ruby

##
# Copyright (C) 2012 Hai Bison
#
# See the file LICENSE at the root directory of this project for copying
# permission.
#

require 'optparse'
require 'ostruct'

DESCRIPTION = "
Compress project as a .7z file -- just the source code to publish to community,
not all the files to backup.
"

##
# Prints message `msg`. If `title` is not `nil`, prints it first with predefined
# indent.
#
def say(msg, title=nil)
    if title
        puts "%15s: #{msg}" % title
    else
        puts msg
    end
end # say

##
# Parses arguments.
#
def parse_args(args=ARGV.clone)
    # Default options
    options = OpenStruct.new
    options.snapshot = nil

    # Now parse args
    OptionParser.new do |opts|
        opts.banner = DESCRIPTION
        opts.separator ''
        opts.separator 'Specific options:'

        opts.on('-s', '--snapshot',
                'appends to the end of the file name current revision number;',
                'default is false') do
            options.snapshot = true
        end

        opts.on('-h', '--help', 'prints help message') do
            puts opts
            exit
        end
    end.parse!

    options
end # parse_args

if __FILE__ == $0
    args = parse_args

    SRC_DIR = File.dirname(File.absolute_path($0))
    say SRC_DIR, 'Backing up'

    revision = nil
    if args.snapshot
        revision = `hg log -l 1 "#{SRC_DIR}"`
        if revision = revision.match(/^changeset:[ \t]+[0-9]+:[0-9a-f]+$/)
            revision = revision[0].match(/:[ \t]+[0-9]+:/)[0][1..-2].strip
        end
    end

    # Open Sys.java to get library version name.
    File_sys = File.join(SRC_DIR, 'code', 'src', 'com', 'haibison', 'android',
                         'anhuu', 'utils', 'Sys.java')
    version = ''
    File.open(File_sys, 'r').each_line do |line|
        if line = line.strip.match(/^.+LIB_VERSION_NAME = ".+";/)
            version = line[0].match(/".+"/)[0][1..-2].gsub(' ', '_')
            break
        end
    end

    # Now build target file name
    filename = File.join(
        File.dirname(SRC_DIR),
        'an-huu_v%s_%ssrc.7z' % [
            version,
            (args.snapshot and revision) ? 'r%s_' % revision : ''])
    say filename, 'To'

    # Ignore list
    ignore_list = [
        '.hg', 'ads', 'bugs', 'demo', 'patches', 'resources',
        'screenshots', 'tmp', '.hgignore', '7zcode.rb',
        File.join('proguard', 'dump.txt'), File.join('code', 'bin'),
        File.join('code', 'gen'), File.join('code', 'local.properties')
    ]
    ignore_list.map!{ |s| '-xr!*' + File.join(File.basename(SRC_DIR), s)}

    cmd = ['7z', 'a'] + ignore_list + [
        '-t7z', '-m0=LZMA2', '-mmt=on', '-mx9', '-md=64m', '-mfb=64', '-ms=4g',
        '-l', '-mhe=on', '-w', filename, SRC_DIR ]

    system(*cmd)

    puts

    if File.file? filename
        say filename, 'Backed up to'
        #File.delete(filename)
    else
        say 'expected target file not existed', '! Error'
        exit 1
    end
end # if __FILE__ == $0
