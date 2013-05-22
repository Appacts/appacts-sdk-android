package com.appacts.plugin.Models;

import java.util.Date;
import java.util.UUID;

public class Crash extends Item {
    
    public Crash(UUID applicationId, UUID sessionId, String version) {
        super(applicationId, sessionId, version);    
    }
    
    public Crash(int id, UUID applicationid, Date dateCreated, UUID sessionId, String version) {
    	super(id, applicationid, dateCreated, sessionId, version);
    }
    
}