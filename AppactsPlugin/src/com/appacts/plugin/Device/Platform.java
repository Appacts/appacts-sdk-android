package com.appacts.plugin.Device;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.appacts.plugin.Device.Interfaces.IPlatform;

public final class Platform implements IPlatform {
    
	private final Context context;
	
    public Platform(Context context) {
    	this.context = context;
    }
    
    public String GetCarrier() {
    	TelephonyManager manager = (TelephonyManager)this.context.getSystemService(Context.TELEPHONY_SERVICE);
    	return  manager.getNetworkOperatorName();
    }
    
    public String GetOS() {
         return Integer.toString(Build.VERSION.SDK_INT);
    }
}
