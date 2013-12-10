package xtencilCreator.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class ImporterUtilities {
	public void copyJar(String jarName){
		File xdLib = new File ("/XtencilDesigner" + File.separator + "lib"); 
		xdLib.mkdirs();
		File currentJar = new File (new File(jarName).getAbsolutePath());
		File newJar = new File (xdLib + File.separator + jarName);

		try{
			FileUtils.copyFile(currentJar, newJar);
		}catch (Exception err){
		}
	}
	
	public void setProperties(HashMap<String, String> props, String defaultValue){
		File xdProps = new File("/XtencilDesigner/XtencilDesigner.properties");
        FileInputStream fs = null;
        InputStreamReader in = null;
        BufferedReader br = null;
        String line;
        boolean hasImporter = false;
        StringBuffer sb = new StringBuffer();

        // Find existing, or add new
		try{
			fs = new FileInputStream(xdProps);
			in = new InputStreamReader(fs);
			br = new BufferedReader(in);
			
			while ((line = br.readLine()) != null) {
				if (line.startsWith("sps.xd.importer.plugins")){
					hasImporter = true;
					HashMap <String, String> existing = new HashMap <String, String>();
					
					line = line.replaceAll("sps.xd.importer.plugins=", "");
					String[] values = line.split(",");
					for (int i = 0; i < values.length; i++){
						existing.put(values[i].replaceAll("(^\\w*).*", "$1"), values[i].replaceAll(".*?=>(.*)", "$1"));
					}
					existing.putAll(props);
					line = "sps.xd.importer.plugins=";

					for (Map.Entry<String, String> plugin: existing.entrySet()){
						if (!line.endsWith("=")){
							line += ",";
						}
						line += plugin.getKey()+ "\\=>" + plugin.getValue();
					}
				}
				sb.append(line);
				sb.append(System.getProperty("line.separator"));
			}
			if (!hasImporter){
				sb.append(defaultValue + System.getProperty("line.separator"));
			}
            fs.close();
            in.close();
            br.close();
	
			
		}catch(Exception err){
		}

		// Write newly updated/created
        try{
            FileWriter fstream = new FileWriter(xdProps);
            BufferedWriter outobj = new BufferedWriter(fstream);
            outobj.write(sb.toString());
            outobj.close();
            
        }catch (Exception e){
          System.err.println("Error: " + e.getMessage());
        }
	}
}
