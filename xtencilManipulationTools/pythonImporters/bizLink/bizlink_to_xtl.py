#Matt Merth
from sys import argv
import sys
import datetime
import re
import importer_util as util
import codecs
#import xml.etree.ElementTree as ET
from lxml import etree
import json
from json_to_xtl import main as _jtx

xs = ''#global variable for the start of every tag name

def main(inputFile):
	bizLinkFileName = inputFile
	#print inputFile
	#open file
	#encoding = util.get_encoding(bizLinkFileName)
	#bizLinkFile = codecs.open(bizLinkFileName, "r", encoding)
	root = etree.fromstring(bizLinkFileName)
	#root = tree.getroot()
	
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
	buildNodeTree(xtlDoc, root.find("./%selement[@name='%s']/%scomplexType" %(xs,schemaName,xs)), root, 0)
	
	#create json file
	#with codecs.open("myJson.json", "w") as newJson:
	jsonStr = json.dumps(xtlRoot)
	return jsonStr
	#	newJson.write(jsonStr)
	
	#create xtl file
	#with codecs.open("myXtl.xtl", "w") as newXtl:
	#	newXtl.write(_jtx("myJson.json"))

def buildNodeTree(parentNode, childrenGroup, root, parentPos):
	#childrenGroup = complexType tag
		#sequenceType = composite groups
		#attribute (tag) = element
		
	if childrenGroup != None:
		#to get correct order, get all record/field info seqNums and sort in list
		infoGroups = childrenGroup.findall(".//%sappinfo/*[1]" %xs)
		orderList = []
		for infoGroup in infoGroups:
			if infoGroup.attrib.has_key('sequence_number'):
				orderList.append(infoGroup.get('sequence_number'))
		
		#assign correct list to loop through
		if len(orderList) > 0:
			orderList.sort()
			orderListOrInfoGroups = orderList
		else:
			orderListOrInfoGroups = infoGroups
		
		for indexOrChild in orderListOrInfoGroups:
			if type(indexOrChild) is str:
				#get the great grand parent of field/record info with correct seqNum
				child = childrenGroup.find(".//%sappinfo/*[1][@sequence_number='%s']/../../.." %(xs,indexOrChild))
			else:
				child = indexOrChild.getparent().getparent().getparent()
			
			if child.attrib.has_key('ref'):#group
				group = child
				#different names for groups with names that start with 'C'
				if group.get('ref')[0] == "C" and type(indexOrChild) is str:
					groupName = parentNode.get('atts').get('name') + indexOrChild
					myIndex = indexOrChild
				else:
					groupName = group.get('ref') + "Group"
					myIndex = 0
				
				#make appropriate Node 
				if "minOccurs" and "maxOccurs" in group.attrib:#this is a repeatable group
					if "unbounded" in group.get("maxOccurs"):
						group.set("maxOccurs", "999999")
					curParent = util.add(parentNode, util.make_repGroup(groupName, group.get('minOccurs'), group.get('maxOccurs'))).get('children')[0]
				elif "minOccurs" in group.attrib:#this is an optional group
					curParent = util.add(parentNode, util.make_group(groupName, '0', '1'))
				else:#this is a 1 to 1 group
					curParent = util.add(parentNode, util.make_group(groupName, '1', '1'))
				
				#find this child's children, make recursive call
				buildNodeTree(curParent, root.find("%selement[@name='%s']/%scomplexType" %(xs, group.get('ref'), xs)), root, myIndex)
			else:#field
				field = child
				
				fieldName =  field.get('name')
				#rename field
				if field.get('name')[0] == 'C' and type(indexOrChild) is str:
					fieldName = parentNode.get('atts').get('name')[:3] + "C0" + indexOrChild
				
				#get fieldInfo tag  or min/max tags for min/max length
				fieldInfo = field.find(".//%sappinfo/*[1]" %xs)
				
				#get data Type
				if fieldInfo.attrib.has_key('edi_datatype'):
					edi_type = convert_data_type(fieldInfo.get('edi_datatype'))
					#N# =  implied Decimal of #
					precision = re.sub("N([^0])", "\\1", fieldInfo.get('edi_datatype'))
				else:
					edi_type = "JString"
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
				
				#get EDI info and rename groups to good names
				#create field tag with/without implied decimals
				if re.search("[1-9]+", precision):
					fieldNode = util.add(parentNode, util.make_field(fieldName, edi_type ,minLen,maxLen, precision))
				else:
					fieldNode = util.add(parentNode, util.make_field(fieldName, edi_type ,minLen,maxLen))
				
				#getElement ID
				elementID = fieldInfo.get('ReferenceDesignator')
				
				#create dict to add to node (edi info)
				#check to see if subPosition needs to be populated, and populate correct EDI info
				if parentPos != 0:#subPos = indexOrChild position = parentPos
					ediDict = {'segmentTag':parentNode.get('atts').get('name')[:3],'referenceNum':elementID, 'position':parentPos, 'subPos': indexOrChild}
				else:#subPos = 0. position = indexOrChild
					ediDict = {'segmentTag':parentNode.get('atts').get('name')[:3],'referenceNum':elementID, 'position':indexOrChild, 'subPos': '0'}
				
				#add edi info to each fieldNode
				util.add_attr(fieldNode, ediDict)
				
				#find the help text for each field
				helpText = field.find(".//%sdocumentation" %xs).text
				#create help tag
				util.add(fieldNode, util.make_help(helpText.split("_")[0]))

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
	fileString = sys.stdin.read()
	print(main(fileString))