package com.appacts.plugin.Interfaces;

import com.appacts.plugin.Models.DeviceType;

public interface IDeviceInformation {
    String GetDeviceId();
    DeviceType GetDeviceType();
    long GetFlashDriveSize();
    long GetMemorySize();
    String GetModel();
}