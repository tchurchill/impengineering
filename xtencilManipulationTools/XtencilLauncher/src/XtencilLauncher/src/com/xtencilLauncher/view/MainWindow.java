package com.xtencilLauncher.view;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.UIManager;

import com.xtencilLauncher.model.FIProperties;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import java.awt.event.ActionEvent;
import javax.swing.JProgressBar;
import java.awt.Font;
import java.awt.Color;

public class MainWindow extends JFrame {
	private class BtnSelectActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JMenuBar menuBar;
	private JMenu mnFile;
	public JMenuItem mntmPreferences;
	public JMenuItem mntmExit;
	private JLabel lblSender;
	public JComboBox senderComboBox;
	private JLabel lblReceiver;
	public JComboBox receiverComboBox;
	private JLabel lblDatabase;
	public JComboBox databaseSelectComboBox;
	private JLabel lblInFileType;
	public JRadioButton inFlatFileRadio;
	public JRadioButton inXmlRadio;
	public JRadioButton inFedsRadio;
	private JLabel lblOutFileType;
	public JRadioButton outXmlRadio;
	public JRadioButton outFedsRadio;
	public JRadioButton outFlatFileRadio;
	private JLabel lblInputFile;
	public JTextField inputFileTextField;
	public JButton fileSelectButton;
	private JLabel lblJarFile;
	public JComboBox jarComboBox;
	public JButton runButton;
	public JProgressBar progressBar;
	public ButtonGroup inFileTypeGroup;
	public ButtonGroup outFileTypeGroup;
	private JLabel lblStatus;
	public JLabel statusLabel;
	private JLabel lblOutputFile;
	private JTextField outputFileTextField;
	private JButton outputFileSaveButton;
	private JMenu mnJars;
	private JMenuItem mntmRefresh;
	private JMenuItem mntmDeleteAllJars;
	private JMenuItem mntmAbortRun;
	private JMenuItem mntmParseXrefs;
	private JMenuItem mntmParseRead;
	private JLabel lblDebug;
	public JCheckBox chkDebug;

	/**
	 * Create the frame.
	 */
	public MainWindow(FIProperties fiProps) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 525, 327);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 584, 21);
		contentPane.add(menuBar);

		mnFile = new JMenu("File");
		mnFile.setMnemonic(KeyEvent.VK_F);
		menuBar.add(mnFile);

		mntmPreferences = new JMenuItem("Preferences");
		mntmPreferences.setMnemonic(KeyEvent.VK_P);
		mnFile.add(mntmPreferences);
		
		mntmAbortRun = new JMenuItem("Abort Run");
		mntmAbortRun.setMnemonic(KeyEvent.VK_A);
		mnFile.add(mntmAbortRun);
		
		mntmParseXrefs = new JMenuItem("Parse Xrefs From Console");
		mntmParseXrefs.setMnemonic(KeyEvent.VK_X);
		mnFile.add(mntmParseXrefs);

		mntmParseRead = new JMenuItem("Parse Read Info From Console");
		mntmParseRead.setMnemonic(KeyEvent.VK_R);
		mnFile.add(mntmParseRead);

		mntmExit = new JMenuItem("Exit");
		mntmExit.setMnemonic(KeyEvent.VK_E);
		mnFile.add(mntmExit);
		
		mnJars = new JMenu("Jars");
		mnJars.setMnemonic(KeyEvent.VK_J);
		menuBar.add(mnJars);
		
		mntmRefresh = new JMenuItem("Refresh");
		mntmRefresh.setMnemonic(KeyEvent.VK_R);
		mnJars.add(mntmRefresh);
		
		mntmDeleteAllJars = new JMenuItem("Delete All Jars");
		mntmDeleteAllJars.setMnemonic(KeyEvent.VK_D);
		mnJars.add(mntmDeleteAllJars);
		
		lblSender = new JLabel("Sender: ");
		lblSender.setBounds(10, 32, 46, 14);
		contentPane.add(lblSender);
		
		senderComboBox = new JComboBox();
		senderComboBox.setEditable(true);
		senderComboBox.setBounds(96, 32, 224, 20);
		contentPane.add(senderComboBox);
		
		lblReceiver = new JLabel("Receiver:");
		lblReceiver.setBounds(10, 57, 46, 14);
		contentPane.add(lblReceiver);
		
		receiverComboBox = new JComboBox();
		receiverComboBox.setEditable(true);
		receiverComboBox.setBounds(96, 57, 224, 20);
		contentPane.add(receiverComboBox);
		
		lblDatabase = new JLabel("Database:");
		lblDatabase.setBounds(10, 82, 59, 14);
		contentPane.add(lblDatabase);
		
		databaseSelectComboBox = new JComboBox();
		databaseSelectComboBox.setBounds(96, 85, 96, 20);
		databaseSelectComboBox.addItem("Blade");
		databaseSelectComboBox.addItem("PreProduction");
		databaseSelectComboBox.addItem("Production");
		contentPane.add(databaseSelectComboBox);
		
		lblDebug = new JLabel("Full Debug:");
		lblDebug.setBounds(245, 82, 245, 27);
		contentPane.add(lblDebug);
		
		chkDebug = new JCheckBox();
		chkDebug.setBounds(300, 85, 300, 20);
		chkDebug.setMnemonic(KeyEvent.VK_G);
		contentPane.add(chkDebug);
		
		lblInFileType = new JLabel("In File Type:");
		lblInFileType.setBounds(10, 116, 71, 14);
		contentPane.add(lblInFileType);
		
		inFlatFileRadio = new JRadioButton("FlatFile");
		inFlatFileRadio.setBounds(96, 112, 71, 23);
		inFlatFileRadio.setMnemonic(KeyEvent.VK_A);
		contentPane.add(inFlatFileRadio);
		
		inXmlRadio = new JRadioButton("XML");
		inXmlRadio.setBounds(169, 112, 51, 23);
		inXmlRadio.setMnemonic(KeyEvent.VK_X);
		contentPane.add(inXmlRadio);
		
		inFedsRadio = new JRadioButton("FEDS");
		inFedsRadio.setBounds(230, 112, 59, 23);
		inFedsRadio.setMnemonic(KeyEvent.VK_E);
		contentPane.add(inFedsRadio);
		
		inFileTypeGroup = new ButtonGroup();
		inFileTypeGroup.add(inFedsRadio);
		inFileTypeGroup.add(inFlatFileRadio);
		inFileTypeGroup.add(inXmlRadio);
		
		lblOutFileType = new JLabel("Out File Type:");
		lblOutFileType.setBounds(10, 145, 71, 14);
		contentPane.add(lblOutFileType);
		
		outXmlRadio = new JRadioButton("XML");
		outXmlRadio.setBounds(169, 142, 51, 23);
		outXmlRadio.setMnemonic(KeyEvent.VK_M);
		contentPane.add(outXmlRadio);
		
		outFedsRadio = new JRadioButton("FEDS");
		outFedsRadio.setBounds(230, 142, 59, 23);
		outFedsRadio.setMnemonic(KeyEvent.VK_D);
		contentPane.add(outFedsRadio);
		
		outFlatFileRadio = new JRadioButton("FlatFile");
		outFlatFileRadio.setBounds(96, 142, 71, 23);
		outFlatFileRadio.setMnemonic(KeyEvent.VK_I);
		contentPane.add(outFlatFileRadio);
		
		outFileTypeGroup = new ButtonGroup();
		outFileTypeGroup.add(outFedsRadio);
		outFileTypeGroup.add(outFlatFileRadio);
		outFileTypeGroup.add(outXmlRadio);
		
		lblInputFile = new JLabel("Input File:");
		lblInputFile.setBounds(10, 170, 59, 14);
		contentPane.add(lblInputFile);
		
		inputFileTextField = new JTextField();
		inputFileTextField.setBounds(96, 167, 296, 20);
		contentPane.add(inputFileTextField);
		inputFileTextField.setColumns(10);
		
		fileSelectButton = new JButton("Select File");
		fileSelectButton.addActionListener(new BtnSelectActionListener());
		fileSelectButton.setBounds(402, 166, 89, 23);
		fileSelectButton.setMnemonic(KeyEvent.VK_C);
		contentPane.add(fileSelectButton);
		
		lblJarFile = new JLabel("Jar File:");
		lblJarFile.setBounds(10, 228, 46, 14);
		contentPane.add(lblJarFile);
		
		jarComboBox = new JComboBox();
		jarComboBox.setBounds(96, 225, 296, 20);
		contentPane.add(jarComboBox);
		
		runButton = new JButton("Run");
		runButton.setBounds(402, 224, 89, 23);
		runButton.setMnemonic(KeyEvent.VK_R);
		contentPane.add(runButton);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(345, 253, 146, 14);
		contentPane.add(progressBar);
		
		lblStatus = new JLabel("Status:");
		lblStatus.setBounds(10, 253, 46, 14);
		contentPane.add(lblStatus);
		
		statusLabel = new JLabel("Idle");
		statusLabel.setForeground(Color.BLUE);
		statusLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		statusLabel.setBounds(96, 253, 71, 21);
		contentPane.add(statusLabel);
		
		lblOutputFile = new JLabel("Output File:");
		lblOutputFile.setBounds(10, 199, 59, 14);
		contentPane.add(lblOutputFile);
		
		outputFileTextField = new JTextField();
		outputFileTextField.setColumns(10);
		outputFileTextField.setBounds(96, 196, 296, 20);
		contentPane.add(outputFileTextField);
		
		outputFileSaveButton = new JButton("Save As");
		outputFileSaveButton.setBounds(402, 195, 89, 23);
		outputFileSaveButton.setMnemonic(KeyEvent.VK_V);
		contentPane.add(outputFileSaveButton);
		
		this.setVisible(true);
	}
	
	public JMenuItem getMntmAbortRun() {
		return mntmAbortRun;
	}
	
	public JMenuItem getMntmParseXrefs() {
		return mntmParseXrefs;
	}

	public JMenuItem getMntmParseRead() {
		return mntmParseRead;
	}

	public JMenuItem getMntmRefresh() {
		return mntmRefresh;
	}

	public JTextField getOutputFileTextField() {
		return outputFileTextField;
	}

	public JButton getOutputFileSaveButton() {
		return outputFileSaveButton;
	}

	public void showAlert(String message){
		JOptionPane.showMessageDialog(this,message);
	}
	
	public JMenuItem getMntmDeleteAllJars() {
		return mntmDeleteAllJars;
	}

	public void addActionListnerToMenuItem(ActionListener al,JMenuItem menuItem){
		menuItem.addActionListener(al);
	}
	
	public void addComboBoxListener(JComboBox comboBox, KeyListener kl){
		comboBox.getEditor().getEditorComponent().addKeyListener(kl);
	}
	
	public void addButtonActionListener(JButton jButton, ActionListener al){
		jButton.addActionListener(al);
	}
	
}
