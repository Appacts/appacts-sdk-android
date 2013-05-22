package com.appacts.plugin.Data;

import java.util.Date;
import java.util.UUID;

import com.appacts.plugin.Data.Interfaces.ISettings;
import com.appacts.plugin.Models.ApplicationMeta;
import com.appacts.plugin.Models.DeviceLocation;
import com.appacts.plugin.Models.ExceptionDatabaseLayer;
import com.appacts.plugin.Models.PluginMeta;
import com.appacts.plugin.Models.User;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SettingsDB implements ISettings {
	
	private final SQLiteDatabase databaseReadWrite;
	private final SQLiteDatabase databaseReadOnly;
	
	public SettingsDB(SQLiteDatabase databaseReadWrite, SQLiteDatabase databaseReadOnly) {
		this.databaseReadWrite = databaseReadWrite;
		this.databaseReadOnly = databaseReadOnly;
    }

    public UUID GetDeviceId() throws ExceptionDatabaseLayer {
        UUID deviceId = null;
        Cursor cursor = null;
        try {
            cursor = this.databaseReadOnly.rawQuery("SELECT DeviceGuid FROM Device", null);
            
            if(cursor.getCount() > 0)
            {
	            cursor.moveToFirst();
	            deviceId = UUID.fromString(cursor.getString(0));
            }
            
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        }
        finally {
        	if(cursor != null) {
        		cursor.close();
        	}
        }
        
        return deviceId;
    }
    
    public DeviceLocation GetDeviceLocation(int statusType) throws ExceptionDatabaseLayer {
        DeviceLocation deviceLocation = null;
        Cursor cursor = null;
        
        try {
            String query = 
            		"SELECT Latitude, Longitude, CountryName, CountryCode, CountryAdminAreaName, CountryAdminAreaCode, DateCreated FROM Device WHERE Status = ?"; 
                                    
            cursor = this.databaseReadOnly.rawQuery(query, new String[] {
            		Integer.toString(statusType)
            });
            
            if(cursor.getCount() > 0)
            {
            	cursor.moveToFirst();
            	
	            deviceLocation = new DeviceLocation
			         ( 
			            cursor.getDouble(0),
			            cursor.getDouble(1),
			            cursor.getString(2),
			            cursor.getString(3),
			            cursor.getString(4),
			            cursor.getString(5),
			            new Date(cursor.getLong(6))
			         );
            }
            
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        }
        finally {
        	if(cursor != null) {
        		cursor.close();
        	}
        }
        
        return deviceLocation;
    }
    
    public User GetUser(UUID applicationId, int statusType) throws ExceptionDatabaseLayer {
        User user = null;
        Cursor cursor = null;
        try {
            String query = "SELECT ID, applicationGuid, SessionId, DateCreated, Age, Sex, Status, Version " +
            		"FROM User WHERE ApplicationGuid = ? AND ('0' = ? OR Status = ?)";
                
            cursor = this.databaseReadOnly.rawQuery(query, new String[] {
            		applicationId.toString(),
            		Integer.toString(statusType),
            		Integer.toString(statusType)
            });
            
            if(cursor.getCount() > 0)
            {
            	cursor.moveToFirst();
	            user = new User
		            (
		                cursor.getInt(0),
		                cursor.getInt(4),
		                cursor.getInt(5),
		                cursor.getInt(6),
		                UUID.fromString(cursor.getString(1)),
		                new Date(cursor.getLong(3)),
		                UUID.fromString(cursor.getString(2)),
		                cursor.getString(7)
		             );
            }
                
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        }
        finally {
        	if(cursor != null) {
        		cursor.close();
        	}
        }
        
        return user;
    }
    
    public void Save(User user) throws ExceptionDatabaseLayer {
        try {
        	synchronized(this.databaseReadWrite) {
	            String query =  
	                 "INSERT INTO User (ApplicationGuid, DateCreated, Age, Sex, Status, Version, SessionId)"
	                         + "VALUES (?,?,?,?,?,?,?)";
	            
	            this.databaseReadWrite.execSQL(query, new String[] {
		            user.ApplicationId.toString(),
		            Long.toString(user.DateCreated.getTime()),
		            Integer.toString(user.Age),
		            Integer.toString(user.Sex),
		            Integer.toString(user.Status),
		            user.Version,
		            user.SessionId.toString()
	            });
        	}
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        }
    }
    
    public void Update(User user, int statusType) throws ExceptionDatabaseLayer {
        try {
        	synchronized(this.databaseReadWrite) {
	            String query =
	        		"UPDATE User SET Status = ? WHERE Id = ?";
	            
	            this.databaseReadWrite.execSQL(query, new String[] {
		            Integer.toString(statusType),
		            Integer.toString(user.Id)
	            });
        	}
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        }
    }
    
    public void Save(DeviceLocation deviceLocation, int statusType) throws ExceptionDatabaseLayer {
        try {
        	synchronized(this.databaseReadWrite) {
	            String query =
	            		"UPDATE Device SET Latitude = ?, Longitude = ?, CountryName = ?, CountryCode = ?, CountryAdminAreaName = ?, CountryAdminAreaCode = ?, Status = ?";
	            
	            this.databaseReadWrite.execSQL(query, new String[] {
	            	Double.toString(deviceLocation.Latitude),
	            	Double.toString(deviceLocation.Longitude),
	            	deviceLocation.CountryName,
	                deviceLocation.CountryCode,
	                deviceLocation.CountryAdminName,
	                deviceLocation.CountryAdminCode,
	            	Integer.toString(statusType)
	            });
        	}
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        }
    }
    
    public void SaveDeviceId(UUID deviceId, Date dateCreated) throws ExceptionDatabaseLayer {
        try {
        	synchronized(this.databaseReadWrite) {
	            String query =
	        		"INSERT INTO Device (DeviceGuid, DateCreated, Status, Latitude, Longitude, CountryName, CountryCode, CountryAdminAreaName, CountryAdminAreaCode)"
	                + "VALUES (?,?,0,0,0,null,null,null,null)";
	            
	            this.databaseReadWrite.execSQL(query, new String[] {
	            	deviceId.toString(),
	            	Long.toString(dateCreated.getTime())
	            });
        	}
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        }
    }

	public ApplicationMeta LoadApplication(UUID applicationId)
			throws ExceptionDatabaseLayer {
        ApplicationMeta applicationState = null;
        
        Cursor cursor = null;
        try {
            cursor = this.databaseReadOnly.rawQuery("SELECT ApplicationGuid, DateCreated, ApplicationState, " + 
            		"SessionId, Version, Upgraded, OptStatus  FROM Application WHERE ApplicationGuid = ?",
            		new String[] {
            		applicationId.toString()
            });
            
            if(cursor.getCount() > 0)
            {
	            cursor.moveToFirst();
	            
	            if(cursor.getCount() > 0)
	            {
		            applicationState = new ApplicationMeta
			             ( 
			                UUID.fromString(cursor.getString(0)),
			            	cursor.getInt(2),
			            	UUID.fromString(cursor.getString(3)),
			                new Date(cursor.getLong(1)),
			                cursor.getString(4),
			                cursor.getInt(5) == 1 ? true : false,
			                cursor.getInt(6)
			             );
	            }
            }
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        }
        finally {
        	if(cursor != null) {
        		cursor.close();
        	}
        }
        
        return applicationState;
	}

	public void Update(ApplicationMeta applicationMeta)
			throws ExceptionDatabaseLayer {
		try {
			String query = "UPDATE Application SET ApplicationState = ?, SessionId = ?, Version = ?, Upgraded = ?," 
					+ " OptStatus = ? WHERE ApplicationGuid = ?";
			
			synchronized(this.databaseReadWrite) {
				this.databaseReadWrite.execSQL(query, new String[] {
						Integer.toString(applicationMeta.State),
						applicationMeta.SessionId.toString(),
						applicationMeta.Version,
						applicationMeta.Upgraded ? "1" : "0",
						Integer.toString(applicationMeta.OptStatus),
						applicationMeta.Id.toString()
				});
			}
			
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        }
	}

	public void Save(ApplicationMeta applicationMeta)
			throws ExceptionDatabaseLayer {
		String query = "INSERT INTO Application (ApplicationGuid, ApplicationState, DateCreated, SessionId, Version, " 
			+ "Upgraded, OptStatus) values (?,?,?,?,?,?,?)";
		
		synchronized(this.databaseReadWrite) {
			this.databaseReadWrite.execSQL(query, new String[] {
					applicationMeta.Id.toString(),
					Integer.toString(applicationMeta.State),
					Long.toString(applicationMeta.DateCreated.getTime()),
					applicationMeta.SessionId != null ? applicationMeta.SessionId.toString() : null,
					applicationMeta.Version,
					applicationMeta.Upgraded ? "1" : "0",
					Integer.toString(applicationMeta.OptStatus),
			});
		}
	}

	public PluginMeta LoadPlugin() 
			throws ExceptionDatabaseLayer {
		
    	PluginMeta pluginMeta = null;
    	Cursor cursor = null;
    	
    	try { 		
            cursor = this.databaseReadOnly.rawQuery("SELECT schemaVersionNumeric FROM Meta", null);
            
            if(cursor.getCount() > 0)
            {
	            cursor.moveToFirst();
	            
	            if(cursor.getCount() > 0)
	            {
	            	pluginMeta = new PluginMeta
			             ( 
		            		 cursor.getInt(0)
			             );
	            }
            }
	       
    	} catch(Exception ex) {
    		throw new ExceptionDatabaseLayer(ex);
    	} finally {
    		if(cursor != null) {
    			cursor.close();
    		}
    	}
    	
    	return pluginMeta;
	}

	public void Save(PluginMeta pluginMeta) 
			throws ExceptionDatabaseLayer {
		synchronized(this.databaseReadWrite) {
		    try {
	            this.databaseReadWrite.execSQL("INSERT INTO Meta (schemaVersionNumeric) VALUES (?)", new String[] {
	            	Integer.toString(pluginMeta.SchemaVersionNumeric)	
	            });
		    } catch(Exception ex) {
		    	throw new ExceptionDatabaseLayer(ex);
		    }
		}
	}

	public void Update(PluginMeta pluginMeta) 
			throws ExceptionDatabaseLayer {
		synchronized(this.databaseReadWrite) {		
	        try {
	            this.databaseReadWrite.execSQL("UPDATE Meta SET schemaVersionNumeric = ?", new String[] {
	            	Integer.toString(pluginMeta.SchemaVersionNumeric)
	            });
	        } catch(Exception ex) {
	        	throw new ExceptionDatabaseLayer(ex);
	        }
	    }
	}
}
