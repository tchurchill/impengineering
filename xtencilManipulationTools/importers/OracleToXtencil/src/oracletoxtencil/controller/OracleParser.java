/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oracletoxtencil.controller;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import oracletoxtencil.model.*;


/**
 *
 * @author mburris
 */
public class OracleParser {
    
    private OracleDocument oracleDocument;
    private OracleDataTypes oracleDatatypes;
    
    //ParseFile
    public File parseFile(File file){

    	// Output file
		File tmpDir = new File(System.getProperty("user.home") + File.separator + "xtlTemp" + File.separator);
		tmpDir.mkdirs();
		File tmpFile = new File (tmpDir.getAbsolutePath() + File.separator + "tmp.xtl");

        oracleDatatypes = new OracleDataTypes();
        oracleDocument = null;
        try {
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            
            while ((strLine = br.readLine()) != null)   {
             
              String[] myValues = strLine.split("\\s+");
                
              //Find Doctype & Direction
              if(myValues.length > 6 && myValues[1].equals("Transaction:") && myValues[3].equals("Description:")){
                  OracleTransactions oracleTransactions = new OracleTransactions();
                  if (oracleTransactions.hasTransaction(myValues[2])) {
                      String[] transactionInfo = oracleTransactions.getTransaction(myValues[2]).split("\\|");
                      if(oracleDocument == null){
                            oracleDocument = new OracleDocument(transactionInfo[0], transactionInfo[1]);
                      }
                  }else{
                  }
                  
              }
              
              //Find Fields 
              if(myValues.length > 5 && oracleDatatypes.hasDatatype(myValues[5])){
                  //System.out.println("Finding Fields "+myValues[1]);
                  //Check Doc for group
                  String appendedName = "";
                  if(myValues[2].length()== 2){
                      appendedName = "00"+myValues[2];
                  }else{
                      appendedName = myValues[2];
                  }
                  
                  if(oracleDocument.hasGroup(appendedName)){
                     Group myGroup = oracleDocument.getGroup(appendedName);
                     Field myField = new Field(myValues[1],"1", myValues[4], oracleDatatypes.getDatetype(myValues[5]));
                     myGroup.addField(myField);
                  }else{
                     String groupJavaName = "g"+appendedName;
                     Group myGroup = new Group(appendedName, groupJavaName, "0", "1",myValues[7],myValues[8]);
                     Field myField = new Field(myValues[1],"1", myValues[4], oracleDatatypes.getDatetype(myValues[5]));
                     myGroup.addField(myField);
                     oracleDocument.addGroup(myGroup);    
                  }
              }
              
            }
            
        } catch (Exception e) {
            
        }
        
        
        try {
            
            //Sorted Groups
            Map sortedGroups = new TreeMap(oracleDocument.getGroups());
            
            //Write Output
            PrintWriter printWriter = new PrintWriter(new FileOutputStream(tmpFile));

            
            //Begin Writing Document
            printWriter.write("<?xml version='1.0' encoding='UTF-8'?>\n<!DOCTYPE SPSFILE SYSTEM 'C:\\XtencilDesigner\\form\\xtencil.dtd'>\n<SPSFILE name='SPS Commerce Xtencil' date='' fileType='FORM' contents='NORM'>\n<DOCUMENTDEF javaPackageName='' owner='' type='" + oracleDocument.getDocType() + "' revision='' barcode='N' xtencilType='Flat File' justification='Left' display='Y' enable='Y' print='Y' exportSource='N' direction='O' resend='N' >\n");

            // Add date format
            printWriter.write("<FORMAT>\n<FIELDFMT writeFormat='yyyyMMdd' maxLength='2147483647' minLength='0' justify='NONE' trim='NONE' dataType='DATE' name='all' keyType='all' desc='all'>yyyyMMdd</FIELDFMT>\n</FORMAT>\n");
            
            //Header
            printWriter.write("<GROUPDEF name='Header' javaName='header' isRecord='Y' type='' min='1' max='1'>\n");
            
            for (Iterator it = sortedGroups.entrySet().iterator(); it.hasNext();) {                
                Map.Entry myMap = (Map.Entry)it.next();
                Group myGroup = (Group)myMap.getValue();
                Double groupNumber = Double.parseDouble(myMap.getKey().toString());
                
                //print groupName
                String myQual = null;
                if(myGroup.getQualifier().length()>3){
                 myQual = myGroup.getQualifier().substring(0,3);   
                }else{
                  myQual = myGroup.getQualifier();
                }
                System.out.println("myHash.put(\""+myGroup.getGroupName()+"\",\""+myQual+"\");");
                
                if(groupNumber < 4000){
                    printWriter.write("<GROUPDEF name='"+myGroup.getGroupName()+"' javaName='"+myGroup.getGroupJavaName()+"' isRecord='Y' type='"+myGroup.getGroupName()+"' min='0' max='1'>\n");
                    printStandardFields(printWriter, myGroup);
                    
                    //Add Fields
                    for (Field myField : myGroup.getFields()) {
                        printField(myField, printWriter);
                    }
                    
                    printWriter.write("</GROUPDEF>\n");//End Group
                }   
            }
            
            printWriter.write("</GROUPDEF>\n");//End Header
            
            //Details
            printWriter.write("<GROUPDEF name='Detail' javaName='detail' isRecord='Y' type='' min='0' max='999999'>\n");
            printWriter.write("<GROUPDEF name='DetailRep' javaName='detailRep' isRecord='N' type='' min='1' max='1'>\n");
            for (Iterator it = sortedGroups.entrySet().iterator(); it.hasNext();) {                
                Map.Entry myMap = (Map.Entry)it.next();
                Group myGroup = (Group)myMap.getValue();
                Integer groupNumber = Integer.parseInt(myMap.getKey().toString());
             
                if(groupNumber >= 4000){
                    printWriter.write("<GROUPDEF name='"+myGroup.getGroupName()+"' javaName='"+myGroup.getGroupJavaName()+"' isRecord='Y' type='"+myGroup.getGroupName()+"' min='0' max='1'>\n");
                    printStandardFields(printWriter, myGroup);
                    //Add Fields
                    for (Field myField : myGroup.getFields()) {
                        printField(myField, printWriter);
                    }
                    printWriter.write("</GROUPDEF>\n");//End Group
                }   
            }
            
            printWriter.write("</GROUPDEF>\n</GROUPDEF>\n");//End Detail
            
            //End Document
            printWriter.write("</DOCUMENTDEF>\n</SPSFILE>");
            printWriter.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OracleParser.class.getName()).log(Level.SEVERE, null, ex);
        }
 
        return tmpFile;
    }
    
    private void printStandardFields(PrintWriter printWriter, Group group){
        printWriter.write("<FIELDDEF name='Trading_Partner_Code' javaName='trading_Partner_Code' minLength='1' maxLength='25' dataType='JString' rounding='2' mandatory='N' exclude='N' display='Y' editable='Y' enable='Y' print='Y' nextRow='N' keyType='NONE' persistent='Y' present='Y' templatable='Y' dtdRequired='N' edi='Y'>\n</FIELDDEF>\n");
        printWriter.write("<FIELDDEF name='Transaction_Reference_1' javaName='transaction_Reference_1' minLength='1' maxLength='22' dataType='JString' rounding='2' mandatory='N' exclude='N' display='Y' editable='Y' enable='Y' print='Y' nextRow='N' keyType='NONE' persistent='Y' present='Y' templatable='Y' dtdRequired='N' edi='Y'>\n</FIELDDEF>\n");
        printWriter.write("<FIELDDEF name='Transaction_Reference_2' javaName='transaction_Reference_2' minLength='1' maxLength='22' dataType='JString' rounding='2' mandatory='N' exclude='N' display='Y' editable='Y' enable='Y' print='Y' nextRow='N' keyType='NONE' persistent='Y' present='Y' templatable='Y' dtdRequired='N' edi='Y'>\n</FIELDDEF>\n");
        printWriter.write("<FIELDDEF name='Transaction_Reference_3' javaName='transaction_Reference_3' minLength='1' maxLength='22' dataType='JString' rounding='2' mandatory='N' exclude='N' display='Y' editable='Y' enable='Y' print='Y' nextRow='N' keyType='NONE' persistent='Y' present='Y' templatable='Y' dtdRequired='N' edi='Y'>\n</FIELDDEF>\n");
        printWriter.write("<FIELDDEF name='Record_Number' javaName='record_Number' minLength='1' maxLength='4' dataType='JString' defaultValue='"+group.getGroupName()+"' rounding='2' mandatory='N' exclude='N' display='Y' editable='Y' enable='Y' print='Y' nextRow='N' keyType='LANDMARK' persistent='Y' present='Y' templatable='Y' dtdRequired='N' edi='Y'>\n</FIELDDEF>\n");
        printWriter.write("<FIELDDEF name='Record_Layout' javaName='record_Layout' minLength='1' maxLength='2' dataType='JString' defaultValue='"+group.getCode()+"' rounding='2' mandatory='N' exclude='N' display='Y' editable='Y' enable='Y' print='Y' nextRow='N' keyType='NONE' persistent='Y' present='Y' templatable='Y' dtdRequired='N' edi='Y'>\n</FIELDDEF>\n");
        printWriter.write("<FIELDDEF name='Record_Layout_Qualifier' javaName='record_Layout_Qualifier' minLength='1' maxLength='3' dataType='JString' defaultValue='"+group.getQualifier()+"' rounding='2' mandatory='N' exclude='N' display='Y' editable='Y' enable='Y' print='Y' nextRow='N' keyType='NONE' persistent='Y' present='Y' templatable='Y' dtdRequired='N' edi='Y'>\n</FIELDDEF>\n");
    }
    
    private void printField(Field field, PrintWriter printWriter){
            printWriter.write("<FIELDDEF name='"+field.getFieldName()+"' javaName='"+field.getFieldJavaName()+"' minLength='"+field.getMinimumLength()+"' maxLength='"+field.getMaximumLength()+"' dataType='JString' rounding='2' mandatory='N' exclude='N' display='Y' editable='Y' enable='Y' print='Y' nextRow='N' keyType='NONE' persistent='Y' present='Y' templatable='Y' dtdRequired='N' edi='Y'>\n</FIELDDEF>\n");
    }
}
