package xtencilCreator.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
	public String todaysDate(String strFormat){
		Date today = new Date();
		DateFormat df = new SimpleDateFormat(strFormat);
		return df.format(today.getTime());
	}
	
	public boolean hasData(String entity){
		return entity != null && !entity.equals("");
	}
}
