package xtencilCreator.controller;


import java.io.File;
import java.util.HashMap;

import sps.xd.docdesign.converter.XtencilFileCreator;
import xtencilCreator.excel.*;
import xtencilCreator.utils.ImporterUtilities;

public class XtencilCreator implements XtencilFileCreator{
	public static File currentFile;
	public static xtencilCreator.xmlDom.XML xmlDocument;


	public static void main(String[] args) {
        //buildXTL("\\Users\\mmerth\\Desktop\\test1_DesignDocTemplate.xlsx");
		ImporterUtilities utils = new ImporterUtilities();

		// Install plugin
		utils.copyJar("Importer_XLSX.jar");

		// Update properties
		HashMap<String, String> props = new HashMap<String, String>();
		props.put("xls", "xtencilCreator.controller.XtencilCreator");
		props.put("xlsx", "xtencilCreator.controller.XtencilCreator");
		utils.setProperties(props, "sps.xd.importer.plugins=xlsx\\=>xtencilCreator.controller.XtencilCreator,xls\\=>xtencilCreator.controller.XtencilCreator");
	}

	public File generateXtencilFromFile(String s) {
		return buildXTL(s);
	}
	
	private static File buildXTL(String fileName){
		currentFile = new File(fileName);
	
		//Get spreadsheet
		Excel excel = new Excel();
		org.apache.poi.ss.usermodel.Sheet spreadsheet = excel.getData(currentFile);

		//Parse spreadsheet
		xmlDocument = excel.parseSpreadSheet(spreadsheet);

		//Write document
		return xmlDocument.writeDocument();
	}
}
