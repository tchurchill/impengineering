/*
 * IDocToXtencilApp.java
 */

package idoctoxtencil;

import idoctoxtencil.controller.IdocParser;

import java.io.File;
import java.util.HashMap;

import sps.xd.docdesign.converter.XtencilFileCreator;
import utilities.*;



public class IDocToXtencilApp implements XtencilFileCreator {


    public static void main(String[] args) {
    	// Test build
    	//buildXTL("\\Users\\amcclintock\\Desktop\\E2.sap");

		ImporterUtilities utils = new ImporterUtilities();

		// Install plugin
		utils.copyJar("Importer_SAP.jar");

		// Update properties
		
		HashMap<String, String> props = new HashMap<String, String>();
		props.put("sap", "idoctoxtencil.IDocToXtencilApp");
		props.put("SAP", "idoctoxtencil.IDocToXtencilApp");
		utils.setProperties(props, "sps.xd.importer.plugins=sap\\=>idoctoxtencil.IDocToXtencilApp,SAP\\=>idoctoxtencil.IDocToXtencilApp");
    	
    }
    
	public File generateXtencilFromFile(String s) {
		return buildXTL(s);
	}
	
	static private File buildXTL(String fileName){
		File file = new File (fileName);
		String name = file.getName();
		
		String prefix = "";
		String format = "";
		
		if (name.length() > 1){
			prefix = name.substring(0,2);
		}
		if (name.endsWith("XML.sap")){
			format = "XML";
		}

		IdocParser parse = new IdocParser();
		File tmpFile = null;
		try{
			tmpFile = parse.processFile(file, prefix, format);
		}catch (Exception err){
		}

		return tmpFile;
	}
}
