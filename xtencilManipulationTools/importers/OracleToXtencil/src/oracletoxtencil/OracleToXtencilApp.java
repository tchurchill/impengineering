/*
 * OracleToXtencilApp.java
 */

package oracletoxtencil;

import utilities.*;

import java.io.File;
import java.util.HashMap;

import oracletoxtencil.controller.OracleParser;
import sps.xd.docdesign.converter.XtencilFileCreator;


public class OracleToXtencilApp implements XtencilFileCreator {

	public File generateXtencilFromFile(String s) {
		return buildXTL(s);
	}
	
	static private File buildXTL(String fileName){
		File file = new File (fileName);
		
		OracleParser parse = new OracleParser();
		File tmpFile = null;
		try{
			tmpFile = parse.parseFile(file);
		}catch (Exception err){
		}

		return tmpFile;
	}

    public static void main(String[] args) {
    	// Test build
//    	buildXTL("\\Users\\amcclintock\\Desktop\\layout.oracle");

		ImporterUtilities utils = new ImporterUtilities();

		// Install plugin
		utils.copyJar("Importer_Oracle.jar");

		// Update properties
		
		HashMap<String, String> props = new HashMap<String, String>();
		props.put("oracle", "oracletoxtencil.OracleToXtencilApp");
		utils.setProperties(props, "sps.xd.importer.plugins=oracle\\=>oracletoxtencil.OracleToXtencilApp");

    }
}
