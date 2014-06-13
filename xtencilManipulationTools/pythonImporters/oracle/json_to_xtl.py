import sys
import json
from lxml import etree


def json_to_xtl(blob, parent):
    if blob['name'] == 'java':
        element = etree.ProcessingInstruction('java', blob['text'])
        parent.append(element)
    else:
        element = etree.SubElement(parent, blob['name'])
        if 'atts' in blob:
            atts = dict(blob['atts'])
            for key in list(atts):
                element.set(key, unicode(atts[key]))
        if 'text' in blob:
            element.text = str(blob['text'])
    if 'children' in blob:
        for child in list(blob['children']):
            json_to_xtl(child, element)


def main(json_file):
    '''Given a .json file, writes an .xtl file.
       Returns a pointer to created .xtl file.'''
    #with open(json_file, 'r') as myfile:
     #   data = myfile.read().replace('\n', '')


    blob = json.loads(json_file)
    root = etree.Element('root')  # placeholder element
    tree = etree.ElementTree(root)
    json_to_xtl(blob, root)
    betterroot = tree.getroot()[0]
    tree = etree.ElementTree(betterroot)
    return etree.tostring(tree, pretty_print=True)

if __name__ == '__main__':
    if len(sys.argv) != 2:
        print('needs one argument that is a .json file')
        exit(1)
    status = main(sys.argv[1])
    exit(status)
