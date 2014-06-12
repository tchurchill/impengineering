import json
import sys
from json_to_xtl import main as xtl
from subprocess import Popen, PIPE

p = Popen([sys.executable, '856_importer.py'], stdin=PIPE, stdout=PIPE)
result = json.loads(p.communicate(json.dumps(sys.argv))[0])

text = xtl(json.dumps(result))
print text
