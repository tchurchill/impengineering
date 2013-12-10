package com.xtencilLauncher.model;

import java.io.Serializable;

public class XLProperties implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String inputDirectory;
	private String outputFile;

	public XLProperties() {
		super();
	}

	public String getInputDirectory() {
		return inputDirectory;
	}

	public void setInputDirectory(String inputDirectory) {
		this.inputDirectory = inputDirectory;
	}
	
	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}
	
}
