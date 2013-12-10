#!/usr/bin/python3

#  Copyright (C) 2012 Hai Bison
#
#  See the file LICENSE at the root directory of this project for copying
#  permission.

import argparse
import os
import os.path
import re
import subprocess
import sys

from datetime import date

def indent(s):
    '''Format string `s` with predefined indent.'''
    return '{:>15}'.format(s)
    #.indent()

parser = argparse.ArgumentParser(
    prog=os.path.basename(sys.argv[0]),
    description='''Compress project as a .7z file -- just the source code to
                publish to community, not all the files to backup.''')
parser.add_argument('-s', '--snapshot', action='store_true',
    help='''if set, append to the end of the file name current revision number;
         default is false;''')

ARGS = parser.parse_args()

SRC_DIR = os.path.dirname(os.path.abspath(sys.argv[0]))
print(indent('Backing up') + ': {}'.format(SRC_DIR))

if ARGS.snapshot:
    revision = subprocess.check_output(['hg', 'log', '-l', '1', SRC_DIR])
    revision = re.search(b'(?sm)^changeset:[ \t]+[0-9]+:[0-9a-f]+$', revision)
    if revision:
        revision = re.search(b':[ \t]+[0-9]+:', revision.group()).\
                   group()[1:-1].strip()

# Open Sys.java to get library version name.
FILE_SYS = os.sep.join([
        SRC_DIR, 'code', 'src', 'com', 'haibison', 'android', 'lib', 'anhuu',
        'utils', 'Sys.java'])
with open(FILE_SYS, 'r') as f:
    for line in f:
        line = line.strip()
        if line and re.match(r'(?si)^.+LIB_VERSION_NAME = ".+";', line):
            version = re.search(r'".+"', line).group()[1:-1].replace(' ', '_')
            break

# Now build target file name
filename = os.sep.join([
        os.path.dirname(SRC_DIR),
        'an-huu_v{}_{}src.7z'.format(
            version,
            'r{}_'.format(revision.decode('utf-8'))
                if ARGS.snapshot and revision else '')
        ])
print(indent('To') + ': {}'.format(filename))

# Ignore list
ignore_list = ['.hg', 'ads', 'bugs', 'demo', 'patches', 'resources',
               'screenshots', 'tmp', '.hgignore', '7zcode.py',
               'proguard' + os.sep + 'dump.txt', 'code' + os.sep + 'bin',
               'code' + os.sep + 'gen', 'code' + os.sep + 'local.properties']
ignore_list = ['-xr!*' + os.sep.join([os.path.basename(SRC_DIR), s])
               for s in ignore_list]

CMD_LINE = ['7z', 'a'] + ignore_list + [
    '-t7z', '-m0=LZMA2', '-mmt=on', '-mx9', '-md=64m', '-mfb=64', '-ms=4g',
    '-l', '-mhe=on', '-w', filename, SRC_DIR ]

#print('CMD_LINE = {}'.format(CMD_LINE))

subprocess.call(CMD_LINE)
print(indent('Backed up to') + ': {}'.format(filename))
#os.remove(filename)
