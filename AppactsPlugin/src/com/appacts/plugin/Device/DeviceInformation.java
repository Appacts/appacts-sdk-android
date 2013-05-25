package com.appacts.plugin.Device;

import java.util.Locale;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.appacts.plugin.Device.Interfaces.IDeviceInformation;
import com.appacts.plugin.Handlers.Utils;
import com.appacts.plugin.Models.DeviceType;
import com.appacts.plugin.Models.Resolution;
 
public final class DeviceInformation implements IDeviceInformation {
        
    private final static String pluginVersion = "1.1.0.2322";
    private final Context context;
        
    public DeviceInformation(Context context) {    
    	this.context = context;
    }
    
    public int GetDeviceType() {
        return DeviceType.Android.GetCode();
    }
    
    public long GetFlashDriveSize() {
    	StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		long flashSize = (long)stat.getBlockSize() *(long)stat.getBlockCount();
		
		return flashSize;
    }
    
    public String GetModel() {
        return Build.MODEL;
    }
    
    public long GetMemorySize() {	
		StatFs stat = new StatFs(Environment.getRootDirectory().getPath());
		long totalMemory = (long)stat.getBlockSize() *(long)stat.getBlockCount();
		
		return totalMemory;
    }
    
    public String GetPluginVersion() {
        return DeviceInformation.pluginVersion;
    }
    
    public int GetPluginVersionCode() {
    	String pluginVersionNumeric = Utils.replaceAll(DeviceInformation.pluginVersion, ".", "");
    	return Integer.parseInt(pluginVersionNumeric);
    }
    
    public String GetLocale() {
    	return Locale.getDefault().toString();
    }
    
    public Resolution GetResolution() {
    	DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    	
    	Resolution resolution = new Resolution(displayMetrics.heightPixels, displayMetrics.widthPixels);
    	return resolution;
    }
    
    public String GetManufacturer() {
    	return Build.MANUFACTURER;
    }
}