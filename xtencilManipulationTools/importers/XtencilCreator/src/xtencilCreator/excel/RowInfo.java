package xtencilCreator.excel;

import java.util.HashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.w3c.dom.Element;

import xtencilCreator.utils.*;

public class RowInfo {
	String rowType = null;
	HashMap<String, String> attributes = new HashMap<String, String> ();
	HashMap<String, String> defaultProperties = new HashMap<String, String> ();

	RowInfo (Row row, HashMap<Integer, String> keyByColumn){
		populateDefaultProperties();
        for (Cell cell : row){
        	if (!new Utils().hasData(row.toString())) continue;
        	if (!keyByColumn.containsKey(cell.getColumnIndex())) continue;

        	String propertyName = keyByColumn.get(cell.getColumnIndex());
        	//Add default properties
        	if(new Utils().hasData(cell.toString())){
        		addDefaultProperties();
        	}

        	//ID Landmark Field
        	if(new Utils().hasData(cell.toString()) && new Utils().hasData(rowType) && rowType.equals("Landmark Field")){
        		attributes.put("keyType", "LANDMARK");
        	}else if(new Utils().hasData(cell.toString())){
        		attributes.put("keyType", "NONE");
        	}
        	
        	//Clean and modify properties to what XD wants to see
        	if (propertyName.equals("info")){
        		rowType = cell.toString();
        	}else if(propertyName.equals("name") && new Utils().hasData(cell.toString())){
        		attributes.put("javaName", makeJavaName(cell.toString()));
        		attributes.put(propertyName, cell.toString());
        	}else if(propertyName.equals("maxLength") && new Utils().hasData(cell.toString())){
        		String maxLength = cell.toString().replaceAll("\\.0*$", "");
        		attributes.put(propertyName, maxLength);
        		attributes.put("max", maxLength);
        		attributes.put("minLength", "1");
        	}else if(propertyName.equals("dataType") && new Utils().hasData(cell.toString())){
        		String[] properties = makeDateType(cell.toString());
        		attributes.put("dataType", properties[0]);
        		if (properties[1] != null){
        			attributes.put("precision", properties[1]);
        		}
        	}else if(new Utils().hasData(cell.toString())){
        		attributes.put(propertyName, cell.toString());
        	}
        }
	}
	
	public String getRowType(){
		return rowType;
	}
	
	public HashMap<String, String> getAttributes(){
		return attributes;
	}
	
	public boolean hasAttributes(){
		return attributes != null && !attributes.isEmpty();
	}
	
	public boolean hasRowType(){
		return new Utils().hasData(rowType);
	}
	
	public String makeJavaName(String original){
		original = Character.toLowerCase(original.charAt(0)) + (original.length() > 1 ? original.substring(1) : "");
		original = original.trim().replaceAll("[\\W\\s]", "");
		if (original.matches("abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|double|do|else|enum|extends|false|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|native|new|null|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throw|throws|transient|true|try|void|volatile|while|^\\d.*")){
			original = "j_" + original;
		}

		return original;
	}
	
	public String[] makeDateType(String original){
		String[] properties = new String[2];
		
		if (original.matches("A|AN")){
			properties[0] = "JString";
		}else if (original.equals("R")){
			properties[0] = "JDouble";
		}else if (original.equals("DT")){
			properties[0] = "JDate";
		}else if (original.equals("N0")){
			properties[0] = "JInteger";
		}else if (original.matches("N\\d+")){
			properties[0] = "JDouble";
			properties[1] = original.replaceAll("^N", "");
		}else{
			properties[0] = "JString";
		}
		return properties;
	}
	
	private void populateDefaultProperties(){
		defaultProperties.put("exclude", "N");
		defaultProperties.put("justification", "Left");
		defaultProperties.put("display", "Y");
		defaultProperties.put("enable", "Y");
		defaultProperties.put("print", "Y");
		defaultProperties.put("nextRow", "N");
    	defaultProperties.put("sourceFilter", "No Filter");
    	defaultProperties.put("persistent", "Y");
    	defaultProperties.put("present", "Y");
    	defaultProperties.put("mandatory", "N");
    	defaultProperties.put("editable", "Y");
    	defaultProperties.put("templatable", "Y");
    	defaultProperties.put("dtdRequired", "N");
    	defaultProperties.put("edi", "Y");
	}
	
	public void addDefaultProperties(){
		attributes.putAll(defaultProperties);
	}
	
	public void setDefaultProperties(Element element){
		for(String key : defaultProperties.keySet()) {
			element.setAttribute(key, defaultProperties.get(key));
		}
	}
}
