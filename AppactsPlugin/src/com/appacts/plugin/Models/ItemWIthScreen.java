package com.appacts.plugin.Models;

import java.util.Date;
import java.util.UUID;

class ItemWithScreen extends Item {
    
    public final String ScreenName;
    
    public ItemWithScreen(UUID applicationId, String screenName, UUID sessionId, String version) { 
        super(applicationId, sessionId, version);
        this.ScreenName = screenName;
    }
    
    public ItemWithScreen(int id, UUID applicationId, String screenName, Date dateCreated, UUID sessionId, String version) {
        super(id, applicationId, dateCreated, sessionId, version);
        this.ScreenName = screenName;
    }
    
} 
