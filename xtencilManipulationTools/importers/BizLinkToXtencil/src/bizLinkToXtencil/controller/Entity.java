package bizLinkToXtencil.controller;

import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Entity {
	String ref, name, type, javaName, segment, documentation, refID, entityType, dateFormat;
	Boolean mandatory = false;
	int min, max, position, subpos, precision;
	HashMap<String, String> properties = new HashMap<String, String>();

	public Entity(Node refNode, Node origNode, String entType, String segName){
		NamedNodeMap origAttributeMap= null, refAttributeMap = null;
		entityType = entType;
		segment = segName;

		if (refNode != null){
			refAttributeMap = refNode.getAttributes();
		}
		if (origNode != null){
			origAttributeMap = origNode.getAttributes();
		}

		ref = getPropertyString(origAttributeMap, refAttributeMap, "ref");
		name = getPropertyString(origAttributeMap, refAttributeMap, "name");
		if(name == null){
			name = getPropertyString(origAttributeMap, refAttributeMap, "ref");
		}
		
		if (entityType != null && entType.equals("seg")){
			name += "_Group";
		}

		if (name != null){
			javaName = makeJavaName(name);
		}
		
		min = getPropertyInt(origAttributeMap, refAttributeMap, "minLength");
		max = getPropertyInt(origAttributeMap, refAttributeMap, "maxLength");
		
		if (getPropertyInt(origAttributeMap, refAttributeMap, "minOccurs") != 0){
			min = getPropertyInt(origAttributeMap, refAttributeMap, "minOccurs");
		}

		if (entityType.equals("seg") && !hasProperty(origAttributeMap, refAttributeMap, "minOccurs")){
			min = 1;
		}
		
		if (getPropertyInt(origAttributeMap, refAttributeMap, "maxOccurs") != 0){
			max = getPropertyInt(origAttributeMap, refAttributeMap, "maxOccurs");
		}

		mandatory = getPropertyBoolean(origAttributeMap, refAttributeMap);

		if (origNode != null){
			examineChild(origNode);
		}
		if (refNode != null){
			examineChild(refNode);
		}
		
		setProperties();
	}

	private void setProperties(){
		properties.put("name", name);
		properties.put("javaName", javaName);
		properties.put("exclude", "N");
		properties.put("display", "Y");
		properties.put("enable", "Y");
		properties.put("print", "Y");
		properties.put("sourceFilter", "No Filter");
		properties.put("persistent", "Y");
		properties.put("present", "Y");
		properties.put("templatable", "Y");

		
		if (entityType != null && entityType.equals("seg")){ // Segment/Group settings
			properties.put("min", String.valueOf(min));
			if (max != 0){
				properties.put("max", String.valueOf(max));
			}else{
				properties.put("max", "1");
			}
		}else if (entityType != null && entityType.equals("elm")){// Element settings
			if (min == 0){
				properties.put("minLength", "1");
			}else{
				properties.put("minLength", String.valueOf(min));
			}
			if (max != 0){
				properties.put("maxLength", String.valueOf(max));
			}else{
				properties.put("maxLength", "1");
			}

			properties.put("position", String.valueOf(position));
			properties.put("subPos", String.valueOf(subpos));
			properties.put("referenceNum", refID);
			properties.put("segmentTag", segment);
			properties.put("dataType", type);
			if (mandatory){
				properties.put("mandatory", "Y");
			}
		}
	}
	
	
	private String getPropertyString(NamedNodeMap origAttributeMap, NamedNodeMap refAttributeMap, String ref){
		if (origAttributeMap != null && origAttributeMap.getNamedItem(ref) != null){
			return origAttributeMap.getNamedItem(ref).getNodeValue().trim();
		}else if (refAttributeMap != null && refAttributeMap.getNamedItem(ref) != null){
			return refAttributeMap.getNamedItem(ref).getNodeValue().trim();
		}
		return null;
	}

	private int getPropertyInt(NamedNodeMap origAttributeMap, NamedNodeMap refAttributeMap, String ref){
		if (origAttributeMap != null && origAttributeMap.getNamedItem(ref) != null){
			if (origAttributeMap.getNamedItem(ref).getNodeValue().trim().equals("unbounded")){
				return 9999999;
			}else{
				return Integer.valueOf(origAttributeMap.getNamedItem(ref).getNodeValue().trim());
			}
		}else if (refAttributeMap != null && refAttributeMap.getNamedItem(ref) != null){
			return Integer.valueOf(refAttributeMap.getNamedItem(ref).getNodeValue().trim());
		}
		return 0;
	}

	private boolean hasProperty(NamedNodeMap origAttributeMap, NamedNodeMap refAttributeMap, String ref){
		if (origAttributeMap != null && origAttributeMap.getNamedItem(ref) != null){
			return true;
		}else if (refAttributeMap != null && refAttributeMap.getNamedItem(ref) != null){
			return true;
		}
		return false;
	}
	
	private Boolean getPropertyBoolean(NamedNodeMap origAttributeMap, NamedNodeMap refAttributeMap){
		if (origAttributeMap != null && origAttributeMap.getNamedItem("use") != null && origAttributeMap.getNamedItem("use").getNodeValue().equals("required")){
			return true;
		}else if (refAttributeMap != null && refAttributeMap.getNamedItem("use") != null && refAttributeMap.getNamedItem("use").getNodeValue().equals("required")){
			return true;
		}
		return false;
	}

	private void examineChild(Node node){
		// Parse through node for attributes
		for (int i = 0; i < node.getChildNodes().getLength(); i++){
			Node nde = node.getChildNodes().item(i);
			if (nde.getNodeName().endsWith("annotation")){
				for (int j = 0; j < nde.getChildNodes().getLength(); j++){
					if (nde.getChildNodes().item(j).getNodeName().endsWith("documentation")){
						documentation = nde.getChildNodes().item(j).getTextContent().replaceAll("(.*)_.*", "$1");
					}else if (nde.getChildNodes().item(j).getNodeName().endsWith("appinfo")){
						for (int k = 0; k < nde.getChildNodes().item(j).getChildNodes().getLength(); k++){
							Node appInfoChild = nde.getChildNodes().item(j).getChildNodes().item(k);
							if (appInfoChild.getNodeName().endsWith("fieldInfo")){
								NamedNodeMap appProps = appInfoChild.getAttributes();
								refID = getNodeValue(appProps, "ReferenceDesignator");
								type = convertDataTypes(getNodeValue(appProps, "edi_datatype"));
								position = getNodeValueInt(appProps, "sequence_number");
								min = getNodeValueInt(appProps, "minLength");
								max = getNodeValueInt(appProps, "maxLength");
								if (getNodeValue(appProps, "format") != null && getNodeValue(appProps, "edi_datatype") != null && getNodeValue(appProps, "edi_datatype").equals("DT")){
									dateFormat = getNodeValue(appProps, "format").replaceAll("Y", "y").replaceAll("D", "d");
								}
							}else if (appInfoChild.getNodeName().endsWith("recordInfo")){
								NamedNodeMap recordProps = appInfoChild.getAttributes();
								if (getNodeValue(recordProps, "delimiter_type") != null && getNodeValue(recordProps, "delimiter_type").equals("inherit_subfield") && getNodeValue(recordProps, "sequence_number") != null){
									subpos = getNodeValueInt(recordProps, "sequence_number");
								}
							}
						}
					}
				}
			}else if (nde.getNodeName().endsWith("simpleType")){
				for (int j = 0; j < nde.getChildNodes().getLength(); j++){
					if (nde.getChildNodes().item(j).getNodeName().endsWith("restriction")){
						for (int k = 0; k < nde.getChildNodes().item(j).getChildNodes().getLength(); k++){
							Node appInfoChild = nde.getChildNodes().item(j).getChildNodes().item(k);
							if (appInfoChild.getNodeName().endsWith("length")){
								NamedNodeMap appProps = appInfoChild.getAttributes();
								max = getNodeValueInt(appProps, "value");
							}else if (appInfoChild.getNodeName().endsWith("minLength")){
								NamedNodeMap appProps = appInfoChild.getAttributes();
								min = getNodeValueInt(appProps, "value");
							}else if (appInfoChild.getNodeName().endsWith("maxLength")){
								NamedNodeMap appProps = appInfoChild.getAttributes();
								max = getNodeValueInt(appProps, "value");
							}
						}
					}
				}
			}
		}
	}

	private String convertDataTypes(String type){
		if (type.matches("ID|AN")){
			return "JString";
		}else if (type.equals("N0")){
			return "JInteger";
		}else if (type.matches("R|N")){
			return "JDouble";
		}else if (type.matches("N\\d+")){
			precision = Integer.valueOf(type.replaceAll("N(\\d+)", "$1"));
			return "JDouble";
		}else if (type.equals("DT")){
			return "JDate";
		}else if (type.equals("TM")){
			return "JTime";
		}
		return type;
	}
	
	private String getNodeValue(NamedNodeMap nodeMap, String ref){
		if (nodeMap.getNamedItem(ref) == null){
			return null;
		}else{
			return nodeMap.getNamedItem(ref).getNodeValue();
		}
	}
	
	private int getNodeValueInt(NamedNodeMap nodeMap, String ref){
		if (nodeMap.getNamedItem(ref) == null){
			return 0;
		}else{
			return Integer.valueOf(nodeMap.getNamedItem(ref).getNodeValue());
		}
	}

	private String makeJavaName(String name){
		String javaName = name.trim();
		if (javaName.matches("\\d")){
			javaName = "j" + javaName;
		}
		javaName = javaName.replaceAll("\\s", "");

		if (javaName.length() > 1){
			javaName = javaName.substring(0,1).toLowerCase() + javaName.substring(1);
		}
		
		return javaName;
	}	
	
	public void makeSubGroup(Element element, boolean isFirst){
		// Update group name
		int nextPos;
		if (isFirst){
			nextPos =  position - 1;
		}else{
			nextPos =  position + 1;
		}

		if (nextPos < 1){
			nextPos = 1;
		}

		String pos = String.format("%02d", nextPos);
		
		String newSegment = segment;
		if (newSegment == null){
			newSegment = name.replaceAll("(.*)_.*", "$1");
		}
		
		String posName = newSegment + pos;
		String posNameGroup = posName + "_Group";
		element.setAttribute("name", posNameGroup);
		element.setAttribute("javaName", makeJavaName(posNameGroup));

		// Sometimes composites are repeatable
		if (Integer.valueOf(element.getAttribute("max")) > 1){
			System.out.println("It's a REP!");
			makeSubGroup((Element)element.getFirstChild(), isFirst);
			return;
		}
		
		
		// Update elements to match true document position
		NodeList childList = element.getChildNodes();
		for (int i = 0; i < childList.getLength(); i++){
			Node node = childList.item(i);
			NamedNodeMap attributeMap = node.getAttributes();
			
			int subPos = Integer.valueOf(attributeMap.getNamedItem("position").getNodeValue());;
			String subPosition = String.format("%02d", subPos);
			String subPosName = posName + "_C" + subPosition;
			
			// Update element properties
			attributeMap.getNamedItem("name").setNodeValue(subPosName);
			attributeMap.getNamedItem("javaName").setNodeValue(makeJavaName(subPosName));
			attributeMap.getNamedItem("subPos").setNodeValue(attributeMap.getNamedItem("position").getNodeValue());
			attributeMap.getNamedItem("position").setNodeValue(String.valueOf(nextPos));
			if (segment != null){
				attributeMap.getNamedItem("segmentTag").setNodeValue(segment);
			}else{
				attributeMap.getNamedItem("segmentTag").setNodeValue(newSegment);
			}
		}
	}
	
	
	public String getRef(){
		return ref;
	}
	
	public String getName(){
		return name;
	}
	
	public String getType(){
		return type;
	}
	
	public String getJavaName(){
		return javaName;
	}
	
	public String getSegment(){
		return segment;
	}
	
	public String getDocumentation(){
		return documentation;
	}
	
	public int getPosition(){
		return position;
	}
	
	public String getRefID(){
		return refID;
	}
	
	public boolean isMandatory(){
		return mandatory;
	}
	
	public int getMin(){
		return min;
	}
	
	public int getMax(){
		return max;
	}
	
	public boolean hasDateFormat(){
		return dateFormat != null;
	}
	
	public String getDateFormat(){
		return dateFormat;
	}
	
	public boolean hasDocumentation(){
		return documentation != null;
	}
	
	public boolean hasSubPos(){
		return subpos != 0;
	}
	
	public int getSubPos(){
		return subpos;
	}
}
