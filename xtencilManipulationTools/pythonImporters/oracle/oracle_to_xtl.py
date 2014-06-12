import argparse
import json
import sys
from json_to_xtl import main as xtl
from subprocess import Popen, PIPE

parser = argparse.ArgumentParser(description = 'Convert an Oracle Transaction'
                                                'file to xtl')
parser.add_argument('infile', metavar = 'input', type = argparse.FileType('r'),
                    help = 'Input file to the Oracle Inporter')
args = parser.parse_args()

#Determine the type of Transaction and route the data to the appropriate script
oracle_document = False

for i in range(10):
    line = args.infile.readline().split()
    if len(line) > 4 and line[0] == 'Transaction' and line[3] == 'Report':
        oracle_document = True        
    if len(line) > 5 and line[0] == 'Transaction:':
        transaction_type = line[1]

if not oracle_document:
    sys.exit()

print transaction_type

p = Popen([sys.executable, '856_importer.py'], stdin=PIPE, stdout=PIPE)
result = json.loads(p.communicate(json.dumps(sys.argv))[0])

text = xtl(json.dumps(result))
print text
