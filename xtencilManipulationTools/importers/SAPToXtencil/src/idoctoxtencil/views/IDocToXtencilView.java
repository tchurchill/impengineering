/*
 * IDocToXtencilView.java
 */

package idoctoxtencil.views;

import idoctoxtencil.IDocToXtencilApp;
import idoctoxtencil.controller.IdocParser;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.htmlcleaner.XPatherException;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * The application's main frame.
 */
public class IDocToXtencilView extends FrameView {

    public IDocToXtencilView(SingleFrameApplication app) {
        super(app);

        initComponents();
        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = IDocToXtencilApp.getApplication().getMainFrame();
            aboutBox = new IDocToXtencilAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        IDocToXtencilApp.getApplication().show(aboutBox);
    }

    @Action
    public void showFileChooser(){
            JFrame mainFrame = IDocToXtencilApp.getApplication().getMainFrame();
            fileChooser = new JFileChooser();
            int returnVal = fileChooser.showDialog(mainFrame, "select");

            if(returnVal == JFileChooser.APPROVE_OPTION){
                String file = fileChooser.getSelectedFile().getAbsolutePath();
                //System.out.println("The File is: "+file);
                logTextArea.setText("Input file selected: "+file);
                filePathField.setText(file);
                myFile = fileChooser.getSelectedFile();
            }
    }

    @Action
    public void showOutPathChooser(){
        JFrame mainFrame = IDocToXtencilApp.getApplication().getMainFrame();

        JFileChooser outPathChooser = new JFileChooser();
        outPathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnVal = outPathChooser.showDialog(mainFrame, "select");

        if(returnVal == JFileChooser.APPROVE_OPTION){
            String fileDir = outPathChooser.getSelectedFile().getAbsolutePath();
            logTextArea.append("\nOutput Directory: "+fileDir);
            outPathField.setText(fileDir);
        }
    }

    @Action
    public void parseFileRequest(){

        if(xtlDirection == null){
            xtlDirection = "R";
        }

        if(groupPrefix == null){
            groupPrefix = "E1";
        }

        if(fileFormat == null){
            fileFormat = "XML";
        }

        if(xtlNameField.getText().length() < 1){
            JOptionPane.showMessageDialog(mainPanel, "Please enter an Xtencil name", null, JOptionPane.INFORMATION_MESSAGE);
            xtlNameField.requestFocusInWindow();

        }else  {
            xtlName = xtlNameField.getText();
        }

        if(ediDocNumField.getText().length() < 1){
            JOptionPane.showMessageDialog(mainPanel, "Please enter an EDI Document Number", null, JOptionPane.INFORMATION_MESSAGE);
            ediDocNumField.requestFocusInWindow();
        }else{
            ediDocNum = ediDocNumField.getText();
        }

        if(outPathField.getText().length() < 1 ){
            JOptionPane.showMessageDialog(mainPanel, "Please enter an EDI Document Number", null, JOptionPane.INFORMATION_MESSAGE);
            outPathField.requestFocusInWindow();
        }else{
            outPath = outPathField.getText();
        }

        if(xtlName != null && ediDocNum != null && outPath != null){
            logTextArea.append("\nXtencil Name: "+xtlName+".xtl");
            logTextArea.append("\nEDI Doc Number: "+ediDocNum);
            logTextArea.append("\nDirection: "+xtlDirection);
            logTextArea.append("\nGroup Prefix: "+groupPrefix);
            logTextArea.append("\nFile Format: "+fileFormat);
            logTextArea.append("\nWorking....");
            parseFile();
        }
    }

    private void parseFile(){

        String parseStatus = null;

        IdocParser iParse = new IdocParser();
        try {
            parseStatus = iParse.processFile(myFile, xtlDirection, xtlName, ediDocNum, groupPrefix, outPath, fileFormat);
        } catch (IOException ex) {
            Logger.getLogger(IDocToXtencilView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XPatherException ex) {
            Logger.getLogger(IDocToXtencilView.class.getName()).log(Level.SEVERE, null, ex);
        }

        logTextArea.append("\n"+parseStatus);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        processButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        filePathField = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        xtlDirectionSeletor = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        grpPrefixSelector = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        xtlNameField = new javax.swing.JTextField();
        status = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        ediDocNumField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        outPathField = new javax.swing.JTextField();
        outPathButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        fileFormatComboBox = new javax.swing.JComboBox();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(idoctoxtencil.IDocToXtencilApp.class).getContext().getActionMap(IDocToXtencilView.class, this);
        processButton.setAction(actionMap.get("parseFileRequest")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(idoctoxtencil.IDocToXtencilApp.class).getContext().getResourceMap(IDocToXtencilView.class);
        processButton.setText(resourceMap.getString("processButton.text")); // NOI18N
        processButton.setName("processButton"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        filePathField.setText(resourceMap.getString("filePathField.text")); // NOI18N
        filePathField.setName("filePathField"); // NOI18N

        jButton2.setAction(actionMap.get("showFileChooser")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N

        xtlDirectionSeletor.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Read", "Write" }));
        xtlDirectionSeletor.setAction(actionMap.get("changeXtlDirection")); // NOI18N
        xtlDirectionSeletor.setName("xtlDirectionSeletor"); // NOI18N
        xtlDirectionSeletor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xtlDirChange(evt);
            }
        });

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        grpPrefixSelector.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "E1", "E2" }));
        grpPrefixSelector.setName("grpPrefixSelector"); // NOI18N
        grpPrefixSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xtlGroupPrefixChange(evt);
            }
        });

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        xtlNameField.setText(resourceMap.getString("xtlNameField.text")); // NOI18N
        xtlNameField.setName("xtlNameField"); // NOI18N

        status.setText(resourceMap.getString("status.text")); // NOI18N
        status.setName("status"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        ediDocNumField.setText(resourceMap.getString("ediDocNumField.text")); // NOI18N
        ediDocNumField.setName("ediDocNumField"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        outPathField.setText(resourceMap.getString("outPathField.text")); // NOI18N
        outPathField.setName("outPathField"); // NOI18N

        outPathButton.setAction(actionMap.get("showOutPathChooser")); // NOI18N
        outPathButton.setText(resourceMap.getString("outPathButton.text")); // NOI18N
        outPathButton.setName("outPathButton"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        logTextArea.setColumns(20);
        logTextArea.setRows(5);
        logTextArea.setName("logTextArea"); // NOI18N
        jScrollPane1.setViewportView(logTextArea);

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        fileFormatComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "XML", "FlatFile" }));
        fileFormatComboBox.setName("fileFormatComboBox"); // NOI18N
        fileFormatComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileFormatSelectionChange(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap(522, Short.MAX_VALUE)
                .addComponent(processButton)
                .addGap(63, 63, 63))
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 610, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel1)
                            .addComponent(jLabel6)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fileFormatComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(outPathField)
                                    .addComponent(filePathField, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton2)
                                    .addComponent(outPathButton)))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(ediDocNumField, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(xtlNameField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                                    .addComponent(xtlDirectionSeletor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(grpPrefixSelector, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(104, 104, 104)
                                .addComponent(status, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(150, 150, 150)
                        .addComponent(jLabel3))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(filePathField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(outPathField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(outPathButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(xtlNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ediDocNumField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(xtlDirectionSeletor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2)))
                            .addComponent(status, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(grpPrefixSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(fileFormatComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(processButton)
                .addGap(6, 6, 6))
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 654, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 484, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void xtlDirChange(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xtlDirChange
        xtlDirection = xtlDirectionSeletor.getSelectedItem().toString();
    }//GEN-LAST:event_xtlDirChange

    private void xtlGroupPrefixChange(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xtlGroupPrefixChange
        groupPrefix = grpPrefixSelector.getSelectedItem().toString();

    }//GEN-LAST:event_xtlGroupPrefixChange

    private void fileFormatSelectionChange(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileFormatSelectionChange
        fileFormat = fileFormatComboBox.getSelectedItem().toString();
    }//GEN-LAST:event_fileFormatSelectionChange


   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ediDocNumField;
    private javax.swing.JComboBox fileFormatComboBox;
    private javax.swing.JTextField filePathField;
    private javax.swing.JComboBox grpPrefixSelector;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea logTextArea;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton outPathButton;
    private javax.swing.JTextField outPathField;
    private javax.swing.JButton processButton;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel status;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JComboBox xtlDirectionSeletor;
    private javax.swing.JTextField xtlNameField;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;
    private JFileChooser fileChooser;
    private File myFile;
    private String xtlDirection;
    private String xtlName;
    private String groupPrefix;
    private String ediDocNum;
    private String outPath;
    private String fileFormat;

}
