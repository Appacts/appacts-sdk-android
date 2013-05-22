package com.appacts.plugin.Device;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import com.appacts.plugin.Device.Interfaces.IDeviceDynamicInformation;
import com.appacts.plugin.Models.DeviceGeneralInformation;
import com.appacts.plugin.Models.DeviceLocation;

public final class DeviceDynamicInformation
	implements IDeviceDynamicInformation{
	
	private final Context context;
	private SignalStrength signalStrength;
	private PhoneStateListener phoneStateListener;
	private TelephonyManager telephonyManager;
	    
	public DeviceDynamicInformation(Context context) {    
		this.context = context;
		
		this.phoneStateListener = new PhoneStateListener() {
			@Override
			public void onSignalStrengthsChanged(SignalStrength signal)
			{
				signalStrength = signal;
				
				super.onSignalStrengthsChanged(signalStrength);
			}
		};
		
		this.telephonyManager =
    			(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		
		this.telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}
	
	public DeviceGeneralInformation GetDeviceGeneralInformation() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		long flashSize = (long)stat.getBlockSize() *(long)stat.getBlockCount();
		//long megAvailable = bytesAvailable / 1048576;
		
		ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);
		
		Intent batteryIntent = context.getApplicationContext().registerReceiver(null,
            new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		
		DeviceGeneralInformation deviceGeneralInformation =
	        new DeviceGeneralInformation
	        (
	        	flashSize,
	        	memoryInfo.availMem,
	        	batteryIntent.getIntExtra("level", 0),
	        	signalStrength.getGsmSignalStrength()
	        );
	        
	    return deviceGeneralInformation;
	}
	
	public DeviceLocation GetDeviceLocation() throws Exception, InterruptedException {
	    DeviceLocation deviceLocation = null;
	    
	    Criteria criteria = new Criteria();
	    criteria.setAccuracy(50);
	    criteria.setCostAllowed(true);
	    criteria.setPowerRequirement(Criteria.POWER_LOW);
	    
	    try {
	    	LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

	    	Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	        
	        deviceLocation = new DeviceLocation(location.getLatitude(), location.getLongitude());
	    } catch (Exception locationException) {
	        throw locationException;
	    } 
	    
	    return deviceLocation;
	}

} 