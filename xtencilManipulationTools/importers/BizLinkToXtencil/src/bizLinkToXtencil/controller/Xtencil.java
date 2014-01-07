package bizLinkToXtencil.controller;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.w3c.dom.Node;


public class Xtencil {

	Document doc;
	Element rootElement;
	Element docElement;
	HashMap<String, String>documentProperties = new HashMap<String, String>();
	Element dateFormat;

	public Xtencil(Node node){
		Entity entity = new Entity(null, node, "doc", null);

		try{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();

			// SPS File properties
			rootElement = doc.createElement("SPSFILE");
			doc.appendChild(rootElement);
			rootElement.setAttribute("name", "SPS Commerce Xtencil");
			rootElement.setAttribute("fileType", "FORM");
			rootElement.setAttribute("contents", "NORM");
			rootElement.setAttribute("date", todaysDate("MM/dd/yyyy"));
			
			// Document properties
			docElement = createElement("DOCUMENTDEF", rootElement);
			docElement.setAttribute("name", "FEDS");
			docElement.setAttribute("designDate", todaysDate("MM/dd/yyyy"));
			docElement.setAttribute("xtencilType", "FEDS");
			docElement.setAttribute("encoding", "UTF-8");
			docElement.setAttribute("quoteUnwrap", "N");
			
			String docName = entity.getName();
			docElement.setAttribute("revision", docName.replaceAll(".*?_(.*?)_.*", "$1").toUpperCase());
			docElement.setAttribute("type", docName.replaceAll(".*_(.*)", "$1"));
			docElement.setAttribute("name", entity.getDocumentation());

			// Date formats
			Element format = doc.createElement("FORMAT");
			docElement.appendChild(format);
			
			Element fieldFormat = doc.createElement("FIELDFMT");
			dateFormat = fieldFormat;
			format.appendChild(fieldFormat);
			
			fieldFormat.setAttribute("dataType", "DATE");
			fieldFormat.setAttribute("maxLength", "2147483647");
			fieldFormat.setAttribute("minLength", "0");
			fieldFormat.setAttribute("justify", "NONE");
			fieldFormat.setAttribute("trim", "NONE");
			fieldFormat.setAttribute("name", "all");
			fieldFormat.setAttribute("keyType", "all");
			fieldFormat.setAttribute("desc", "all");
		}catch(Exception err){
		}
	}

	public String todaysDate(String strFormat){
		Date today = new Date();
		DateFormat df = new SimpleDateFormat(strFormat);
		return df.format(today.getTime());
	}
	
	public Element createElement(String elementName, Element parent){
		Element newElement = doc.createElement(elementName);
		parent.appendChild(newElement);
		return newElement;
	}

	public Element createElement(String elementName){
		Element newElement = doc.createElement(elementName);
		return newElement;
	}
	
	public void setProperties(Element element, HashMap<String, String> attributes){
		for(String key : attributes.keySet()){
			addProperties(element, key, attributes.get(key));
		}
	}

	public boolean hasData(String entity){
		return entity != null && !entity.equals("");
	}

	public void addProperties(Element element, String key, String value){
		if (hasData(key) && hasData(value)){
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
	
	public void setDateFormat(String format){
		dateFormat.setTextContent(format);
		dateFormat.setAttribute("writeFormat", format);
	}
	
	public void addHelp(Element parent, String helpText){
		Element help = doc.createElement("HELP");
		parent.appendChild(help);
		help.setTextContent(helpText);
	}
}
