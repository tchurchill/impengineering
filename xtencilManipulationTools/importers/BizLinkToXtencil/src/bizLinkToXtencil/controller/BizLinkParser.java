package bizLinkToXtencil.controller;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BizLinkParser {
	File inputFile;
	Document bizDoc;
	HashMap<String, Node> elementIndex = new HashMap<String, Node>();
	Xtencil xtl;
	Element parent;
	HashMap<Integer, Element> subElement = new HashMap<Integer, Element>();
	boolean inSubGroup = false;
	Entity lastEntity;
	
	
	public BizLinkParser (File file){
		inputFile = file;
		try {
			// Parse the XML file
			parseFile();
			
			// Parse the DOM and build XTL DOM
			getFirstElement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseFile() throws Exception{
		// Get the DOM
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		// Get the DOM Builder
		DocumentBuilder builder = factory.newDocumentBuilder();

		// Parse the document
		bizDoc = builder.parse(inputFile);

		// Index Elements
		indexElements(bizDoc.getDocumentElement().getChildNodes());
	}

	private void indexElements(NodeList nodeList){
		// Gather all elements from document for quick reference
		for (int i = 0; i < nodeList.getLength(); i++){
			Node node = nodeList.item(i);
			if (node instanceof Element && node.getAttributes().getNamedItem("name") != null){
				elementIndex.put(node.getAttributes().getNamedItem("name").getNodeValue(), node);
			}
		}
	}
	
	private void getFirstElement(){
		// Used to build document information
		NodeList nodeList = bizDoc.getDocumentElement().getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++){
			Node node = nodeList.item(i);
			if (node instanceof Element && node.getAttributes().getNamedItem("name") != null){
				parseNode(node);
				break;
			}
		}
	}

	private void parseStructure(NodeList nodeList){
		// Walk through structure
		for (int i = 0; i < nodeList.getLength(); i++){
			parseNode(nodeList.item(i));
		}
	}

	private void parseNode(Node node){
		boolean inSubPos = false;
		Element subParent = null;
		
		if (node instanceof Element){
			NamedNodeMap attributeMap = node.getAttributes();

			if (xtl == null){
				// Create new xtl document
				xtl = new Xtencil(node);
				parent = xtl.docElement;
			}
			
	
			if (node.getNodeName().endsWith("element") && attributeMap.getNamedItem("ref") != null && elementIndex.containsKey(attributeMap.getNamedItem("ref").getNodeValue())){ // References another Group
				Node referenceNode = elementIndex.get(attributeMap.getNamedItem("ref").getNodeValue());
				Entity entity = new Entity(referenceNode, node, "seg", null);
				
				String ref = attributeMap.getNamedItem("ref").getNodeValue();

				// Create the Segment group
				Element currentElement;
				if (entity.hasSubPos()){
					// Group holding sub-elements, these are present at the start of the parent group.  Make the group
					// associate sub elements.  Store until correct position.  Then add.
					currentElement = xtl.createElement("GROUPDEF");
					subParent = parent;
					inSubPos = true;
					inSubGroup = true;
					subElement.put(entity.getSubPos(), currentElement);
				}else{
					currentElement = xtl.createElement("GROUPDEF", parent);
					lastEntity = entity;
				}
				xtl.setProperties(currentElement, entity.properties);
				
				// Set new group as parent
				parent = currentElement;
				
				// Check if repeatable group
				if (entity.getMax() > 1){
					// Build the rep, using same information as repeatable group
					currentElement = xtl.createElement("GROUPDEF", parent);
					xtl.setProperties(currentElement, entity.properties);
					
					// Over-ride some properties
					xtl.addProperties(currentElement, "name", entity.getName() + "Rep");
					xtl.addProperties(currentElement, "javaName", entity.getJavaName() + "Rep");
					xtl.addProperties(currentElement, "max", "1");
					xtl.addProperties(currentElement, "min", "1");
					
					// This is the new parent
					parent = currentElement;
				}

								// System out for debug
								if (ref.contains("Loop")){
									ref = ref.replaceAll("(.*)(Loop.*)", "$1_$2");
									System.out.println(ref);
								}else{
									System.out.println("Group_" + ref);
								}
								

				// Parse through reference to build structure
				parseStructure(referenceNode.getChildNodes());

			}else if (node.getNodeName().endsWith("attribute")){ // Add an element
				
								// System out for debug
								System.out.println("\tElement\t" + attributeMap.getNamedItem("name"));
				
				// Parse the details
				Entity entity = new Entity(null, node, "elm", parent.getAttribute("name").replaceAll("_.*", ""));
				
				
				// Sometimes composite elements are first
				if (entity.getPosition() > 0 && !inSubGroup && subElement.containsKey(entity.getPosition() - 1)){
					// Update sub-element information
					Element appendElement = subElement.get(entity.getPosition() - 1);
					entity.makeSubGroup(appendElement, true);
					
					// Add sub-element group	
					parent.appendChild(appendElement);
					subElement.remove(entity.getPosition() - 1);
				}
				
				
				// Create the Element
				Element currentElement = xtl.createElement("FIELDDEF", parent);
				xtl.setProperties(currentElement,entity.properties);
				
				// Set help text if available
				if (entity.hasDocumentation()){
					xtl.addHelp(currentElement, entity.getDocumentation());
				}
				
				// Set document date format if parsed from data
				if (entity.hasDateFormat()){
					xtl.setDateFormat(entity.getDateFormat());
				}	
				
				// Check if there is a sub-element group to add next

				if (entity.getPosition() > 0 && !inSubGroup && subElement.containsKey(entity.getPosition() + 1)){
					// Update sub-element information
					Element appendElement = subElement.get(entity.getPosition() + 1);
					entity.makeSubGroup(appendElement, false);
					
					// Add sub-element group	
					parent.appendChild(appendElement);
					subElement.remove(entity.getPosition() + 1);
				}
	
				
				// Elements don't have kids
				return;

			}else if (node.getNodeName().matches("xs:element|xs:sequence|xs:complexType")){
				// Find more kids
				parseStructure(node.getChildNodes());
			}

			
			// If an element move up a parent, aka finish the group
			if (node.getNodeName().endsWith("element") && !inSubPos){ 

				//Sometimes there are only composite elements, add them before moving up
				if (!subElement.isEmpty()){
					for (int key : subElement.keySet()){
						// Update sub-element information
						Element appendElement = subElement.get(key);
						lastEntity.makeSubGroup(appendElement, true);
						
						// Add sub-element group	
						parent.appendChild(appendElement);
					}
					// Empty all groups
					subElement.clear();
				}
						
				if (parent.getAttribute("name").endsWith("Rep")){
					// Step up an extra level for Rep
					parent = (Element)parent.getParentNode();
				}
				parent = (Element)parent.getParentNode();
				

			}else if (node.getNodeName().endsWith("element") && inSubPos){
				// Account for sub-elements
				inSubPos = false;
				inSubGroup = false;
				parent = subParent;
			}			
		}
	}


	public File writeXtl(){
		return xtl.writeDocument();
	}
}
