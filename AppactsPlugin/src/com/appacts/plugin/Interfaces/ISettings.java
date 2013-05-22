package com.appacts.plugin.Interfaces;

import java.util.UUID;

import com.appacts.plugin.Models.ApplicationStateType;
import com.appacts.plugin.Models.DeviceLocation;
import com.appacts.plugin.Models.User;

public interface ISettings {
    ApplicationStateType GetApplicationState(UUID applicationId);
    UUID GetDeviceId();
    DeviceLocation GetDeviceLocation();
    User GetUser(UUID applicationId);
    void Save(UUID applicationId, ApplicationStateType applicationState);
    void Save(User user);
    void Save(DeviceLocation deviceLocation);
    void SaveDeviceId(UUID deviceId);
} 
