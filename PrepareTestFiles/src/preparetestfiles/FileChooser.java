/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package preparetestfiles;

import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 * @author amcclintock
 */
public class FileChooser {
    final JFileChooser fleChooser = new JFileChooser();
    
    public File getFile(){
        fleChooser.showOpenDialog(null);
        File inFile = fleChooser.getSelectedFile();
        if (inFile == null){
            System.exit(1);
        }
        return inFile;
    }
 }

