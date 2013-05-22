package com.appacts.plugin.Handlers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {
	public static Date GetDateTimeNow() {
    	return new Date(System.currentTimeMillis());
    }
    
    public static String DateTimeFormat(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dateFormat.format(date).toString(); 
    }
    
    public static int TimeOffSet() {
    	return TimeZone.getDefault().getRawOffset();
    }
    
    public static String GetValueNotNull(String value) {
        if(value != null && !value.equals("null")) {
            return value;
        }
        return "";
    }
    
    public static String replaceAll(String source, String pattern, String replacement)
    {    

        //If source is null then Stop
        //and retutn empty String.
        if (source == null)
        {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        //Intialize Index to -1
        //to check agaist it later 
        int idx = 0;
        //Search source from 0 to first occurrence of pattern
        //Set Idx equal to index at which pattern is found.

        String workingSource = source;
        
        //Iterate for the Pattern till idx is not be -1.
        while ((idx = workingSource.indexOf(pattern, idx)) != -1)
        {
            //append all the string in source till the pattern starts.
            sb.append(workingSource.substring(0, idx));
            //append replacement of the pattern.
            sb.append(replacement);
            //Append remaining string to the String Buffer.
            sb.append(workingSource.substring(idx + pattern.length()));
            
            //Store the updated String and check again.
            workingSource = sb.toString();
            
            //Reset the StringBuffer.
            sb.delete(0, sb.length());
            
            //Move the index ahead.
            idx += replacement.length();
        }

        return workingSource;
    }
}
