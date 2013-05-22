package com.appacts.plugin.Models;

public final class AnalyticsSystem {
    
    public final int DeviceType;
    public final String Version;
    
    public AnalyticsSystem(int deviceType, String version) {
        this.DeviceType = deviceType;
        this.Version = version;
    }
}