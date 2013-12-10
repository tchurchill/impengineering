package xtencilCreator.xmlDom;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import xtencilCreator.utils.Utils;

public class XML {
	Document doc;
	Element rootElement;
	Element docElement;
	HashMap<String, String>documentProperties = new HashMap<String, String>();

	public XML(){
		try{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();

			rootElement = doc.createElement("SPSFILE");
			doc.appendChild(rootElement);
			rootElement.setAttribute("name", "SPS Commerce Xtencil");
			rootElement.setAttribute("fileType", "FORM");
			rootElement.setAttribute("contents", "NORM");
			rootElement.setAttribute("date", new Utils().todaysDate("MM/dd/yyyy"));
			
			docElement = createElement("DOCUMENTDEF", rootElement);
			docElement.setAttribute("name", "Test Name");
			docElement.setAttribute("javaName", "Test Java Name");
			docElement.setAttribute("javaPackageName", "Test Java Package Name");
			docElement.setAttribute("designDate", new Utils().todaysDate("MM/dd/yyyy"));
		}catch(Exception err){
		}
	}
	
	public Element createElement(String elementName, Element parent){
		Element newElement = doc.createElement(elementName);
		parent.appendChild(newElement);
		return newElement;
	}
	
	public void setProperties(Element element, HashMap<String, String> attributes){
		for(String key : attributes.keySet()){
			addProperties(element, key, attributes.get(key));
		}
	}

	public void addProperties(Element element, String key, String value){
		if (new Utils().hasData(key) && new Utils().hasData(value)){
			element.setAttribute(key, value);
		}
	}
	
	public File writeDocument(){
		// write the content into xml file
		try{
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			DOMSource source = new DOMSource(doc);
			File tmpDir = new File(System.getProperty("user.home") + File.separator + "xtlTemp" + File.separator);
			tmpDir.mkdirs();
			File tmpFile = new File (tmpDir.getAbsolutePath() + File.separator + "tmp.xtl");

			StreamResult result = new StreamResult(tmpFile);

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "C:\\XtencilDesigner\\form\\xtencil.dtd");
			
			transformer.transform(source, result);

			return tmpFile;
		}catch(Exception err){
			return null;
		}
	}
	
	public Element getRoot(){
		return rootElement;
	}
	
	public Document getDocument(){
		return doc;
	}
	
	public Element getDocDef(){
		return docElement;
	}
	
	public void setDocumentProp(String key, String value){
		documentProperties.put(key, value);
	}
	
	public HashMap<String, String> getDocumentProperties(){
		return documentProperties;
	}
}
