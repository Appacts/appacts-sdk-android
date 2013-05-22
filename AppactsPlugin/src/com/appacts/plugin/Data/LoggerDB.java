package com.appacts.plugin.Data;

import java.util.Date;
import java.util.UUID;

import com.appacts.plugin.Data.Interfaces.ILogger;
import com.appacts.plugin.Handlers.Utils;
import com.appacts.plugin.Models.AnalyticsSystem;
import com.appacts.plugin.Models.Crash;
import com.appacts.plugin.Models.DeviceGeneralInformation;
import com.appacts.plugin.Models.ErrorItem;
import com.appacts.plugin.Models.EventItem;
import com.appacts.plugin.Models.ExceptionDatabaseLayer;
import com.appacts.plugin.Models.ExceptionDescriptive;
import com.appacts.plugin.Models.FeedbackItem;
import com.appacts.plugin.Models.SystemError;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LoggerDB implements ILogger {
	
	private final SQLiteDatabase databaseReadWrite;
    private final SQLiteDatabase databaseReadOnly;
	
	public LoggerDB(SQLiteDatabase databaseReadWrite, SQLiteDatabase databaseReadOnly) {
		this.databaseReadWrite = databaseReadWrite;
		this.databaseReadOnly = databaseReadOnly;
    }
	
	public Crash GetCrash(UUID applicationId) throws ExceptionDatabaseLayer {
        Crash crash = null;
        
        Cursor cursor = null;
        try {
            String query = "SELECT ID, applicationGuid, SessionId, DateCreated, Version FROM Crash WHERE applicationGuid = ?";
                
            cursor = this.databaseReadOnly.rawQuery(query, new String[] {
            		applicationId.toString()
            });
            
            if(cursor.getCount() > 0)
            {
	            cursor.moveToFirst();
	            
	            crash = new Crash
		            (
		                cursor.getInt(0), 
		                UUID.fromString(cursor.getString(1)), 
		                new Date(cursor.getLong(3)),
		                UUID.fromString(cursor.getString(2)),
		                cursor.getString(4)
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
        
        return crash;
    }
	
	public ErrorItem GetErrorItem(UUID applicationId)  throws ExceptionDatabaseLayer {
        ErrorItem errorItem = null;
        
        Cursor cursor = null;
        try {
            String query = "SELECT ID, applicationGuid, SessionId, DateCreated, Data, " 
            		+ "EventName, AvailableFlashDriveSize, AvailableMemorySize, Battery, NetworkCoverage, " 
            		+ "ErrorMessage, ErrorStackTrace, ErrorSource, ErrorData, ScreenName, Version FROM Error WHERE applicationGuid = ?";
            
            cursor = this.databaseReadOnly.rawQuery(query, new String[] {
            		applicationId.toString()
            });
            
            if(cursor.getCount() > 0)
            {
	            cursor.moveToFirst();
	            
	            errorItem = new ErrorItem
		            (
		                cursor.getInt(0),
		                UUID.fromString(cursor.getString(1)),
		                cursor.getString(14),
		                cursor.getString(4),
		                new DeviceGeneralInformation(cursor.getLong(6), cursor.getLong(7), cursor.getInt(8), cursor.getInt(9)),
		                cursor.getString(5),
		                new ExceptionDescriptive(cursor.getString(10)),
		                new Date(cursor.getLong(3)),
		                UUID.fromString(cursor.getString(2)),
		                cursor.getString(15)
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
        
        return errorItem;
    }
	
	public EventItem GetEventItem(UUID applicationId)  throws ExceptionDatabaseLayer {
        EventItem eventItem = null;

        Cursor cursor = null;
        try {
            String query = "SELECT ID, applicationGuid, SessionId, DateCreated, Data, " 
            		+ "Event, EventName, Length, ScreenName, Version FROM Event WHERE applicationGuid = ?";
            
            cursor = this.databaseReadOnly.rawQuery(query, new String[] {
            		applicationId.toString()
            });
            
            if(cursor.getCount() > 0)
            {
	            cursor.moveToFirst();
	            
	            eventItem = new EventItem
                    (
                        cursor.getInt(0),
                        UUID.fromString(cursor.getString(1)),
                         cursor.getString(8),
                         cursor.getString(4),
                         cursor.getInt(5),
                         cursor.getString(6),
                         cursor.getLong(7),
                         new Date(cursor.getLong(3)),
                         UUID.fromString(cursor.getString(2)),
                         cursor.getString(9)
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
        
        return eventItem;
    }
    
    public FeedbackItem GetFeedbackItem(UUID applicationId) throws ExceptionDatabaseLayer {
        FeedbackItem feedbackItem = null;

        Cursor cursor = null;
        try {            
            String query = "SELECT ID, applicationGuid, SessionId, DateCreated, " 
            		+ "ScreenName, Feedback, FeedbackRating, Version FROM Feedback WHERE applicationGuid = ?";
                
            cursor = this.databaseReadOnly.rawQuery(query, new String[] {
            		applicationId.toString()
            });
            
            if(cursor.getCount() > 0)
            {
	            cursor.moveToFirst();
	            
	            feedbackItem = new FeedbackItem
	                (
	                	cursor.getInt(0),
	                    UUID.fromString(cursor.getString(1)),
	                     cursor.getString(4),
	                     cursor.getString(5),
	                     cursor.getInt(6),
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
        
        return feedbackItem;
    }
    
    public SystemError GetSystemError(UUID applicationId)  throws ExceptionDatabaseLayer {
        SystemError systemError = null;
        Cursor cursor = null;
        
        try {
            String query = "SELECT ID, applicationGuid, DateCreated, " +
                " ErrorMessage, Platform, SystemVersion, Version FROM SystemError WHERE applicationGuid = ?";
                
            cursor = this.databaseReadOnly.rawQuery(query, new String[] {
            		applicationId.toString()
            });
            
            if(cursor.getCount() > 0)
            {
	            cursor.moveToFirst();
	            
	            systemError = new SystemError
		            (
		            	cursor.getInt(0),
		                UUID.fromString(cursor.getString(1)),
		                new ExceptionDescriptive(cursor.getString(3)),
		                new AnalyticsSystem(cursor.getInt(4), cursor.getString(5)),
		                new Date(cursor.getLong(2)),
		                cursor.getString(6)
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
        
        return systemError;
    }
    
    public void Remove(EventItem eventItem) throws ExceptionDatabaseLayer {
        try {
        	synchronized(this.databaseReadWrite) {
        		this.databaseReadWrite.execSQL("DELETE FROM Event WHERE Id = ?", new String[] {
        				 Integer.toString(eventItem.Id)
        		});
        	}
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        }
    }
    
    public void Remove(FeedbackItem feedbackItem)  throws ExceptionDatabaseLayer {
        try {
        	synchronized(this.databaseReadWrite) {
	        	this.databaseReadWrite.execSQL("DELETE FROM Feedback WHERE Id = ?", new String[] {
	        			Integer.toString(feedbackItem.Id)
	        	});
        	}
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        }
    }
    
    public void Remove(ErrorItem errorItem) throws ExceptionDatabaseLayer {
        try {
        	synchronized(this.databaseReadWrite) {    
	            this.databaseReadWrite.execSQL("DELETE FROM Error WHERE Id = ?", new String[] {
	            		 Integer.toString(errorItem.Id)
	            });
        	}
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        }
    }
    
    public void Remove(SystemError systemError) throws ExceptionDatabaseLayer {
        try {
        	synchronized(this.databaseReadWrite) {
	            this.databaseReadWrite.execSQL("DELETE FROM SystemError WHERE Id = ?", new String[] {
	            		Integer.toString(systemError.Id)
	            });
        	}
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        }
    }
    
    public void Remove(Crash crash) throws ExceptionDatabaseLayer {
        try {
        	synchronized(this.databaseReadWrite) {
	            this.databaseReadWrite.execSQL("DELETE FROM Crash WHERE Id = ?", new String[] {
	            		Integer.toString(crash.Id)
	            });
        	}
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        }
    }
    
    public void Save(EventItem eventItem) throws ExceptionDatabaseLayer {
        try {
        	synchronized(this.databaseReadWrite) {
	            String  query =
	                 "INSERT INTO Event (applicationGuid, DateCreated, Data, Event," +
	                 "EventName, Length, ScreenName, Version, SessionId) VALUES (?,?,?,?,?,?,?,?,?)";
	            
	            this.databaseReadWrite.execSQL(query, new String[] {
		            eventItem.ApplicationId.toString(),
		            Long.toString(eventItem.DateCreated.getTime()),
		            Utils.GetValueNotNull(eventItem.Data),
		            Integer.toString(eventItem.EventType),
		            Utils.GetValueNotNull(eventItem.EventName),
		            Long.toString(eventItem.Length),
		            Utils.GetValueNotNull(eventItem.ScreenName),
		            Utils.GetValueNotNull(eventItem.Version),
		            eventItem.SessionId.toString()
	            });
        	}
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        }
    }
    
    public void Save(ErrorItem errorItem)  throws ExceptionDatabaseLayer {
        try {
        	synchronized(this.databaseReadWrite) {
	            String query =   
	                 "INSERT INTO Error (applicationGuid, DateCreated, ErrorMessage, ErrorStackTrace, ErrorSource, ErrorData, "
	                 + "Data, EventName, AvailableFlashDriveSize, AvailableMemorySize, Battery, NetworkCoverage, ScreenName, Version, SessionId)"
	                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	            
	            this.databaseReadWrite.execSQL(query, new String[] {
		            errorItem.ApplicationId.toString(),
		            Long.toString(errorItem.DateCreated.getTime()),
		            errorItem.Error.getMessage(),
		            Utils.GetValueNotNull(errorItem.Error.StackTrace),
		            Utils.GetValueNotNull(errorItem.Error.Source),
		            Utils.GetValueNotNull(errorItem.Error.Data),
		            Utils.GetValueNotNull(errorItem.Data),
		            Utils.GetValueNotNull(errorItem.EventName),
		            Long.toString(errorItem.DeviceInformation.AvailableFlashDriveSize),
		            Long.toString(errorItem.DeviceInformation.AvailableMemorySize),
		            Integer.toString(errorItem.DeviceInformation.Battery),
		            Integer.toString(errorItem.DeviceInformation.NetworkCoverage),
		            Utils.GetValueNotNull(errorItem.ScreenName),
		            Utils.GetValueNotNull(errorItem.Version),
		            errorItem.SessionId.toString()
	            });
        	}
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        }
    }
    
    public void Save(FeedbackItem feedbackItem)  throws ExceptionDatabaseLayer {
        try {
        	synchronized(this.databaseReadWrite) {
	            String query =   
	                 "INSERT INTO Feedback (applicationGuid, DateCreated, ScreenName, Feedback, FeedbackRating, Version, SessionId)"
	                    + "VALUES (?,?,?,?,?,?,?)";
	            
	            this.databaseReadWrite.execSQL(query, new String[] {
		            feedbackItem.ApplicationId.toString(),
		            Long.toString(feedbackItem.DateCreated.getTime()),
		            Utils.GetValueNotNull(feedbackItem.ScreenName),
		            Utils.GetValueNotNull(feedbackItem.Message),
		            Integer.toString(feedbackItem.Rating),
		            Utils.GetValueNotNull(feedbackItem.Version),
		            feedbackItem.SessionId.toString()
	            });
        	}
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        }
    }
    
    public void Save(SystemError systemError) throws ExceptionDatabaseLayer {
        try {
        	synchronized(this.databaseReadWrite) {
	            String query =   
	                 "INSERT INTO SystemError (applicationGuid, DateCreated, ErrorMessage, ErrorStackTrace, ErrorSource, ErrorData, "
	                 + "Platform, SystemVersion, Version)"
	                    + "VALUES (?,?,?,?,?,?,?,?,?)";
	            
	            this.databaseReadWrite.execSQL(query, new String[] {
		            systemError.ApplicationId.toString(),
		            Long.toString(systemError.DateCreated.getTime()),
		            Utils.GetValueNotNull(systemError.Error.getMessage()),
		            systemError.Error.getStackTrace() == null ? "" : Utils.GetValueNotNull(systemError.Error.getStackTrace().toString()),
            		systemError.Error.getCause() == null ? "" : Utils.GetValueNotNull(systemError.Error.getCause().toString()),
		            Utils.GetValueNotNull(systemError.Error.Data),
		            Integer.toString(systemError.System.DeviceType),
		            Utils.GetValueNotNull(systemError.System.Version),
		            Utils.GetValueNotNull(systemError.Version)
	            });
        	}
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        }
    }
    
    public void Save(Crash crash) throws ExceptionDatabaseLayer {
        try {
        	synchronized(this.databaseReadWrite) {
	            String query = 
	                "INSERT INTO Crash (applicationGuid, DateCreated, Version, SessionId)"
	                + "VALUES (?,?,?,?)";
	
	            this.databaseReadWrite.execSQL(query, new String[] {
	        		crash.ApplicationId.toString(),
	                Long.toString(crash.DateCreated.getTime()),
	                Utils.GetValueNotNull(crash.Version),
	                crash.SessionId.toString()
	            });
        	}
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        }
    }
}
