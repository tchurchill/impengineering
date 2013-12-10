/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package preparetestfiles;

import java.io.File;

/**
 *
 * @author amcclintock
 */
public class PrepareTestFiles {
    public static void main(String[] args) {
        String strRevision = "1.2";
        FileChooser fleChooser = new FileChooser();
        File inFile = fleChooser.getFile();
        ExcelData xlData = new ExcelData(inFile);
        WriteFile xlWriter = new WriteFile(xlData.getSampleFile(), inFile.getAbsolutePath());
        xlWriter.writeFile(strRevision);
    }
}
