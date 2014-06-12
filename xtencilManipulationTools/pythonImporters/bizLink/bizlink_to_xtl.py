#Matt Merth
from sys import argv
import sys
import datetime
import re
import importer_util as util
import codecs
import xml.etree.ElementTree as ET
import json
from json_to_xtl import main as _jtx

xs = ''#global variable for the start of every tag name

def main(inputFile):
	bizLinkFileName = inputFile
	#open file
	encoding = util.get_encoding(bizLinkFileName)
	bizLinkFile = codecs.open(bizLinkFileName, "r", encoding)
	tree = ET.parse(bizLinkFileName)
	root = tree.getroot()
	
	#get the value of 'xs' in the schema
	global xs
	xs = re.sub("schema", "", root.tag)#remove the trailing 'schema' from the root tag name
	
	#create the root node
	xtlRoot = util.make_spsfile()
	
	#get schema name (the name attribute of the first tag we need)
	schemaName = root.find("./%sannotation/%sdocumentation" %(xs,xs)).text
	
	#format is 'Schema name: X12_6020_810'. ignore stuff before ':'
	schemaName = schemaName[schemaName.find(": ")+2:]
	
	#get the docType
	doc = root.find("./%selement[@name='%s']/%sannotation/%sdocumentation" %(xs,schemaName,xs,xs))
	
	####Use this as the root for the xtl#####
	xtlDoc = util.add(xtlRoot, util.make_document(doc.text, "FEDS"))
	#########################################
	
	#date format
	util.add(xtlDoc, util.make_format('DATE', 'yyyyMMdd'))
	
	#call recursive function to create nodes
	buildNodeTree(xtlDoc, root.find("./%selement[@name='%s']/%scomplexType" %(xs,schemaName,xs)), root)
	
	#create json file
	with codecs.open("myJson.json", "w") as newJson:
		jsonStr = json.dumps(xtlRoot)
		newJson.write(jsonStr)
	
	#create xtl file
	with codecs.open("myXtl.xtl", "w") as newXtl:
		newXtl.write(_jtx("myJson.json"))

def buildNodeTree(parentNode, childrenGroup, root):
	if childrenGroup != None:
		groupsToSearch = childrenGroup.findall(".//%selement[@ref]" %xs)
		fieldsToMake = childrenGroup.findall(".//%sattribute[@name]" %xs)
		
		#make Field nodes first
		for field in fieldsToMake:
			#find data type of field
			#if field.attrib.has_key('type'):
			#	type = "J" + field.get('type').capitalize()
			#else:
			#	type = "JString"
			
			#get fieldInfo tag  or min/max tags for min/max length
			fieldInfo = field.find(".//%sappinfo/*[1]" %xs)
			
			#get data Type
			if fieldInfo.attrib.has_key('edi_datatype'):
				type = convert_data_type(fieldInfo.get('edi_datatype'))
				#N# =  implied Decimal of #
				precision = re.sub("N([^0])", "\\1", fieldInfo.get('edi_datatype'))
				print precision
			else:
				type = "JString"
				precision = "N0"#this value will be checked for
			
			
			minTag = field.find(".//%sminLength" %xs)
			maxTag = field.find(".//%smaxLength" %xs)
			lengthTag = field.find(".//%slength" %xs)
			if fieldInfo.attrib.has_key('maxLength'):
				maxLen = fieldInfo.get('maxLength')
				minLen = fieldInfo.get('minLength')
			elif minTag != None and maxTag != None:
				maxLen = maxTag.get('value')
				minLen = minTag.get('value')
			elif lengthTag != None:
				maxLen = lengthTag.get('value')
				minLen = '1'
			else:
				maxLen = ''
				minLen = ''
			
			#create field tag with/without implied decimals
			if re.search("[1-9]+", precision):
				fieldNode = util.add(parentNode, util.make_field(field.get('name'), type ,minLen,maxLen, precision))
			else:
				fieldNode = util.add(parentNode, util.make_field(field.get('name'), type ,minLen,maxLen))
			
			#find the help text for each field
			helpText = field.find(".//%sdocumentation" %xs).text
			#create help tag
			util.add(fieldNode, util.make_help(helpText.split("_")[0]))
		
		
		#search the groups for more stuff
		for group in groupsToSearch:
			#make appropriate Node 
			if "minOccurs" and "maxOccurs" in group.attrib:#this is a repeatable group
				if "unbounded" in group.get("maxOccurs"):
					group.set("maxOccurs", "999999")
				curParent = util.add(parentNode, util.make_repGroup(group.get('ref'), group.get('minOccurs'), group.get('maxOccurs'))).get('children')[0]
			elif "minOccurs" in group.attrib:#this is an optional group
				curParent = util.add(parentNode, util.make_group(group.get('ref'), '0', '1'))
			else:#this is a 1 to 1 group
				curParent = util.add(parentNode, util.make_group(group.get('ref'), '1', '1'))
			
			#find this child's children, make recursive call
			buildNodeTree(curParent, root.find("%selement[@name='%s']/%scomplexType" %(xs, group.get('ref'), xs)), root)


def convert_data_type(ediType):
	#R = double, N0 = int, N# =  implied Decimal of #, ID & An = string
	if ediType == "R":
		return "JDouble"
	elif ediType == "AN" or ediType == "ID":
		return "JString"
	elif ediType == "N0":
		return "JInteger"
	else:
		return "JDouble"



#use this check to see if this was called directly
if __name__ == "__main__":
	#check the parameters passed to this script
	if len(argv) < 2:
		print("Usage: python input_file.csv")
	else:
		main(argv[1])