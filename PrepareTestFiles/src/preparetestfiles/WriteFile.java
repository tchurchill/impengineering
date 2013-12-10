/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package preparetestfiles;

import java.io.File;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;


/**
 *
 * @author amcclintock
 */
public class WriteFile {
    String strData = null;
    String strFileName = null;

    public WriteFile(StringBuilder strBuild, String strFile){
        strData = strBuild.toString();
        strFileName = FilenameUtils.removeExtension(strFile) + ".csv";
    }

    public void writeFile(String strVersion){
        try{
            File outFile = new File(strFileName);
            FileUtils.writeStringToFile(outFile, strData);
            JOptionPane.showMessageDialog(null, "File " + outFile.getAbsolutePath() + " was created successfully", "Test file created " + strVersion, 1);
        }catch (Exception e){
            System.err.println("Error writing: " + e.getMessage());
        }  
    }
}
