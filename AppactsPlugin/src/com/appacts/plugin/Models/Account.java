package com.appacts.plugin.Models;

import java.util.UUID;

public class Account {
    
    public final UUID AccountId;
    public final UUID ApplicationId;
    
    public Account(UUID accountId, UUID applicationId) {    
        this.AccountId = accountId;
        this.ApplicationId = applicationId;
    }
}
