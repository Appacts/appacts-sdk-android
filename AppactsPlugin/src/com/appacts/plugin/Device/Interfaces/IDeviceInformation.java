package com.appacts.plugin.Device.Interfaces;

import com.appacts.plugin.Models.Resolution;

public interface IDeviceInformation {
    int GetDeviceType();
    long GetFlashDriveSize();
    long GetMemorySize();
    String GetModel();
    String GetPluginVersion();
    int GetPluginVersionCode();
    String GetLocale();
    Resolution GetResolution();
    String GetManufacturer();
}