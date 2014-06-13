#!/usr/bin/env python
import json
import sys

data_file = json.load(sys.stdin)
print data_file
node = {'name': 'GROUPDEF', 'atts': {}, 'children': []}
json.dump(node, sys.stdout)
