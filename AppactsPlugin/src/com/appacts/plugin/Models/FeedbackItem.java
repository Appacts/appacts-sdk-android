package com.appacts.plugin.Models;

import java.util.Date;
import java.util.UUID;

public class FeedbackItem extends ItemWithScreen {
    
    public final String Message;
    public final int Rating; 
    
    public FeedbackItem(UUID applicationId, 
        String screenName, String message, int ratingType, UUID sessionId, String version) {    
        super(applicationId, screenName, sessionId, version);
        
        this.Message = message;
        this.Rating = ratingType;
        
    }
    
    public FeedbackItem(int id, UUID applicationId, String screenName, String message, 
        int ratingType, Date dateCreated, UUID sessionId, String version) {
        super(id, applicationId, screenName, dateCreated, sessionId, version);
        
        this.Message = message;
        this.Rating = ratingType;
    }
    
} 