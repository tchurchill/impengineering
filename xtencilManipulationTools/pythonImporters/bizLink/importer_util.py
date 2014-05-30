import datetime
import re
from itertools import chain
import codecs
import sys

######How to use these functions######
#create root node - xtlRoot = make_spsfile()
#adding to the root - xtlDoc = add(xtlRoot, make_document("your name", "FEDS"))
#xtlDoc now points to the node that was added to xtlRoot
#
#this structure creates a json type structure
#to print out the json created from these nodes use this:
#jsonStr = json.dumps(xtlRoot)
#jsonStr is a string of json. Currently, David's code takes in a json file (not just a string)
#so you need to write this string to a file.
#
#	with codecs.open("myJson.json", "w") as newJson:
#		jsonStr = json.dumps(xtlRoot)
#		newJson.write(jsonStr)
#
#


def to_java_name(name):
	name = re.sub("[^a-zA-Z\d]+", "", name)
	name = str.lower(name[:1])+name[1:]
	return name

def to_xtl_name(name):
	name = re.sub("[^a-zA-Z\d]+", "", name)
	return name

def node(tag='', atts={}, children=[], text=''):
	if text == '':
		return {'name': tag, 'atts': atts, 'children': children if children else []}
	else:
		return {'name': tag, 'atts': atts, 'children': children if children else [], 'text': text}

def add(parent, child):
	parent.get('children').append(child)
	return child

def make_group(name, min='0', max='1'):
	return node('GROUPDEF', {'name':to_xtl_name(name), 'javaName':to_java_name(name), 'min': min, 'max': max})

def make_field(name, dataType="JString", min='', max='', ):
	return node('FIELDDEF',{'name':to_xtl_name(name), 'javaName':to_java_name(name), 'minLength':min, 'maxLength':max, 'dataType':dataType})

def make_repGroup(name, min='0', max='999999'):
	return node('GROUPDEF',{'name':to_xtl_name(name), 'javaName':to_java_name(name), 'min':min, 'max':max},\
				[node('GROUPDEF', {'name':to_xtl_name(name), 'javaName': to_java_name(name)+'Rep', 'min':1, 'max':1})])

def make_spsfile():
	date = datetime.datetime.now()
	formatDate = "%s/%s/%s" %(date.month, date.day, date.year)
	return node('SPSFILE', {'name': 'SPS Commerce Xtencil', 'date':formatDate, 'fileType': 'FORM', 'contents': 'NORM'})

def make_document(name, xtlType="XML"):
	return node('DOCUMENTDEF', {'name':to_xtl_name(name), 'javaName':to_java_name(name), 'xtencilType':xtlType, 'encoding':'UTF-8'})

def make_help(text):
	return node('HELP', {}, [], text)

def make_format(type, format):
	return node('FORMAT', {}, [node('FIELDFMT', {'writeFormat': format, 'dataType': type, 'maxLength': '9999', 'minLength': '1', 'keyType': 'all'}, [], format)])

def get_encoding(fileName):
	fileEncodings = chain(["ascii", "utf-8", "utf-16"])
	while True:
		try:
			e = fileEncodings.next();
			file = codecs.open(fileName, "r", e)
			file.read()
			file.close()
			encoding = e
			return e
		except UnicodeDecodeError:
			#print("trying next encoding type")
			continue
		except StopIteration:
			sys.exit("could not read file")