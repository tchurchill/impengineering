package com.xtencilLauncher.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class XLConsoleBatWriter {
	private File filePath = null;
	private PrintWriter printWriter = null;
	
	
	public XLConsoleBatWriter(String filePath) throws FileNotFoundException {
		super();
		this.filePath = new File(filePath);
		this.printWriter = new PrintWriter(this.filePath);
	}
	
	public void printFile(){
		printWriter.write("echo off\r\n");
		printWriter.write("xdLauncher.bat > console.txt 2>&1");
		printWriter.close();
	}
	
}
