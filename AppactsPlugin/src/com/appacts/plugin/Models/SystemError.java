package com.appacts.plugin.Models;

import java.util.Date;
import java.util.UUID;

public final class SystemError extends Item {
    
	public final ExceptionDescriptive Error;
    public final AnalyticsSystem System;
    
    public SystemError(UUID applicationId, 
        ExceptionDescriptive ex, AnalyticsSystem system, String version) {    
        super(applicationId, null, version);
        
        this.Error = ex;
        this.System = system;
    }
    
    public SystemError(int id, UUID applicationId, ExceptionDescriptive ex, 
        AnalyticsSystem system, Date dateCreated, String version) {
        super(id, applicationId, dateCreated, null, version);
        
        this.Error = ex;
        this.System = system;
    }
    
} 
