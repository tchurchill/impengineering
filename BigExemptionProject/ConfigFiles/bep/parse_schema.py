from lxml import etree
import os
import json
import argparse
import re

parser = argparse.ArgumentParser(description = 'Produce an ordered list'
        'representation of an XML document')
parser.add_argument('infile', metavar = 'input', 
        type = argparse.FileType('r'),
        help = 'Input file to be parsed')
args = parser.parse_args()

json_root = None

def main():
    global json_root
    doc = etree.parse(args.infile)
    root = doc.getroot()

    version = root.get('version')

    plural = root[0].get('name')
    singular = root[1].get('name')

    json_root = node(singular, [], [])
    build_json_tree(root[2], singular)
    print json_root

def build_json_tree(tree, singular):
    global json_root
    for child in tree.iterchildren():
        if child.tag.endswith('element') and child.get('name'):
            if get_type(child) == 'group':
                find(json_root, get_parent(child, singular)).get('children').append(node(child.get('name'), [], []))
            elif get_type(child) == 'field':
                find(json_root, get_parent(child, singular)).get('fields').append(child.get('name'))
            "print (get_parent(child, singular), child.get('name'), get_type(child))"
        build_json_tree(child, singular)

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

def get_parent(child, singular):
    parent = child.getparent()
    if parent is None:
        return singular
    else:
        if parent is not None and parent.tag.endswith('element'):
            return parent.get('name')
        else:
            return get_parent(parent, singular)


def node(name='', fields=[], children=[]):
    return {'title' : name, 'fields' : fields, 'children' : children if children else []}

def add(parent, child):
    parent.get('children').append(child)
    return child

def find(root, node):
    if  root['title'] == node:
        return root;
    elif root['children']:
        for child in root['children']:
            found = find(child, node)
            if found:
                return found
    return None

"""
def find(root, node):
    if  root['title'] is node:
        print 'Returning the found node', root
        return root;
    elif root['children']:
        for child in root['children']:
            print 'searching the child node for ' + node + ' ', child
            found = find(child, node.strip())
            if found:
                print 'FOUND ' + found['title']
                return found
    return None
"""

if __name__ == '__main__':
    main()

