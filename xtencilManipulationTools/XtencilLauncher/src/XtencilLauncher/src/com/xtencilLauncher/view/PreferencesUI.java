package com.xtencilLauncher.view;


import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;

public class PreferencesUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -874871167902122349L;
	private JPanel contentPane;
	private JTabbedPane tabbedPane;
	private JPanel panel;
	private JLabel lblDefaultInputFile;
	private JTextField defaultInputFileTextField;
	private JPanel panel_1;
	private JLabel label;
	private JTextArea bladeSettingsTextArea;
	private JButton btnSave;
	private JLabel lblDefaultOutputFile;
	private JTextField defaultOutputFileTextField;

	

	/**
	 * Create the frame.
	 */
	public PreferencesUI() {
		setTitle("Preferences");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 538, 405);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 11, 522, 356);
		contentPane.add(tabbedPane);
		
		panel = new JPanel();
		tabbedPane.addTab("Application", null, panel, null);
		panel.setLayout(null);
		
		lblDefaultInputFile = new JLabel("Default Input File Directory:");
		lblDefaultInputFile.setBounds(10, 11, 134, 14);
		panel.add(lblDefaultInputFile);
		
		defaultInputFileTextField = new JTextField();
		defaultInputFileTextField.setBounds(168, 5, 190, 20);
		panel.add(defaultInputFileTextField);
		defaultInputFileTextField.setColumns(10);
		
		btnSave = new JButton("Save");
		btnSave.setBounds(417, 291, 89, 23);
		panel.add(btnSave);
		
		lblDefaultOutputFile = new JLabel("Default Output File Directory:");
		lblDefaultOutputFile.setBounds(10, 42, 148, 14);
		panel.add(lblDefaultOutputFile);
		
		defaultOutputFileTextField = new JTextField();
		defaultOutputFileTextField.setColumns(10);
		defaultOutputFileTextField.setBounds(168, 36, 190, 20);
		panel.add(defaultOutputFileTextField);
		
		//Removing all explicit references to blade settings
		panel_1 = new JPanel();
		tabbedPane.addTab("Blade", null, panel_1, null);
		panel_1.setLayout(null);
		
		label = new JLabel("Blade Settings: ");
		label.setBounds(10, 10, 75, 14);
		panel_1.add(label);
		
		bladeSettingsTextArea = new JTextArea();
		bladeSettingsTextArea.setBounds(95, 5, 412, 89);
		panel_1.add(bladeSettingsTextArea);
	}
	
	public JTextField getDefaultInputFileTextField() {
		return defaultInputFileTextField;
	}

	public JButton getBtnSave() {
		return btnSave;
	}

	public void addButtonActionListener(JButton jb, ActionListener al){
		jb.addActionListener(al);
	}

	public JTextArea getBladeSettingsTextArea() {
		return bladeSettingsTextArea;
	}

	public void setBladeSettingsTextArea(String text) {
		this.bladeSettingsTextArea.setText(text);
	}

	public JTextField getDefaultOutputFileTextField() {
		return defaultOutputFileTextField;
	}
	
	
}
