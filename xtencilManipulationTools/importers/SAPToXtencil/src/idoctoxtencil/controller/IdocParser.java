/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package idoctoxtencil.controller;

import idoctoxtencil.model.Field;
import idoctoxtencil.model.Group;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;


/**
 *
 * @author Mike
 */
public class IdocParser {

	static TagNode bodyNode = null;
	static TagNode segStructures = null;
	static PrintWriter printWriter = null;
	static String xtlDirection  = null;
	static String fileName = null;
	static String rootGroupName = null;
	static Boolean needXml = null;
    static String groupPref = null;


        public File processFile(File file, String groupPrefix, String fileFormat) throws IOException, XPatherException{

           //Input Parameters
            groupPref = groupPrefix;

            if(fileFormat.matches("XML")){
                needXml = true;
            }else{
                needXml = false;
            }

            HtmlCleaner cleaner = new HtmlCleaner();
            TagNode docNode = cleaner.clean(file);
            bodyNode = docNode.findElementByName("body", true);

			File tmpDir = new File(System.getProperty("user.home") + File.separator + "xtlTemp" + File.separator);
			tmpDir.mkdirs();
			File tmpFile = new File (tmpDir.getAbsolutePath() + File.separator + "tmp.xtl");

            printWriter = new PrintWriter(new FileOutputStream(tmpFile));
            TagNode sections = null;


            /*
             * Evaluate "ul" tags
             */
            TagNode[] testNodes = bodyNode.getChildTags();
            System.out.println(testNodes.length);
            
            for(int i = 0; i<testNodes.length ; i++){
                TagNode tempTagNode = (TagNode)testNodes[i];
                if(tempTagNode.getText().toString().contains("Structure of basic type")){
                    sections = testNodes[i+4];
                }else if(tempTagNode.getText().toString().contains("Structure of extension")){
                    sections = testNodes[i+5];
                }

                if(tempTagNode.getText().toString().contains("Segment structures")){
                    segStructures = testNodes[i+3];
                }


            }
            

            /*
             * If !XML, write out the EDI_DC40
             */
            if(!needXml || needXml){
                // Handle XML
                String xtlType = "XML";
                if (!needXml){
                	xtlType = "Flat File";
                }

                //Begin Writing Document
                printWriter.write("<?xml version='1.0' encoding='UTF-8'?>\n<!DOCTYPE SPSFILE SYSTEM 'C:\\XtencilDesigner\\form\\xtencil.dtd'>\n<SPSFILE name='SPS Commerce Xtencil' date='' fileType='FORM' contents='NORM'>\n<DOCUMENTDEF javaPackageName='' owner='' revision='' barcode='N' xtencilType='" + xtlType + "' justification='Left' display='Y' enable='Y' print='Y' exportSource='N' direction='O' resend='N' "+">\n");

                // Add date format
                printWriter.write("<FORMAT>\n<FIELDFMT writeFormat='yyyyMMdd' maxLength='2147483647' minLength='0' justify='NONE' trim='NONE' dataType='DATE' name='all' keyType='all' desc='all'>yyyyMMdd</FIELDFMT>\n</FORMAT>\n");
                
                //Write Group
                printWriter.write("<GROUPDEF name='EDI_DC40' javaName='edi_DC40' isRecord='Y' type='EDI_DC40' min='1' max='1'>\n");
                
    

                //Write Fields
                printWriter.write("<FIELDDEF name='TABNAM' javaName='tABNAM' minLength='1' maxLength='8' dataType='JString' mandatory='N' defaultValue='EDI_DC40' keyType='LANDMARK'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='UTF' javaName='uTF' minLength='1' maxLength='2' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='MANDT' javaName='mANDT' minLength='1' maxLength='3' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='DOCNUM' javaName='dOCNUM' minLength='1' maxLength='16' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='DOCREL' javaName='dOCREL' minLength='1' maxLength='4' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='STATUS' javaName='sTATUS' minLength='1' maxLength='2' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='DIRECT' javaName='dIRECT' minLength='1' maxLength='1' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='OUTMOD' javaName='oUTMOD' minLength='1' maxLength='1' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='EXPRSS' javaName='eXPRSS' minLength='1' maxLength='1' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='TEST' javaName='tEST' minLength='1' maxLength='1' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='IDOCTYP' javaName='iDOCTYP' minLength='1' maxLength='30' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='CIMTYP' javaName='cIMTYP' minLength='1' maxLength='30' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='MESTYP' javaName='mESTYP' minLength='1' maxLength='30' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='MESCOD' javaName='mESCOD' minLength='1' maxLength='3' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='MESFCT' javaName='mESFCT' minLength='1' maxLength='3' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='STD' javaName='sTD' minLength='1' maxLength='1' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='STDVRS' javaName='sTDVRS' minLength='1' maxLength='6' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='STDMES' javaName='sTDMES' minLength='1' maxLength='6' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='SNDPOR' javaName='sNDPOR' minLength='1' maxLength='10' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='SNDPRT' javaName='sNDPRT' minLength='1' maxLength='2' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='SNDPFC' javaName='sNDPFC' minLength='1' maxLength='2' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='SNDPRN' javaName='sNDPRN' minLength='1' maxLength='10' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='SNDSAD' javaName='sNDSAD' minLength='1' maxLength='21' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='SNDLAD' javaName='sNDLAD' minLength='1' maxLength='70' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='RCVPOR' javaName='rCVPOR' minLength='1' maxLength='10' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='RCVPRT' javaName='rCVPRT' minLength='1' maxLength='2' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='RCVPFC' javaName='rCVPFC' minLength='1' maxLength='2' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='RCVPRN' javaName='rCVPRN' minLength='1' maxLength='10' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='RCVSAD' javaName='rCVSAD' minLength='1' maxLength='21' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='RCVLAD' javaName='rCVLAD' minLength='1' maxLength='70' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='CREDAT' javaName='cREDAT' minLength='1' maxLength='8' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='CRETIM' javaName='cRETIM' minLength='1' maxLength='6' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='REFINT' javaName='rEFINT' minLength='1' maxLength='14' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='REFGRP' javaName='rEFGRP' minLength='1' maxLength='14' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='REFMES' javaName='rEFMES' minLength='1' maxLength='14' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='ARCKEY' javaName='aRCKEY' minLength='1' maxLength='70' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                printWriter.write("<FIELDDEF name='SERIAL' javaName='sERIAL' minLength='1' maxLength='20' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");

                //End Group
                printWriter.write("</GROUPDEF>\n");
            }


            getGroup(sections);
            printWriter.write("</DOCUMENTDEF>\n</SPSFILE>");
            printWriter.close();

            return tmpFile;

    }


    public static void getGroup(TagNode node) throws XPatherException{
           
		for(TagNode childOne : node.getChildTags()){
			//System.out.println("Tag: "+childOne.getName());
			//System.out.println("Tag Children Count: "+childOne.getChildTags().length);

			if(childOne.getName().equals("li")){
                boolean isRep = false;
				Group tempGroup = new Group();

				//Get Groups Name
				for(TagNode grandChild : childOne.getChildTags()){
					if(grandChild.getName().equals("b")){
                                            tempGroup.setGroupName(grandChild.getText().substring(0,12).trim(), groupPref);
					}
				}
				System.out.println("GroupName: "+tempGroup.getGroupName());

				//Get Group Attributes
				for(TagNode grandChild : childOne.getChildTags()){
					if(grandChild.getName().equals("p") && grandChild.getText().toString().contains("Status")){
						HashMap<String, String> attributeMap = new HashMap<String, String>();
						String[] stageOneAttributes = grandChild.getText().toString().split(",");

						for(int i = 0 ; i<stageOneAttributes.length; i++){
							String[] tempAttribute = stageOneAttributes[i].split(":");
							attributeMap.put(tempAttribute[0].trim(), tempAttribute[1].trim());
						}

						tempGroup.setMaxRepetitions(attributeMap.get("max. number"));
						tempGroup.setMinRepetitions(attributeMap.get("min. number"));

						String essentialValue = attributeMap.get("Status");
						if(essentialValue.matches("Essential")){
							tempGroup.setRequired(true);
						}else{
							tempGroup.setRequired(false);
						}
					}
				}

				//Stats
				System.out.println("Min Reps: "+tempGroup.getMinRepetitions());
				System.out.println("Max Reps: "+tempGroup.getMaxRepetitions());
				System.out.println("Required: "+tempGroup.isRequired());




				/*
				 * Write Group Header to file
				 */

				int groupMaxReps = Integer.parseInt(tempGroup.getMaxRepetitions());
				if(groupMaxReps > 1 && tempGroup.isRequired()){
				}else if(groupMaxReps == 1 && tempGroup.isRequired()){
					printWriter.write("<GROUPDEF name='"+tempGroup.getGroupName()+"' javaName='"+tempGroup.getGroupJavaName()+"' isRecord='Y' type='"+tempGroup.getGroupName()+"' min='1' max='1'>\n");
				}else if(groupMaxReps == 1 && !tempGroup.isRequired()){
					printWriter.write("<GROUPDEF name='"+tempGroup.getGroupName()+"' javaName='"+tempGroup.getGroupJavaName()+"' isRecord='Y' type='"+tempGroup.getGroupName()+"' min='0' max='1'>\n");
				}else if(groupMaxReps > 1 && !tempGroup.isRequired()){
                                        isRep = true;
					printWriter.write("<GROUPDEF name='"+tempGroup.getGroupName()+"' javaName='"+tempGroup.getGroupJavaName()+"' isRecord='Y' type='"+tempGroup.getGroupName()+"' min='0' max='"+tempGroup.getMaxRepetitions()+"'>\n");
                    printWriter.write("<GROUPDEF name='"+tempGroup.getGroupName()+"Rep' javaName='"+tempGroup.getGroupJavaName()+"Rep' isRecord='Y' type='"+tempGroup.getGroupName()+"' min='1' max='1'>\n");
				}


				/*
				 * If !XML , write standard SAP Flat File fields
				 */
                if(!needXml){
               		String groupName = tempGroup.getGroupName();
               		groupName = groupName.replaceAll("(.{7}).*", "$1");
               		
                    //Traditional SAP
                    printWriter.write("<FIELDDEF name='SEGNAM' javaName='sEGNAM' minLength='1' maxLength='7' dataType='JString' mandatory='N' defaultValue='" + groupName + "' keyType='LANDMARK'>\n</FIELDDEF>\n");
                    printWriter.write("<FIELDDEF name='SEG_VER' javaName='sEGVER' minLength='1' maxLength='3' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                    printWriter.write("<FIELDDEF name='SEG_BUFFER' javaName='sEGBUF' minLength='1' maxLength='20' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                    printWriter.write("<FIELDDEF name='MANDT' javaName='mANDT' minLength='1' maxLength='3' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                    printWriter.write("<FIELDDEF name='DOCNUM' javaName='dOCNUM' minLength='1' maxLength='16' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                    printWriter.write("<FIELDDEF name='SEGNUM' javaName='sEGNUM' minLength='1' maxLength='6' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                    printWriter.write("<FIELDDEF name='PSGNUM' javaName='pSGNUM' minLength='1' maxLength='6' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                    printWriter.write("<FIELDDEF name='HLEVEL' javaName='hLEVEL' minLength='1' maxLength='2' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
                }

				//Get group fields
                                //TO-DO
				//segStructures = (TagNode)bodyNode.evaluateXPath("ul")[1];

				for(TagNode groupFields : segStructures.getChildTags()){
					//System.out.println(groupFields.getName());
					//Object[] myseg = groupFields.evaluateXPath("//h2");
					//System.out.println("Xpath eval: "+myseg.length);

					if(groupFields.getName().matches("li")){
						TagNode h2 = (TagNode) groupFields.evaluateXPath("//h2")[0];
						if(h2.getText().toString().contains(tempGroup.getGivenGroupName())){
							TagNode ol = (TagNode)groupFields.evaluateXPath("//ol")[0];

							for(TagNode liTag : ol.getChildTags()){
								/* Print out all ol child tags
								TagNode[] liElements = liTag.getAllElements(true);
								for(int i = 0 ; i<liElements.length; i++){
									System.out.println(liElements[i].getName());
								}*/
								Field tempField = new Field();

								//Get field name
								String liText = liTag.getText().toString();
								String[] fieldSplit = liText.split(":");
								String fieldName = fieldSplit[0].trim();
								tempField.setFieldName(fieldName);
								System.out.println(fieldName);

								//Get field length
								TagNode pTag = (TagNode)liTag.evaluateXPath("//p")[1];
								String[] pSplit = pTag.getText().toString().split(":");
								String fieldLength = pSplit[5].trim().replaceAll("^0+(?!$)", "");
								tempField.setMaxLength(fieldLength);
								System.out.println("Field Length:"+fieldLength);



								//TagNode bTag = (TagNode)liTag.evaluateXPath("//b")[0];
								//System.out.println(liTag.getText());

								/*
								 * Write Field to file
								 */

								printWriter.write("<FIELDDEF name='"+tempField.getFieldName()+"' javaName='"+tempField.getFieldJavaName()+"' minLength='1' maxLength='"+tempField.getMaxLength()+"' dataType='JString' mandatory='N' defaultValue='' keyType='NONE'>\n</FIELDDEF>\n");
							}
						}
					}

				}





				//Get Children Tags
				for(TagNode grandChild : childOne.getChildTags()){
					if(grandChild.getName().equals("ul")){
						System.out.println("Child Group");
						getGroup(grandChild);

					}
				}
			if(isRep == true){
                            printWriter.write("</GROUPDEF>\n");
                            isRep = false;
                        }
			printWriter.write("</GROUPDEF>\n");
			System.out.println("End "+tempGroup.getGroupName());
			//Blank line
			System.out.println("");

			}

		}

	}
}
