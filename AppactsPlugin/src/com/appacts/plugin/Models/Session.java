package com.appacts.plugin.Models;

import java.util.Date;

public final class Session {
	private final Date dateStart;
    
    public final String Name;
    
    public Session() {
    	this.Name = null;
    	this.dateStart = new Date();
    }
    
    public Session(String screenName) {    
        this.Name = screenName;
        this.dateStart = new Date();
    }
    
    public long End() {
        return new Date().getTime() - this.dateStart.getTime();
    }
    
    public boolean equals(Object obj){
    	
    	if(this.getClass() == obj.getClass()) {
    		return ((Session)obj).Name.equals(this.Name);
    	}
    		
    	return false;
    }
}
