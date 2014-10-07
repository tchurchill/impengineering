#!flask/bin/python
from lxml import etree
import os
import json
import argparse
import re
import json

parser = argparse.ArgumentParser(description = 'Produce an ordered list'
        'representation of an XML document')
parser.add_argument('infile', metavar = 'input', 
        type = argparse.FileType('r'),
        help = 'Input file to be parsed')
args = parser.parse_args()

doc = None
root = None
json_root = None

def main():
    global doc
    global root
    global json_root
    doc = etree.parse(args.infile)
    root = doc.getroot()

    version = root.get('version')

    plural = root[0].get('name')
    singular = root[1].get('name')

    json_root = {'title' : singular, 'path' : doc.getpath(root[2]), 
            'version' : version, 'fields' : [], 'children' : []}
    build_json_tree(root[2])

    print json.dumps([json_root])

def build_json_tree(tree):
    global doc
    global json_root
    for child in tree.iterchildren():
        if child.tag.endswith('element') and child.get('name'):
            parent = get_parent(child)
            json_parent = find(json_root, doc.getpath(parent))
            if json_parent is None:
                print 'WARNING: json_parent is Null and child is ' , child.get('name')
            if get_type(child) == 'group':
                json_parent.get('children').append(node(child.get('name'), doc.getpath(child), [], []))
            elif get_type(child) == 'field':
                json_parent.get('fields').append(child.get('name'))
        build_json_tree(child)

def get_type(child):
    tags = [elem.tag for elem in child.iterchildren()]
    tags = [re.sub("^{.*?}", "", x) for x in tags]
    if not tags:
        return 'field'
    elif 'complexType' in tags:
        return 'group'
    elif 'simpleType' in tags:
        return 'field'
    elif 'annotation' in tags and len(tags) == 1:
        return 'field'
    else:
        return None

def get_parent(child):
    global root
    parent = child.getparent()
    if parent is None:
        return root[2]
    elif parent is not None and parent.tag.endswith('element'):
        return parent
    else:
        return get_parent(parent)


def node(name='', path='', fields=[], children=[]):
    return {'title' : name, 'path' : path, 'fields' : fields, 'children' : children if children else []}

def add(parent, child):
    parent.get('children').append(child)
    return child

def find(root, path):
    if  root['path'] == path:
        return root;
    elif root['children']:
        for child in root['children']:
            found = find(child, path)
            if found:
                return found
    return None

if __name__ == '__main__':
    main()

