package com.xtencilLauncher.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class XLLauncherBatWriter {
	private File filePath = null;
	private PrintWriter printWriter = null;
	
	
	public XLLauncherBatWriter(String filePath) throws FileNotFoundException {
		super();
		this.filePath = new File(filePath);
		this.printWriter = new PrintWriter(this.filePath);
	}
	
	public void printFileWithOptions(String inputType, String outputType, String inputFile, String outputFile, String jarName){
		printWriter.write("echo off\r\n");
		printWriter.write("bin\\win\\map.bat > console.txt 2>&1"+inputType+" "+"\""+inputFile+"\""+" "+outputType+" "+outputFile+" "+jarName);
		printWriter.close();
	}
	
}
