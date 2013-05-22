package com.appacts.plugin.Models;

import java.util.Date;
import java.util.UUID;

public final class EventItem extends ItemWithScreen {
    
	public final String Data;
    public final int EventType;
    public final String EventName;
    public final long Length;
    
    public EventItem(UUID applicationId, String screenName, String data, int eventType, String eventName,
       long length, UUID sessionId, String version) { 
        super(applicationId, screenName, sessionId, version);
        
        this.Data = data;
        this.EventType = eventType;
        this.EventName = eventName;
        this.Length = length;
    }
    
    public EventItem(int id, UUID applicationId, String screenName,
    String data, int eventType, String eventName, long length, Date dateCreated, UUID sessionId, String version) {
        super(id, applicationId, screenName, dateCreated, sessionId, version);
        
        this.Data = data;
        this.EventType = eventType;
        this.EventName = eventName;
        this.Length = length;
    }
    
}