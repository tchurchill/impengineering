package com.xtencilLauncher.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

public class MapProcessor implements Runnable {

	ProcessBuilder processBuilder;
	String processDirectoryPath;
	Process process;
	XtencilLauncherController xlc;
	OutputStream outStream;
	
	
	public MapProcessor(XtencilLauncherController xlc, String inputType, String inputFile, String outType, String outputFile, String jarName) {
		super();
		String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "javaw.exe";
        String classpath = "lib/fi.jar;lib/spsApp.jar;lib/Xtencil.jar;lib/spsUtil.jar;lib/spsXml.jar;lib/spsIo.jar;/FI4/lib/spsResolver.jar;/FI4/lib/spsPersistence.jar;/FI4/lib/spsLocation.jar;/FI4/lib/spsItemMaster.jar";

		this.processBuilder = new ProcessBuilder(javaBin, "-Xms1024M","-XX:MaxPermSize=256M","-Xmx1024M","-cp", classpath,"sps.fi.MapDriver", inputType, inputFile, outType, outputFile, jarName);
		this.processBuilder.directory(new File("C:\\FI4"));
		this.processBuilder.redirectErrorStream(true);
		this.xlc = xlc;
		
		
		/*** IMPORTANT SECTION FOR MESSING WITH THE CONSOLE OUTPUT!! ***/
		try {
			PrintStream out = new PrintStream(new FileOutputStream("C:\\FI4\\console.txt"));
			System.setOut(out);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	
	public void stopMap(){
		process.destroy();
	}

	@Override
	public  void run() {
		try {
			process = processBuilder.start();
			
			InputStream is = process.getInputStream();
			 			 
			 InputStreamReader isr = new InputStreamReader(is);
		       BufferedReader br = new BufferedReader(isr);
		       String line;
		       while ((line = br.readLine()) != null) {
		           System.out.println(line);
		         }

			int exitValue = process.waitFor();
			xlc.updateFromMapProcessor(new Integer(exitValue));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
}
