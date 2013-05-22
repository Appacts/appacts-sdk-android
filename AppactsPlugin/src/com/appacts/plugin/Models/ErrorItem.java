package com.appacts.plugin.Models;

import java.util.Date;
import java.util.UUID;

public final class ErrorItem extends ItemWithScreen {
    
    public final String Data;
    public final DeviceGeneralInformation DeviceInformation;
    public final String EventName;
    public final ExceptionDescriptive Error;
    
    public ErrorItem(UUID applicationId, String screenName,
        String data, DeviceGeneralInformation deviceDynamicInformation,
        String eventName, ExceptionDescriptive ex, UUID sessionId, String version) {    
        super(applicationId, screenName, sessionId, version);
        
        this.Data = data;
        this.DeviceInformation = deviceDynamicInformation;
        this.EventName = eventName;
        this.Error = ex;
        
    }
    
    public ErrorItem(int id, UUID applicationId, String screenName,
    	String data, DeviceGeneralInformation deviceGeneralInformation, String eventName,
    	ExceptionDescriptive ex, Date dateCreated, UUID sessionId, String version) {
    	super(id, applicationId, screenName, dateCreated, sessionId, version);
    	
    	this.Data = data;
    	this.DeviceInformation = deviceGeneralInformation;
    	this.EventName = eventName;
    	this.Error = ex;
    }
}
    