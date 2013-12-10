package com.xtencilLauncher.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.activation.MimetypesFileTypeMap;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import oracle.jdbc.OracleDriver;

import com.xtencilLauncher.controller.PropertyFileWriter.TestSystem;
import com.xtencilLauncher.model.FIProperties;
import com.xtencilLauncher.model.XLProperties;
import com.xtencilLauncher.view.MainWindow;
import com.xtencilLauncher.view.PreferencesUI;

public class XtencilLauncherController {

	private FIProperties fiProperties;
	private MainWindow mainWindow;
	private PreferencesUI preferencesUI;
	private JFileChooser fileChooser;
	private File inputFile;
	private XLProperties xlProperties;
	private ArrayList<File> jarFiles = new ArrayList<File>();
	private volatile MapProcessor mapProcessor = null;
	
	
	private volatile Thread altThread;
	
	public XtencilLauncherController(FIProperties fiProperties,
			MainWindow mainWindow, PreferencesUI prefUI) {
		super();
		
		checkForPropertiesFile();
		
		this.fiProperties = fiProperties;
		this.mainWindow = mainWindow;
		this.preferencesUI = prefUI;
		this.preferencesUI.addButtonActionListener(this.preferencesUI.getBtnSave(), new PreferencesSaveActionListener());
		
		this.mainWindow.getOutputFileTextField().setText(xlProperties.getOutputFile());
		this.mainWindow.addActionListnerToMenuItem(new ExitProgramListener(),this.mainWindow.mntmExit);
		this.mainWindow.addActionListnerToMenuItem(new ViewPreferenceUIActionListener(),this.mainWindow.mntmPreferences);
		this.mainWindow.addActionListnerToMenuItem(new RefreshJarListener(), this.mainWindow.getMntmRefresh());
		this.mainWindow.addActionListnerToMenuItem(new DeleteJarsListener(), this.mainWindow.getMntmDeleteAllJars());
		
		this.mainWindow.addComboBoxListener(this.mainWindow.senderComboBox, new ComboBoxEditorListener(this.mainWindow.senderComboBox));
		this.mainWindow.addComboBoxListener(this.mainWindow.receiverComboBox, new ComboBoxEditorListener(this.mainWindow.receiverComboBox));
		
		this.mainWindow.addButtonActionListener(this.mainWindow.fileSelectButton,new SelectInputFileListener());
		this.mainWindow.addButtonActionListener(this.mainWindow.getOutputFileSaveButton(), new SelectOutputFileNameListener());
		this.mainWindow.addButtonActionListener(this.mainWindow.runButton, new RunButtonActionListener());
		this.mainWindow.addActionListnerToMenuItem(new AbortButtonListener(),this.mainWindow.getMntmAbortRun());
		this.mainWindow.addActionListnerToMenuItem(new XrefPrintListener(),this.mainWindow.getMntmParseXrefs());
		this.mainWindow.addActionListnerToMenuItem(new ReadPrintListener(),this.mainWindow.getMntmParseRead());
		loadJars();
		/*
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				pollForJars();
			}
		}, new Date(), 5000);*/
		
	}
	
	private void checkForPropertiesFile(){
		File propertyFileDirectory = new File("c:/FI4/XtencilLauncher");
		File propertyFile = new File("c:/FI4/XtencilLauncher/properties.xlp");
		if(!propertyFileDirectory.isDirectory()){
			if(!createPropertiesDirectory(propertyFileDirectory)){
				mainWindow.showAlert("Could not create XL direcotry in c:\\FI4");
			}
		}
		if(propertyFile.isFile()){
			readPropertiesFile(propertyFile);
		}else{
			xlProperties = new XLProperties();
		}
	}
	
	private Boolean createPropertiesDirectory(File file){
        return file.mkdir();
	}
	
	private Boolean readPropertiesFile(File file){
		try {
			ObjectInputStream infile = new ObjectInputStream(new FileInputStream(file));
			xlProperties = (XLProperties)infile.readObject();
			infile.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private class PreferencesSaveActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			xlProperties.setInputDirectory(preferencesUI.getDefaultInputFileTextField().getText());
			xlProperties.setOutputFile(preferencesUI.getDefaultOutputFileTextField().getText());
			preferencesUI.dispose();
			
			try {
				ObjectOutputStream outfile = new ObjectOutputStream(new FileOutputStream("c:/FI4/XtencilLauncher/properties.xlp"));
				outfile.writeObject(xlProperties);
				outfile.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public XLProperties getXlProperties() {
		return xlProperties;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}
	
	
	private class RunButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			if(mainWindow.inFileTypeGroup.getSelection() == null){
				mainWindow.showAlert("Please select a In File Type.");
			}else if(mainWindow.outFileTypeGroup.getSelection() == null){
				mainWindow.showAlert("Please select a Out File Type.");
			}else if(inputFile==null && mainWindow.inputFileTextField.getText().length()==0){
				mainWindow.showAlert("Please select Input File.");
			}else if(mainWindow.getOutputFileTextField().getText().length()==0){
				mainWindow.showAlert("Please select Output File.");
			}else{
				mainWindow.progressBar.setIndeterminate(true);
				mainWindow.statusLabel.setText("Processing");
				
				fiProperties.clearStandardProperties();
				if(checkComboBoxForData(mainWindow.senderComboBox)){
					fiProperties.addProperty("sender="+(String)mainWindow.senderComboBox.getSelectedItem());
				} else {
					fiProperties.addProperty("sender=");
				}
				
				if(checkComboBoxForData(mainWindow.receiverComboBox)){
					fiProperties.addProperty("receiver="+ mainWindow.receiverComboBox.getSelectedItem());
				} else {
					fiProperties.addProperty("receiver=");
				}
				
				if (mainWindow.chkDebug.isSelected()) {
					fiProperties.addProperty("sps.dbug.opts=true");
				} else {
					fiProperties.addProperty("sps.dbug.opts=false");
				}
				fiProperties.addStandardProperties();

				runMap();
				
				
			}	
		}
	}
	
	private Boolean checkComboBoxForData(JComboBox combobox){
        return combobox.getSelectedItem() != null && ((String) combobox.getSelectedItem()).length() > 0;
    }
	
	private void runMap(){
		
		try {
			PropertyFileWriter propertyFileWrite = new PropertyFileWriter("C:\\FI4\\conf\\fi.properties", fiProperties);
			if(mainWindow.databaseSelectComboBox.getSelectedItem().toString().equals("Blade")){
				propertyFileWrite.writePropertiesForSystem(TestSystem.BLADE);
			}else if(mainWindow.databaseSelectComboBox.getSelectedItem().toString().equals("PreProduction")){
				propertyFileWrite.writePropertiesForSystem(TestSystem.PREPROD);
			}else if(mainWindow.databaseSelectComboBox.getSelectedItem().toString().equals("Production")){
				propertyFileWrite.writePropertiesForSystem(TestSystem.DC4);
			}
			propertyFileWrite.closePropertyFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		String inputType = "";
		if(mainWindow.inXmlRadio.isSelected()){
			inputType = "XML";
		}else if(mainWindow.inFedsRadio.isSelected()){
			inputType = "FEDS";
		}else if(mainWindow.inFlatFileRadio.isSelected()){
			inputType = "APP";
		}
		
		String outputType = "";
		if(mainWindow.outXmlRadio.isSelected()){
			outputType = "XML";
		}else if(mainWindow.outFedsRadio.isSelected()){
			outputType = "FEDS";
		}else if(mainWindow.outFlatFileRadio.isSelected()){
			outputType = "APP";
		}
				
		mapProcessor = new MapProcessor(this, inputType, mainWindow.inputFileTextField.getText(), outputType, mainWindow.getOutputFileTextField().getText(), (String)mainWindow.jarComboBox.getSelectedItem());
		altThread = new Thread(mapProcessor);
		altThread.start();
	
	}
	
	public void updateFromMapProcessor(Object obj){
		if(obj instanceof Integer){
			mapProcessor = null;
			altThread = null;
			int exitValue = (Integer)obj;
			if(exitValue == 0){
				mainWindow.progressBar.setIndeterminate(false);
				mainWindow.statusLabel.setText("Completed");
			}else {
				mainWindow.progressBar.setIndeterminate(false);
				mainWindow.statusLabel.setText("Completed");
				mainWindow.showAlert("An Error occurred see console for details.");
			}
		}
	}
	
	private class AbortButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if(mapProcessor != null){
				mainWindow.progressBar.setIndeterminate(false);
				mainWindow.statusLabel.setText("Stopped");
				mapProcessor.stopMap();
			}
		}
	}
	
	private class XrefPrintListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			ConsoleParser consoleParser = new ConsoleParser();
			try {
				consoleParser.parseFile();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			
			consoleParser.printAllXrefs();
		}
	}

	private class ReadPrintListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			ConsoleParser consoleParser = new ConsoleParser();
			try {
				consoleParser.parseFile();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			
			consoleParser.printReadInfo();
		}
	}

	/*
	private void loadJars(long delay){
		
		File folder = new File("c:/FIMaps");
        File[] listOfFiles = folder.listFiles();		
        final ArrayList<File> arrayOfFiles = new ArrayList<File>();
        arrayOfFiles.clear();
        for(File file : listOfFiles){
        	String mimeType = new MimetypesFileTypeMap().getContentType(file);
        	if(mimeType.equals("application/octet-stream")){
        		arrayOfFiles.add(file);
        	}	
		}
        Timer timer = new Timer();
    	timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				refreshJars(arrayOfFiles);
			}
		}, delay);
	}
	*/
	private void loadJars(){
		File folder = new File("c:/FIMaps");
        for(File file : folder.listFiles()){
        	String mimeType = new MimetypesFileTypeMap().getContentType(file);
        	if(mimeType.equals("application/octet-stream")){
        		jarFiles.add(file);
        	}	
		}
        refreshJars();
	}
	
	private void refreshJars(){
		ArrayList<String> tempList = new ArrayList<String>();
		for(File file : jarFiles){
			try {
				JarFile jar = new JarFile(file);
				Enumeration<JarEntry> jarEnum = jar.entries();
				while (jarEnum.hasMoreElements()) {
					java.util.jar.JarEntry enumFile = jarEnum.nextElement();
					if(enumFile.getName().contains("SPSManifest.mf")){						
						InputStream is = jar.getInputStream(enumFile);
						BufferedReader br = new BufferedReader(new InputStreamReader(is));						
						String strLine;
						while((strLine = br.readLine()) != null){
							if(strLine.startsWith("fullyQualifiedJavaName=") && !tempList.contains(strLine.substring(strLine.indexOf("=")+1))){
								tempList.add(strLine.substring(strLine.indexOf("=")+1));
							}
						}
					}
				}
				jar.close();
			} catch (IOException e) {
				mainWindow.showAlert(e.getMessage());
			}
		}
		mainWindow.jarComboBox.removeAllItems();
		for (String string : tempList) {
			mainWindow.jarComboBox.addItem(string);
		}
	}

	
	private class SelectInputFileListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
            fileChooser = new JFileChooser(xlProperties.getInputDirectory());
            int returnVal = fileChooser.showDialog(mainWindow, "select");
            if(returnVal == JFileChooser.APPROVE_OPTION){
                String file = fileChooser.getSelectedFile().getAbsolutePath();
                mainWindow.inputFileTextField.setText(file);
                setInputFile(fileChooser.getSelectedFile());
            }
		}
	}
	
	private class SelectOutputFileNameListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser outputFileChooser = new JFileChooser(xlProperties.getOutputFile());
			int returnVal = outputFileChooser.showSaveDialog(mainWindow);
			if(returnVal == JFileChooser.APPROVE_OPTION){
				String file = outputFileChooser.getSelectedFile().getAbsolutePath();
				mainWindow.getOutputFileTextField().setText(file);
			}
		}
		
	}
	
	private class ExitProgramListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}
	
	private class ViewPreferenceUIActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			preferencesUI.getDefaultInputFileTextField().setText(xlProperties.getInputDirectory());
			preferencesUI.getDefaultOutputFileTextField().setText(xlProperties.getOutputFile());
			preferencesUI.setBladeSettingsTextArea(fiProperties.getBladeXTS_Entries());
			preferencesUI.setVisible(true);
			
		}
	}
	
	private class ComboBoxEditorListener extends KeyAdapter{
		private JComboBox comboBox;
		@Override
		public void keyPressed(KeyEvent e) {
			super.keyPressed(e);
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				String usr = "ngeorge";
		        String pwd = "ngeorge7";
		        String url = "jdbc:oracle:thin:@(DESCRIPTION=(LOAD_BALANCE=on)(ADDRESS=(PROTOCOL=tcp)(HOST=trckprime01.mps.spscommerce.net)(PORT=1521))(ADDRESS=(PROTOCOL=tcp)(HOST=trckprime02.mps.spscommerce.net)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=drtrck)))";
		       try {
		    	   DriverManager.registerDriver(new OracleDriver());
		    	   Connection conn = DriverManager.getConnection(url,usr,pwd);
		    	   String sql = "SELECT * FROM SPSCDC4.COMPANY where COMPANY_NAME like '"+comboBox.getEditor().getItem()+"%'";
		           Statement statement = conn.createStatement();
		           ResultSet rs = statement.executeQuery(sql);
		           comboBox.removeAllItems();
		           while (rs.next()){
		               //System.out.println("results: " + rs.getString(2));
		               comboBox.addItem(rs.getString(2));
		           }
		           conn.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		public ComboBoxEditorListener(JComboBox comboBox) {
			super();
			this.comboBox = comboBox;
		}
	}
	
	private class RefreshJarListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			loadJars();
		}
	}
	
	private class DeleteJarsListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(jarDeletionConfirmed()){
				jarFiles.clear();
				File folder = new File("c:/FIMaps");
		        File[] listOfFiles = folder.listFiles();
                if (listOfFiles != null) {
                    for (File file : listOfFiles) {
                        file.delete();
                    }
                }
                loadJars();
			}
		}
	}
	
	private Boolean jarDeletionConfirmed(){
		int response = JOptionPane.showConfirmDialog(mainWindow, "Are you sure you want to delete all Jars?", null, JOptionPane.YES_NO_OPTION);
        return response == JOptionPane.YES_OPTION;
    }
}



