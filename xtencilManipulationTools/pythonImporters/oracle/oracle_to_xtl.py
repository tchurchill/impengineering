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

#Variables
data_file = {}
oracle_document = False
field_type = set(['VARCHAR2', 'DATE', 'NUMBER', 'CHAR', 'VARCHAR'])
trans = {'INO' : '810', 'POI' : '850', 'POAO' : '855', 'DSNO' : '856'}

#Determine the type of Transaction and route the data to the appropriate script
for i in range(10):
    line = args.infile.readline().split()
    if len(line) > 4 and line[0] == 'Transaction' and line[3] == 'Report':
        oracle_document = True        

if not oracle_document:
    sys.exit()

args.infile.seek(0)

for lines in args.infile:
    line = lines.split()
    if len(line) > 5 and line[0] == 'Transaction:':
        transaction_type = line[1]
    if len(line) > 6 and line[4] in field_type:
        data_file[line[1]] = line


json_data = {'name' : 'oracle_data',
        'children' : [{'name' : key, 'data' : value} for key, value in data_file.items()]}

print json_data

p = Popen([sys.executable, trans[transaction_type] + '_importer.py'], stdin=PIPE, stdout=PIPE)
result = json.loads(p.communicate(json.dumps(json_data))[0])

text = xtl(json.dumps(result))
print text
