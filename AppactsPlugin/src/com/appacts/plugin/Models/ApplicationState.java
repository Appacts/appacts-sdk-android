package com.appacts.plugin.Models;

import java.util.Date;

public final class ApplicationState {
    public final int State;
    public final Date DateCreated;
    
    public ApplicationState(int applicationStateType, Date dateCreated) {
        this.State = applicationStateType;
        this.DateCreated = dateCreated;
    }
    
}