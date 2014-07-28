from lxml import html, etree		
import os
import time
import importer_util as util
import json
import codecs

structure = 0
attributes = ''
doc = html.fromstring(open('PROACT01_d.htm').read())
groupName = ""	

def changeGroupName(groupName):
	if groupName[0].startswith('Z'):		
		groupName = groupName[0].replace("Z","E2")+groupName[1:]
	elif groupName[:2].startswith('E1'):
		groupName = groupName[0].replace("E","E2")+groupName[2:]
	return groupName.strip()
	
def isValidJavaName(javaName):
	javaName = javaName[0].lower()+javaName[1:].strip()
	javaName = javaName.replace(' ', '_')
	if(javaName[0].isdigit()):
		javaName = javaName[1:]
	return javaName
	
def getGroup(parent,node):
	isRep = False
	print "\n\n"
	print " PARENT 1. "
	print parent
	for index,groupNode in enumerate(node.iterfind('li')):		
		childNodeFlag = False	
		groupName = ""		
		if 'li' in groupNode.tag:
			lookNode = groupNode
			for num, childNode in enumerate(groupNode.iter()):
				if num > 10:
					break
				if 'b' in childNode.tag:					
					groupName =  childNode.text.rsplit(':')[0]
				if 'p' in childNode.tag:					
					if 'Status' in childNode.text:																		
						attributes = childNode.text.rsplit(',')
						if attributes[2].split(':')[1].strip() == "1" and attributes[0].split(':')[1].strip() == "Required":
							curParent = util.add(parent,util.make_group(changeGroupName(groupName),1,attributes[2].rsplit(':')[1].strip()))
						#elif attributes[2].split(':')[1].strip() > "1" and attributes[0].split(':')[1].strip() == "Optional":
						elif attributes[2].split(':')[1].strip() > "1":
							isRep = True
							curParent = util.add(parent, util.make_repGroup(groupName, 1,attributes[2].rsplit(':')[1].strip()))
				count = 0
				if 'ul' in childNode.tag:	
					childNodeFlag = True	
					getGroup(curParent,childNode)				
					
		if isRep == True and childNodeFlag == False:
			isRep = False
		elif isRep == True and childNodeFlag == True:
			isRep = False

if __name__ == "__main__":
	for index,node in enumerate(doc.iter()):	
		if node.text: 
			if node.text.find("Structure of basic type") != -1:				
				structure = index+4
			if index == structure and index != 0:		
				xtlRoot = util.make_spsfile()	
				xtlDoc = util.add(xtlRoot, util.make_document("IDOC", "SAP"))				
				getGroup(xtlDoc, node)
				with codecs.open("myJsonPROACT01_d.json", "w") as newJson:
					jsonStr = json.dumps(xtlRoot)
					newJson.write(jsonStr)			
		