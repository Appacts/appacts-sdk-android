package com.appacts.plugin.Models;

import java.util.Date;

import com.appacts.plugin.Handlers.Utils;

public class DeviceLocation {
    
    public final double Latitude;
    public final double Longitude;
    public final String CountryName;
    public final String CountryCode;
    public final String CountryAdminName;
    public final String CountryAdminCode;
    
    public final Date DateCreated;
    
    public DeviceLocation(double latitude, double longitude) { 
        this.Latitude = latitude;
        this.Longitude = longitude;
        this.CountryName = null;
        this.CountryCode = null;
        this.CountryAdminName = null;
        this.CountryAdminCode = null;
        this.DateCreated = Utils.GetDateTimeNow();
    }
    
    public DeviceLocation(double latitude, double longitude, Date dateCreated) {
        this.Latitude = latitude;
        this.Longitude = longitude;
        this.CountryName = null;
        this.CountryCode = null;
        this.CountryAdminName = null;
        this.CountryAdminCode = null;
        this.DateCreated = dateCreated;
    }
    
    public DeviceLocation(double latitude, double longitude, String countryName, String countryCode, 
        String countryAdminName, String countryAdminCode, Date dateCreated) {
        this.Latitude = latitude;
        this.Longitude = longitude;
        this.CountryName = countryName;
        this.CountryCode = countryCode;
        this.CountryAdminName = countryAdminName;
        this.CountryAdminCode = countryAdminCode;
        this.DateCreated = dateCreated;
    }
        public DeviceLocation(double latitude, double longitude, String countryName, String countryCode, 
        String countryAdminName, String countryAdminCode) {
        this.Latitude = latitude;
        this.Longitude = longitude;
        this.CountryName = countryName;
        this.CountryCode = countryCode;
        this.CountryAdminName = countryAdminName;
        this.CountryAdminCode = countryAdminCode;
        this.DateCreated = Utils.GetDateTimeNow();
    }
} 