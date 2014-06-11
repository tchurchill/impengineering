import json
import sys
from json_to_xtl import main as xtl
from subprocess import Popen, PIPE

p = Popen([sys.executable, '856_importer.py'], stdin=PIPE, stdout=PIPE)
result = json.loads(p.communicate(json.dumps(sys.argv))[0])
with open('output.json', 'w') as output:
    output.write(json.dumps(result))

text = xtl('output.json')
print text
