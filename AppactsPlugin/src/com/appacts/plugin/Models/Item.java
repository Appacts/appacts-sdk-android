package com.appacts.plugin.Models;

import java.util.Date;
import java.util.UUID;

import com.appacts.plugin.Handlers.Utils;

abstract class Item {
    
    public final int Id;
    public final UUID ApplicationId;
    public final Date DateCreated;
    public final String Version;
    public final UUID SessionId;
        
    public Item(UUID applicationId, UUID sessionId, String version) {    
        this.ApplicationId = applicationId;
        this.DateCreated = Utils.GetDateTimeNow();
        this.Id = 0;
        this.Version = version;
        this.SessionId = sessionId;
    }
    
    public Item(int id, UUID applicationId, Date dateCreated, UUID sessionId, String version) {
        this.Id = id;
        this.ApplicationId = applicationId;
        this.DateCreated = dateCreated;
        this.Version = version;
        this.SessionId = sessionId;
    }
    
} 
