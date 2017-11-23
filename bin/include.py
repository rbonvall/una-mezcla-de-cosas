#!/usr/bin/env python3

from panflute import *
from itertools import takewhile

def include(elem, doc):
    if type(elem) == CodeBlock and 'include' in elem.classes:
        config = parse_include_block(elem.text)
        elem.text = read_content(config)

def parse_include_block(text) -> dict:
    d = {}
    for line in text.splitlines():
        k, v = line.split(':', 1)
        d[k.strip()] = v.strip()
    return d

from itertools import dropwhile
def read_content(config: dict):
    lines = []
    active = 'from' not in config
    with open(config['file']) as f:
        for line in f:
            if 'to' in config and config['to'] in line:
                active = False
            if active:
                lines.append(line[:-1])
            if 'from' in config and config['from'] in line:
                active = True
    min_indent = min(
        len(list(takewhile(lambda c: c == ' ', line)))
        for line in lines if line.strip())
    return '\n'.join(line[min_indent:] for line in lines)

def main(doc=None):
    return run_filter(include, doc=doc)

if __name__ == '__main__':
    main()

