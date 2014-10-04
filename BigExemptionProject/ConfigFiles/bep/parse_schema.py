from lxml import etree
import os
import json
import argparse

parser = argparse.ArgumentParser(description = 'Produce an ordered list'
        'representation of an XML document')
parser.add_argument('infile', metavar = 'input', 
        type = argparse.FileType('r'),
        help = 'Input file to be parsed')
args = parser.parse_args()

def main():
    doc = etree.parse(args.infile)
    root = doc.getroot()

    version = root.get('version')

    plural = root[0]
    xtl = root[1]

    build_list(root)

def build_list(tree):
    for child in tree.iterchildren():
        if child.tag.endswith('element') and child.get('name'):
            print (get_parent(child), child.get('name'), get_type(child))
        build_list(child)

def get_type(child):
    if not [elem.tag for elem in child.iterchildren()]:
        return 'group'
    else:
        return 'field'

def get_parent(child):
    parent = child.getparent()
    if parent is None:
        return 'root'
    else:
        if parent is not None and parent.tag.endswith('element'):
            return parent.get('name')
        else:
            return get_parent(parent)


def node(name='', fields=[], children=[]):
    return {'title' : name, 'fields' : fields, 'children' : children if children else []}

def add(parent, child):
    parent.get('children').append(child)
    return child

if __name__ == '__main__':
    main()

