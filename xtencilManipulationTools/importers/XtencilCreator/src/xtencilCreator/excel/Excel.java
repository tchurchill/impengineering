package xtencilCreator.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.poifs.filesystem.*;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import xtencilCreator.xmlDom.XML;
import xtencilCreator.utils.*;


public class Excel {
    public Sheet getData(File inputFile){
        if (inputFile.getName().endsWith(".xls")){
            return getXLSData(inputFile);
        }else if (inputFile.getName().endsWith(".xlsx")){
            return getXLSXData(inputFile);
        }
        return null;        
    }
 
    private Sheet getXLSData(File inputFile){
        try {
            InputStream inFileStream = new FileInputStream(inputFile);
            POIFSFileSystem myFileSystem = new POIFSFileSystem(inFileStream);
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            return myWorkBook.getSheetAt(0);      
        } catch (Exception ex) {
            return null;
        }
    }
    
    private Sheet getXLSXData(File inputFile){
        try{
            InputStream inFileStream = new FileInputStream(inputFile);
            XSSFWorkbook myWorkBook = new XSSFWorkbook(inFileStream); 
            return myWorkBook.getSheetAt(0);
        }catch (Exception ex){
            return null;
        }
    }

    public xtencilCreator.xmlDom.XML parseSpreadSheet(Sheet spreadsheet){
    	XML xmlDocument = new XML();
    	Element currentElement = (Element) xmlDocument.getDocDef();
    
    	HashMap <Integer, String> keyByColumn = new HashMap<Integer, String>();
    	
        for (Row row: spreadsheet){
        	if (row.getRowNum() == 0){
        		// Cell attributes
        		getAttributes(row, keyByColumn);
        	}else if (row.getRowNum() > 6 && row.getRowNum() < 11){
        		// Document information
        		parseDocumentProperties(xmlDocument ,row);
        	}else if (row.getRowNum() > 13){
        		// Skeleton
        		RowInfo parsedRow = new RowInfo(row, keyByColumn);
        		currentElement = buildElement(xmlDocument, currentElement, parsedRow);
        	}
        }
        
        // Update document properties
        writeDocProperties(xmlDocument);
        
        return xmlDocument;
    }
    
    private void writeDocProperties(XML xmlDocument){
    	Element doc = xmlDocument.getDocDef();
    	HashMap<String, String> docProps = xmlDocument.getDocumentProperties();

    	xmlDocument.addProperties(doc, "owner", docProps.get("owner"));
    	xmlDocument.addProperties(doc, "resolverFetch", docProps.get("resolverFetch"));
    	xmlDocument.addProperties(doc, "resolverInsert", docProps.get("resolverInsert"));
    	xmlDocument.addProperties(doc, "keys", docProps.get("keys"));
    	xmlDocument.addProperties(doc, "revision", docProps.get("rsxVersion"));
    	xmlDocument.addProperties(doc, "quoteWrap", docProps.get("quoteWrap"));
    	xmlDocument.addProperties(doc, "quoteUnwrap", docProps.get("quoteUnWrap"));
    	xmlDocument.addProperties(doc, "xtencilType", "Flat File");
    	xmlDocument.addProperties(doc, "name", docProps.get("docType"));
    	xmlDocument.addProperties(doc, "type", docProps.get("docType"));
    	xmlDocument.addProperties(doc, "encoding", "UTF-8");
    	
    	// Java Name
    	if (docProps.containsKey("docID") && (docProps.containsKey("resolverFetch") || docProps.containsKey("resolverInsert"))){
    		String javaName = docProps.get("docID").toLowerCase() + "Cus";
    		if (docProps.containsKey("resolverFetch")){
    			javaName = javaName + "Read";
    		}else if (docProps.containsKey("resolverInsert")){
    			javaName = javaName + "Write";
    		}
    		
    		xmlDocument.addProperties(doc, "javaName", javaName);
    	}
    	
    	// Map Path
    	if (docProps.containsKey("owner") && docProps.containsKey("rsxVersion") && docProps.containsKey("docID") && docProps.containsKey("docFormat")){
    		String mapPath = "formats.";
    		
    		String owner = docProps.get("owner");
    		owner = Character.toLowerCase(owner.charAt(0)) + (owner.length() > 1 ? owner.substring(1) : "");
    		owner = owner.trim().replaceAll("\\s", "");
    		if (owner.matches("abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|double|do|else|enum|extends|false|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|native|new|null|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throw|throws|transient|true|try|void|volatile|while|^\\d.*")){
    			owner = "j_" + owner;
    		}
    		
    		mapPath += owner + ".";
    		mapPath += "v" + docProps.get("rsxVersion").replaceAll("\\.", "_") + ".";
    		mapPath += docProps.get("docFormat").replaceAll(".*-", "").trim().toLowerCase() + ".";
    		mapPath += docProps.get("docID").toLowerCase();
    		xmlDocument.addProperties(doc, "javaPackageName", mapPath);
    	}
    	
    	// Date Format and Delimiter
    	if (docProps.containsKey("dateFormat") || docProps.containsKey("docFormat")){
    		Element format = xmlDocument.createElement("FORMAT", doc);
    		if (docProps.containsKey("dateFormat")){
    			Element dateFormat = xmlDocument.createElement("FIELDFMT", format);
    			dateFormat.setAttribute("dataType", "DATE");
    			dateFormat.setAttribute("maxLength", "2147483647");
    			dateFormat.setAttribute("minLength", "0");
    			dateFormat.setAttribute("justify", "NONE");
    			dateFormat.setAttribute("trim", "NONE");
    			dateFormat.setAttribute("name", "all");
    			dateFormat.setAttribute("keyType", "all");
    			dateFormat.setAttribute("desc", "all");
    			if (docProps.containsKey("resolverFetch")){
    				dateFormat.setTextContent(docProps.get("dateFormat"));
        		}else if (docProps.containsKey("resolverInsert")){
        			dateFormat.setAttribute("writeFormat", docProps.get("dateFormat"));
        		}
    		}
    		if (docProps.containsKey("docFormat") && !docProps.get("docFormat").startsWith("Fixed length")){
    			String value = docProps.get("docFormat").replaceAll(".*\\((.*)\\).*", "$1");
    			
    			Element delimFormat = xmlDocument.createElement("DELIMITER", format);
    			delimFormat.setAttribute("type", "STRING");
    			delimFormat.setAttribute("appliesTo", "FIELD");
    			delimFormat.setAttribute("position", "DEFAULT");
    			delimFormat.setAttribute("tagType", "NORMAL");
    			delimFormat.setAttribute("tagContent", "NAME");
    			delimFormat.setTextContent(value);
    		}
    	}
    }
    
    private void parseDocumentProperties(XML xmlDocument, Row row){
        for (Cell cell : row){
        	Cell nextCell = row.getCell(cell.getColumnIndex() + 1);
        	if (cell.toString().endsWith(":")){
        		if (cell.toString().equals("Customer name:") && new Utils().hasData(nextCell.toString())){
        			xmlDocument.setDocumentProp("owner", nextCell.toString());
        		}else if (cell.toString().equals("Format type:") && new Utils().hasData(nextCell.toString())){
        			xmlDocument.setDocumentProp("docFormat", nextCell.toString());
        		}else if (cell.toString().equals("Date format:") && new Utils().hasData(nextCell.toString())){
        			xmlDocument.setDocumentProp("dateFormat", nextCell.toString());
        		}else if (cell.toString().equals("RSX version:") && new Utils().hasData(nextCell.toString())){
        			xmlDocument.setDocumentProp("rsxVersion", nextCell.toString());
        		}else if (cell.toString().equals("Document Type:") && new Utils().hasData(nextCell.toString())){
        			xmlDocument.setDocumentProp("docType", nextCell.toString().replaceAll("\\(.*|\\-.*", "").trim());
        			xmlDocument.setDocumentProp("docID", nextCell.toString().replaceAll(".*-", "").trim());
        		}else if (cell.toString().equals("Quote unwrap:")){
        			if (new Utils().hasData(nextCell.toString()) && nextCell.toString().equals("Yes")){
        				xmlDocument.setDocumentProp("quoteUnWrap", "Y");
        			}else{
        				xmlDocument.setDocumentProp("quoteUnWrap", "N");
        			}
        		}else if (cell.toString().equals("Quote wrap:") && new Utils().hasData(nextCell.toString())){
        			if (new Utils().hasData(nextCell.toString()) && nextCell.toString().equals("Yes")){
        				xmlDocument.setDocumentProp("quoteWrap", "Y");
        			}else{
        				xmlDocument.setDocumentProp("quoteWrap", "N");
        			}
        		}else if (cell.toString().equals("Read/Write:") && new Utils().hasData(nextCell.toString()) && nextCell.toString().equals("Read")){
        			xmlDocument.setDocumentProp("resolverFetch", "Y");
        		}else if (cell.toString().equals("Read/Write:") && new Utils().hasData(nextCell.toString()) && nextCell.toString().equals("Write")){
        			xmlDocument.setDocumentProp("resolverInsert", "Y");
        		}
        	}
        }
    }
    
    private void getAttributes(Row row, HashMap<Integer, String> keyByColumn){
        for (Cell cell : row){
        	keyByColumn.put(cell.getColumnIndex(), cell.toString());
        }
    }
    
    private Element buildElement(XML xmlDocument, Element currentElement, RowInfo parsedRow){
    	HashMap<String, String> attributes = parsedRow.getAttributes();
    	ArrayList<String> currentJavaNames = new ArrayList<String>();
    	
    	//Capture java names
        NodeList nodeList = currentElement.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
        	Element element = (Element)nodeList.item(i);
        	currentJavaNames.add(element.getAttribute("javaName"));
        }
 
    	
    	if (!parsedRow.hasAttributes() && !parsedRow.hasRowType()){
    	//Noise
    		return currentElement;
    	}else if (!parsedRow.hasRowType() || parsedRow.getRowType().equals("Landmark Field") || parsedRow.getRowType().equals("Document Identifying Field")){
    	//New JElement
    		Element newElement = xmlDocument.createElement("FIELDDEF", currentElement);
    		xmlDocument.setProperties(newElement, attributes);
    		dupeJavaNameCheck(newElement, newElement.getAttribute("javaName"), currentJavaNames);
    		
    		//Check if landmark is at element and not group
    		if (new Utils().hasData(parsedRow.getRowType()) && parsedRow.getRowType().equals("Landmark Field") && attributes.containsKey("type")){
    			((Element)newElement.getParentNode()).setAttribute("type", attributes.get("type"));
    			((Element)newElement.getParentNode()).setAttribute("isRecord", "Y");
    			if (((Element)newElement.getParentNode()).getAttribute("javaName").endsWith("Rep")){
    				((Element)newElement.getParentNode().getParentNode()).setAttribute("type", attributes.get("type"));
    			}
    		}
    		
    		// Marks document identifier
    		if (new Utils().hasData(parsedRow.getRowType()) && parsedRow.getRowType().equals("Document Identifying Field")){
    			String documentKey = newElement.getAttribute("javaName");
    			Element parent = (Element)newElement.getParentNode();
    			while (parent.getNodeName().equals("GROUPDEF")){
    				documentKey = parent.getAttribute("javaName") + "." + documentKey;
    				parent = (Element)parent.getParentNode();
    			}
    			xmlDocument.setDocumentProp("keys", documentKey);
    		}
    	}else if (parsedRow.getRowType().equals("Start Group")){
    	//New JGroup
    		Element newGroup = xmlDocument.createElement("GROUPDEF", currentElement);
    		xmlDocument.setProperties(newGroup, attributes);
    		xmlDocument.addProperties(newGroup, "isRecord", "Y");
    		currentElement = newGroup;
    		String rename = dupeJavaNameCheck(newGroup, newGroup.getAttribute("javaName"), currentJavaNames);
  
    		if (attributes.containsKey("maxLength") && !attributes.get("maxLength").equals("0")){
    			//Make the repeatable group
    			xmlDocument.addProperties(newGroup, "min", "0");
        		xmlDocument.addProperties(newGroup, "max", attributes.get("maxLength"));

    			//Model the repetition
        		Element newGroupRep = xmlDocument.createElement("GROUPDEF", currentElement);
        		parsedRow.setDefaultProperties(newGroupRep);
        		if (rename == null){
        			rename = attributes.get("name");
                    char c[] = rename.toCharArray();
                    c[0] = Character.toLowerCase(c[0]);
                    rename = new String(c);
                    //remove whitespace and non-alpha characters from java name
                    rename = rename.trim().replaceAll("[^A-Za-z0-9]","");
        		}
        		xmlDocument.addProperties(newGroupRep, "name", attributes.get("name") + "Rep");
        		xmlDocument.addProperties(newGroupRep, "javaName", rename + "Rep");
        		xmlDocument.addProperties(newGroupRep, "min", "1");
        		xmlDocument.addProperties(newGroupRep, "max", "1");
        		if (attributes.containsKey("type")){
        			xmlDocument.addProperties(newGroupRep, "isRecord", "Y");
        			xmlDocument.addProperties(newGroupRep, "type", attributes.get("type"));
        		}
        		currentElement = newGroupRep;
    		}else{
    			xmlDocument.addProperties(newGroup, "max", "1");
    			if (attributes.containsKey("maxLength") && attributes.get("maxLength").equals("0")){
    				xmlDocument.addProperties(newGroup, "min", "0");
    			}else{
    				xmlDocument.addProperties(newGroup, "min", "1");
    			}
    		}
    	}else if (parsedRow.hasRowType() && parsedRow.getRowType().equals("Stop Group")){
    		if (currentElement.hasAttribute("name") && currentElement.getAttribute("name").endsWith("Rep")){
    			currentElement = (Element)currentElement.getParentNode();
    		}
    		currentElement = (Element)currentElement.getParentNode();
    	}
   
    	return currentElement;
    }
    
    private String dupeJavaNameCheck(Element element, String currentJavaName, ArrayList<String> usedJavaNames){
    	//Update java names to avoid dupes
    	String rename = null;

    	for (int i = 1; i < 1000 && usedJavaNames.contains(currentJavaName) && rename == null; i++){
    		rename = currentJavaName.replaceAll("\\d+$", "") + i;
    	}

    	if (reJavaName(element, rename)){
    		return rename;
    	}else{
    		return null;
    	}
    }
    
    private boolean reJavaName(Element element, String name){
    	if (new Utils().hasData(name)){
    		element.setAttribute("javaName", name);
    		return true;
    	}
    	return false;
    }
}

