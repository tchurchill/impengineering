/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package preparetestfiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



public class ExcelData {
    Sheet shtExcel = null;
            
    public ExcelData(File inFile){
        try{
            InputStream inFileStream = new FileInputStream(inFile);
            
            if (inFile.getName().endsWith(".xls")){
                POIFSFileSystem myFileSystem = new POIFSFileSystem(inFileStream);
                HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                shtExcel = myWorkBook.getSheetAt(0);
            }else if (inFile.getName().endsWith("xlsx")){
                XSSFWorkbook myWorkBook = new XSSFWorkbook(inFileStream); 
                shtExcel = myWorkBook.getSheetAt(0);
            }
        }catch (Exception ex){
            Logger.getLogger(ExcelData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public StringBuilder getSampleFile(){
        ArrayList<String> lstOMMs = new ArrayList<String>();
        StringBuilder stBufItemsAndKits = new StringBuilder("");
        StringBuilder stBufHeader = new StringBuilder("");
        
        ROW:
        for (Row row : shtExcel){
            boolean blnIsOMM = false;
            boolean blnIsHO = false;
            boolean blnIsLI = false;
            boolean blnIsPopulated = false;
            StringBuilder stBufTempRecord = new StringBuilder("");
            CELL:
            for (Cell cell : row){
                if (cell.getColumnIndex() == 0){
                    if (cell.toString().equals("OMM")){
                        blnIsOMM = true;
                    }else if (cell.toString().equals("HO")){
                        blnIsHO = true;
                    }else if (cell.toString().equals("LI")){
                        blnIsLI = true;
                    }else{
                        continue ROW;
                    }
                }else if (cell.getColumnIndex() != 0 && !blnIsHO && !blnIsLI && !blnIsOMM){
                    //Ignore extra information
                    continue ROW;
                }else if (cell.getColumnIndex() == 1){
                    continue CELL;
                }
                
                //Add OMMs
                if (blnIsOMM && cell.getColumnIndex() == 3 && cell.toString().equalsIgnoreCase("YES")){
                    lstOMMs.add(row.getCell(2).toString());
                }
                
                //Find Header
                if (blnIsHO){
                    stBufHeader.append(getData(cell, row, "HO"));
                }

                //Build LI and LK records only if row is populated
                if (blnIsLI){  
                    if (hasData(cell) && !cell.toString().matches("Master|Component|LI")){
                        //Only write out populated records
                        blnIsPopulated = true;
                    }
                    stBufTempRecord.append(getData(cell, row, "LI"));
                    

                    if (cell.getColumnIndex() == 2 && cell.toString().equals("Component")){
                        stBufTempRecord.replace(0, 4, "\"LK\"");
                    }

                    if (blnIsPopulated && isLastCell(cell, row)){
                        stBufItemsAndKits.append(stBufTempRecord);
                    }
                }
            }
        }

        //Build final output
        StringBuilder stBufFinalDoc = new StringBuilder("");
        for (String strOMM : lstOMMs){
            stBufFinalDoc.append(insertOMM(stBufHeader, strOMM));
            stBufFinalDoc.append(stBufItemsAndKits);
        }
        return stBufFinalDoc;
    }
    
    private StringBuilder insertOMM(StringBuilder stBuild, String strOMM){
        String strHeader = stBuild.toString();
        String[] strFields = Pattern.compile(",").split(strHeader);
        strFields[1] = getData(strOMM);
        StringBuilder stNewHeader = new StringBuilder();
        
        for (int i = 0; i < strFields.length; i++){
            String strField = strFields[i];
            if (i != strFields.length -1){
                strField += ",";
            }
            stNewHeader.append(strField);
        }
 
        return stNewHeader.append("\n");
    }
    
    private String getData(Cell cell, Row row, String strRecord){
        String strFinalData = cell.toString();
        strFinalData = "\"" + strFinalData + "\"";
        if (isLastCell(cell, row) && !strRecord.equals("HO")){
            strFinalData += "\n";
        }else{
            strFinalData += ",";
        }
        return strFinalData;
    }
    
    private String getData(String strData){
        strData = "\"" + strData + "\"";
        return strData;
    }
    
    private Boolean hasData(Cell cell){
        return cell.toString() != null && !cell.toString().equals("");
    }
    
    private Boolean isLastCell(Cell cell, Row row){
        return cell.getColumnIndex() + 1 == row.getLastCellNum();
    }
}