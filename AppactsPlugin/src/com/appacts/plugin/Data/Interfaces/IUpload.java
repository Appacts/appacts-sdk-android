package com.appacts.plugin.Data.Interfaces;

import java.util.UUID;

import com.appacts.plugin.Models.Crash;
import com.appacts.plugin.Models.DeviceLocation;
import com.appacts.plugin.Models.ErrorItem;
import com.appacts.plugin.Models.EventItem;
import com.appacts.plugin.Models.ExceptionWebServiceLayer;
import com.appacts.plugin.Models.FeedbackItem;
import com.appacts.plugin.Models.Resolution;
import com.appacts.plugin.Models.SystemError;
import com.appacts.plugin.Models.User;

public interface IUpload {
	int Crash(UUID deviceId, Crash crash) throws ExceptionWebServiceLayer;
    UUID Device(UUID applicationId, String model,  String osVersion, int deviceType,
        String carrier, String applicationVersion, int timeZoneOffset, String locale,
        Resolution resolution, String manufacturer) throws ExceptionWebServiceLayer;
    int Error(UUID deviceId, ErrorItem errorItem) throws ExceptionWebServiceLayer;
    int Event(UUID deviceId, EventItem eventItem) throws ExceptionWebServiceLayer;
    int Feedback(UUID deviceId, FeedbackItem feedbackItem) throws ExceptionWebServiceLayer;
    int SystemError(UUID deviceId, SystemError systemError) throws ExceptionWebServiceLayer;
    int User( UUID deviceId, User user) throws ExceptionWebServiceLayer;
    int Location(UUID deviceId, UUID applicationId, DeviceLocation deviceLocation) throws ExceptionWebServiceLayer;
    int Upgrade(UUID deviceId, UUID applicationId, String version) throws ExceptionWebServiceLayer;
}