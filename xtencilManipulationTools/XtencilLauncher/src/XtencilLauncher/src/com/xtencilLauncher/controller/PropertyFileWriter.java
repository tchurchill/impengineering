package com.xtencilLauncher.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import com.xtencilLauncher.model.FIProperties;

public class PropertyFileWriter {
	
	private File filePath = null;
	private PrintWriter printWriter = null;
	private FIProperties fiProperties = null;
	
	public enum TestSystem{
		BLADE,PREPROD,DC4;
	}

	public PropertyFileWriter(String filePath, FIProperties fiProperties) throws FileNotFoundException {
		super();
		this.filePath = new File(filePath);
		this.printWriter = new PrintWriter(this.filePath);
		this.fiProperties = fiProperties;
	}
	
	public void writePropertiesForSystem(TestSystem system){
		writeProperties();
		writeSystemProperty(system);
	}
	
	
	private void writeProperties(){
		for (String property : fiProperties.getPropertiesList()) {
			printWriter.write(property+"\r\n\r\n");
		}
	}
	
	private void writeSystemProperty(TestSystem system){
		switch (system) {
		case BLADE:
			printWriter.write(fiProperties.getBladeXTS_Entries());
			break;
		case PREPROD:
			printWriter.write(fiProperties.getPreprodXTS_Entries());
			break;
		case DC4:
			printWriter.write(fiProperties.getProdXTS_Entries());
			break;
		default:
			break;
		}
	}
	
	public void closePropertyFile(){
		printWriter.close();
	}

	
}
