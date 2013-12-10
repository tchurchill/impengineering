package xtencilCreator.view;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
 

public class FileChooser {
	public File selectedFile;

	public FileChooser(){
		JFileChooser fc = new JFileChooser();
		FileFilter filter1 = new ExtensionFileFilter("Excel documents", new String[] { "XLS", "XLSX" });
		fc.setFileFilter(filter1);
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int status = fc.showOpenDialog(null);
		selectedFile = fc.getSelectedFile();
		if (status == JFileChooser.APPROVE_OPTION){
			selectedFile = fc.getSelectedFile();
		}
	}
}

class ExtensionFileFilter extends FileFilter {
	  String description;

	  String extensions[];

	  public ExtensionFileFilter(String description, String extension) {
	    this(description, new String[] { extension });
	  }

	  public ExtensionFileFilter(String description, String extensions[]) {
	    if (description == null) {
	      this.description = extensions[0];
	    } else {
	      this.description = description;
	    }
	    this.extensions = (String[]) extensions.clone();
	    toLower(this.extensions);
	  }

	  private void toLower(String array[]) {
	    for (int i = 0, n = array.length; i < n; i++) {
	      array[i] = array[i].toLowerCase();
	    }
	  }

	  public String getDescription() {
	    return description;
	  }

	  public boolean accept(File file) {
	    if (file.isDirectory()) {
	      return true;
	    } else {
	      String path = file.getAbsolutePath().toLowerCase();
	      for (int i = 0, n = extensions.length; i < n; i++) {
	        String extension = extensions[i];
	        if ((path.endsWith(extension) && (path.charAt(path.length() - extension.length() - 1)) == '.')) {
	          return true;
	        }
	      }
	    }
	    return false;
	  }
	}
