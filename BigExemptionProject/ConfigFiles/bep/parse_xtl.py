#!flask/bin/python
from lxml import etree
import os
import json
import argparse
import re
import json

parser = argparse.ArgumentParser(description = 'Produce an ordered list'
        'representation of an Xtensil')
parser.add_argument('infile', metavar = 'input', 
        type = argparse.FileType('r'),
        help = 'Input file to be parsed')
args = parser.parse_args()

json_root = None
doc = None

def main():
    global json_root
    global doc
    doc = etree.parse(args.infile)
    root = doc.getroot()
    docroot = doc.xpath('/SPSFILE/DOCUMENTDEF')[0]
    
    version = docroot.get('revision')

    json_root = node(docroot.get('name'), doc.getpath(docroot), [], [])
    build_json_tree(docroot)

    cleanup_json(json_root)
    print json.dumps([json_root])

def build_json_tree(tree):
    global json_root
    global doc

    for child in tree.iterchildren():
        if child.tag == 'GROUPDEF' and child.get('name'):
            parent_path = doc.getpath(child.getparent())
            json_parent = find(json_root, parent_path)
            json_parent.get('children').append(node(child.get('name'), doc.getpath(child), [], []))
        elif child.tag  == 'FIELDDEF':
            parent_path = doc.getpath(child.getparent())
            json_parent = find(json_root, parent_path)
            json_parent.get('fields').append(child.get('name'))
        build_json_tree(child)

def cleanup_json(tree):
    tree.pop('path', None)
    if tree['children']:
        for child in tree['children']:
            cleanup_json(child)
    return None

def node(name='', path='', fields=[], children=[]):
    return {'title' : name, 'path' : path, 'fields' : fields, 'children' : children if children else []}

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

