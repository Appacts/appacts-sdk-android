package com.appacts.plugin.Handlers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class HttpConnectionManager {
    
	//private static Context context;
	
    public HttpConnectionManager(Context context)
    {
    	//this.context = context;
    }
    
    public static boolean HasNetworkCoverage(Context context) 
    {       

	    ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    
	    return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || 
	    		connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI  ).getState() == NetworkInfo.State.CONNECTED;

	    
//    	boolean hasWifiCoverage = false;
//	    boolean hasMobileCoverage = false;
	    
//	    NetworkInfo[] netInfo = connectivityManager.getAllNetworkInfo();
//	    
//	    for (NetworkInfo ni : netInfo) {
//	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
//	            if (ni.isConnected())
//	            	hasWifiCoverage = true;
//	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
//	            if (ni.isConnected())
//	            	hasMobileCoverage = true;
//	    }
//	    
//	    return hasWifiCoverage || hasMobileCoverage;
    }
}
