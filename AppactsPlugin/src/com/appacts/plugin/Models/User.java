package com.appacts.plugin.Models;

import java.util.Date;
import java.util.UUID;

public class User extends Item {
	public final int Age;
    public final int Sex;
    public final int Status;
    
    public User(int age, int sexType, 
        int statusType, UUID applicationId, UUID sessionId, String version) {    
        super(applicationId, sessionId, version);
        
        this.Age = age;
        this.Sex = sexType;
        this.Status = statusType;
        
    }
    
    public User(int id, int age, int sexType, int statusType, 
        UUID applicationId, Date dateCreated, UUID sessionId, String version) {
        super(id, applicationId, dateCreated, sessionId, version);
        
        this.Age = age;
        this.Sex = sexType;
        this.Status = statusType;
    }
}
