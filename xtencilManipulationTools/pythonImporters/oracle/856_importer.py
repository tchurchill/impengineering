#!/usr/bin/env python
import json
import sys

args = json.load(sys.stdin)

node = {'name': 'GROUPDEF', 'atts': {}, 'children': []}
json.dump(node, sys.stdout)
