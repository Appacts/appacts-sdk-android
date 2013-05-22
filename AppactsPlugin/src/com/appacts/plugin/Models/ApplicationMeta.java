package com.appacts.plugin.Models;

import java.util.Date;
import java.util.UUID;

public final class ApplicationMeta {
	
	public final UUID Id;
    public int State;
    public final Date DateCreated;
    public UUID SessionId;
    public String Version;
    public boolean Upgraded;
    public int OptStatus;
    
    public ApplicationMeta(UUID applicationId, int applicationStateType, UUID sessionId, 
    		Date dateCreated, String version, boolean upgraded, int optStatus) {
    	this.Id = applicationId;
        this.State = applicationStateType;
        this.SessionId = sessionId;
        this.DateCreated = dateCreated;
        this.Version = version;
        this.Upgraded = upgraded;
        this.OptStatus = optStatus;
    }
    
    public ApplicationMeta(UUID applicationId, int applicationStateType,
    		Date dateCreated, int optStatus)
    {
    	this.Id = applicationId;
        this.State = applicationStateType;
        this.DateCreated = dateCreated;
        this.OptStatus = optStatus;
    }
    
}