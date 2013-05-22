package com.appacts.plugin.Data.Interfaces;

import java.util.Date;
import java.util.UUID;

import com.appacts.plugin.Models.ApplicationMeta;
import com.appacts.plugin.Models.DeviceLocation;
import com.appacts.plugin.Models.ExceptionDatabaseLayer;
import com.appacts.plugin.Models.PluginMeta;
import com.appacts.plugin.Models.User;

public interface ISettings {
    UUID GetDeviceId() throws ExceptionDatabaseLayer;
    DeviceLocation GetDeviceLocation(int statusType) throws ExceptionDatabaseLayer;
    User GetUser(UUID applicationId, int statusType) throws ExceptionDatabaseLayer;
    void Save(User user) throws ExceptionDatabaseLayer;
    void Save(DeviceLocation deviceLocation, int statusType) throws ExceptionDatabaseLayer;
    void SaveDeviceId(UUID deviceId, Date dateCreated) throws ExceptionDatabaseLayer;
    void Update(User user, int statusType) throws ExceptionDatabaseLayer;
    ApplicationMeta LoadApplication(UUID applicationId) throws ExceptionDatabaseLayer;
    void Update(ApplicationMeta applicationState) throws ExceptionDatabaseLayer;
    void Save(ApplicationMeta applicationState) throws ExceptionDatabaseLayer;
	PluginMeta LoadPlugin() throws ExceptionDatabaseLayer;
	void Save(PluginMeta pluginMeta) throws ExceptionDatabaseLayer;
	void Update(PluginMeta pluginMeta) throws ExceptionDatabaseLayer;
} 