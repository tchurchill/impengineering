package com.xtencilLauncher.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class ConsoleParser {
	private File consoleFile = null;
	private ArrayList<String> vendorXrefs;
	private ArrayList<String> dataXrefs;
	private ArrayList<String> turnaroundXrefs;
	private ArrayList<String> sequenceXrefs;
	private ArrayList<String> readParse;
	
	
	public ConsoleParser() {
		consoleFile = new File("C:\\FI4\\console.txt");
		if (!consoleFile.exists()) {
			consoleFile = null;
			System.out.println("File \"C:\\FI4\\console.txt\" could not be found.");
		}

		vendorXrefs = new ArrayList<String>();
		dataXrefs = new ArrayList<String>();
		turnaroundXrefs = new ArrayList<String>();
		sequenceXrefs = new ArrayList<String>();
		readParse = new ArrayList<String>();
		
		vendorXrefs.add("Vendor Xrefs:");
		dataXrefs.add("Data Xrefs:");
		turnaroundXrefs.add("Turnaround Xrefs:");
		sequenceXrefs.add("Sequence Xrefs:");
	}
	
	public void parseFile() throws FileNotFoundException {
		Scanner input = new Scanner(consoleFile);
		int xrefNumber = 0;
		boolean scrapeDBUG = false;
		String previousLine = null;
		while (input.hasNext()) {
			String thisLine = input.nextLine();
			if (thisLine.contains("vendor_xref")) {
				vendorXrefs.add("Xref Number " + Integer.toString(++xrefNumber) + " - " +thisLine);
			} else if (thisLine.contains("data_xref")) {
				dataXrefs.add("Xref Number " + Integer.toString(++xrefNumber) + " - " +thisLine);
			} else if (thisLine.contains("turnaround_xref")) {
				turnaroundXrefs.add("Xref Number " + Integer.toString(++xrefNumber) + " - " +thisLine);
			} else if (thisLine.contains("sequence_xref")) {
				sequenceXrefs.add("Xref Number " + Integer.toString(++xrefNumber) + " - " +thisLine);
			} else if (thisLine.contains("DocumentStart")){
				scrapeDBUG = true;
			} else if (thisLine.contains("end document") || thisLine.contains("Reached end of file") || thisLine.contains("sps.editdoc.jdoc.ResolverReport")){
				scrapeDBUG = false;
	// XML Resolver
			} else if (scrapeDBUG && thisLine.contains("DBUG:") && !thisLine.matches(".*DBUG: Creating.*Resolver")){ 
				readParse.add(toUpper(thisLine.replaceAll(".*?DBUG: (.*)", "$1")));
	// FlatFile Resolver
			} else if (scrapeDBUG && thisLine.contains("children from:")){
				readParse.add("\n" + toUpper(thisLine.replaceAll(".*? children from: (.*)", "$1")));
			} else if (scrapeDBUG && thisLine.contains("Reading field: ")){
				if (previousLine != null){
					readParse.add("\t" + toUpper(previousLine.replaceAll(".*?Reading field: (.*)", "$1") + "="));
				}
				previousLine = thisLine;
			} else if (scrapeDBUG && thisLine.contains("Read ")){
				readParse.add("\t" + toUpper(thisLine.replaceAll(".*?Read (.*)", "$1")));
				previousLine = null;
			}
		}
		input.close();
	}
	
	public void printElementsOfArrayList(ArrayList<String> inputList) {
		for (String element : inputList) {
			System.out.println(element);
		}
		System.out.println();
	}
	
	public void printAllXrefs() {
		PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream("C:\\FI4\\xrefs.txt"));
			System.setOut(out);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		printElementsOfArrayList(vendorXrefs);
		printElementsOfArrayList(dataXrefs);
		printElementsOfArrayList(turnaroundXrefs);
		printElementsOfArrayList(sequenceXrefs);
		out.close();
	}
	
	public void printReadInfo(){
		PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream("C:\\FI4\\readSideParsed.txt"));
			System.setOut(out);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		printElementsOfArrayList(readParse);
		out.close();
	}
	
	private String toUpper(String original){
		char[] stringArray = original.toCharArray();
		stringArray[0] = Character.toUpperCase(stringArray[0]);
		return new String(stringArray);
	}
}
