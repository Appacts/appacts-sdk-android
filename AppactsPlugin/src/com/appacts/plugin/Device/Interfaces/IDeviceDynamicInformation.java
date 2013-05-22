package com.appacts.plugin.Device.Interfaces;

import com.appacts.plugin.Models.DeviceGeneralInformation;
import com.appacts.plugin.Models.DeviceLocation;

public interface IDeviceDynamicInformation {
    DeviceGeneralInformation GetDeviceGeneralInformation();
    DeviceLocation GetDeviceLocation() throws Exception;//, InterruptedException;
} 
