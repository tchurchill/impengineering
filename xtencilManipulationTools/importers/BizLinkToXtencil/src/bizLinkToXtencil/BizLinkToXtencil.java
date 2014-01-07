package bizLinkToXtencil;

import java.io.File;
import java.util.HashMap;

import bizLinkToXtencil.controller.BizLinkParser;
import sps.xd.docdesign.converter.XtencilFileCreator;
import utilities.ImporterUtilities;

public class BizLinkToXtencil implements XtencilFileCreator {
	public static bizLinkToXtencil.controller.BizLinkParser doc;

	public File generateXtencilFromFile(String s) {
		return buildXTL(s);
	}
	
	static private File buildXTL(String fileName){
		doc = new BizLinkParser(new File (fileName));

		return doc.writeXtl();
	}

    public static void main(String[] args) {
    	// Test build
    	//buildXTL("\\Users\\amcclintock\\Desktop\\Spec1.bizLink");

		ImporterUtilities utils = new ImporterUtilities();

		// Install plugin
		utils.copyJar("Importer_BizLink.jar");

		// Update properties
		
		HashMap<String, String> props = new HashMap<String, String>();
		props.put("bizLink", "bizLinkToXtencil.BizLinkToXtencil");
		utils.setProperties(props, "sps.xd.importer.plugins=bizLink\\=>bizLinkToXtencil.BizLinkToXtencil");
    }
}
