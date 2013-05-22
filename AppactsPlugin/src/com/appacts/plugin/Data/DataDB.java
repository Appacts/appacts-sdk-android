package com.appacts.plugin.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.appacts.plugin.Data.Interfaces.IData;
import com.appacts.plugin.Models.ExceptionDatabaseLayer;

public class DataDB extends SQLiteOpenHelper implements IData {
	private final String connectionString;
	private final String baseConnectionString;
	private SQLiteDatabase databaseReadWrite = null;
    private SQLiteDatabase databaseReadOnly = null;
    private final Object objectInstanceLock = new Object();
	
	public DataDB(Context context, String connectionString, int databaseVersion) {
		super(context, connectionString, null, databaseVersion);
        this.connectionString = connectionString;
        this.baseConnectionString = context.getApplicationInfo().dataDir +"/databases/";
	}
	
    public void Create() throws ExceptionDatabaseLayer {
    	try {
    		SQLiteDatabase sqliteDatabase = this.getReadableDatabase();
    		sqliteDatabase.close();
    	} catch(Exception ex) {
    		throw new ExceptionDatabaseLayer(ex);
    	}
    }
	
	public void Setup(UUID applicationId) throws ExceptionDatabaseLayer {
		
		SQLiteDatabase database = this.OpenReadWriteConnection();
		
	    try{
	    	String[] sqlBase = new String[] {
	    			"CREATE TABLE 'Crash' ( 'ID' INTEGER PRIMARY KEY, 'applicationGuid' TEXT, 'DateCreated' TIMESTAMP, 'Version' NVARCHAR(64))",
	    			
	    			"CREATE TABLE 'Error' ( 'ID' INTEGER PRIMARY KEY, 'applicationGuid' VARCHAR(36), 'DateCreated' TIMESTAMP," +
	                " 'Data' NVARCHAR(256), 'EventName' NVARCHAR(256),  'AvailableFlashDriveSize' INTEGER, " +
	                " 'AvailableMemorySize' INTEGER, 'Battery' INTEGER, 'NetworkCoverage' INTEGER," +
	                " 'ErrorMessage' NVARCHAR(1024), 'ErrorStackTrace' TEXT, 'ErrorSource' NVARCHAR(1024), 'ErrorData' NVARCHAR(256)," +
	                " 'ScreenName' NVARCHAR(256), 'Version' NVARCHAR(64) " +
	                "  )",
	                
	    			"CREATE TABLE 'Event' ( 'ID' INTEGER PRIMARY KEY, 'applicationGuid' NVARCHAR(36), 'DateCreated' TIMESTAMP," +
	                " 'Data' NVARCHAR(256), 'Event' INTEGER, 'EventName' NVARCHAR(256), 'Length' INTEGER, 'ScreenName' NVARCHAR(256), 'Version' NVARCHAR(64))",
	                
	    			"CREATE TABLE 'Feedback' ( 'ID' INTEGER PRIMARY KEY, 'applicationGuid' NVARCHAR(36), 'DateCreated' TIMESTAMP," +
	                " 'ScreenName' NVARCHAR(256), 'Feedback' TEXT, 'FeedbackRating' INTEGER, 'Version' NVARCHAR(64))",
	                
	    			"CREATE TABLE 'SystemError' ( 'ID' INTEGER PRIMARY KEY, 'applicationGuid' NVARCHAR(36), 'DateCreated' TIMESTAMP," +
	                " 'ErrorMessage' NVARCHAR(1024), 'ErrorStackTrace' TEXT, 'ErrorSource' NVARCHAR(1024), 'ErrorData' NVARCHAR(256)," +
	                " 'Platform' INTEGER, 'SystemVersion' NVARCHAR(64), 'Version' NVARCHAR(64) )",
	                
	    			"CREATE TABLE 'User' (  'ID' INTEGER PRIMARY KEY, 'applicationGuid' NVARCHAR(36), 'DateCreated' TIMESTAMP," +
	                " 'Age' INTEGER, 'Sex' INTEGER, 'Status' INTEGER, 'Version' NVARCHAR(64))",
	                
	    			"CREATE TABLE 'Application' ( 'applicationGuid' NVARCHAR(36), 'DateCreated' TIMESTAMP, 'ApplicationState' INTEGER, 'OptStatus' INTEGER)",
	    			
	    			"CREATE TABLE 'Device' ( 'DeviceGuid' NVARCHAR(36), 'DateCreated' TIMESTAMP, " + 
	                        " 'Status' INTEGER, 'Latitude' NUMERIC(9,6), 'Longitude' NUMERIC(9, 6)," +
	                        " 'CountryName' NVARCHAR(256), 'CountryCode' NVARCHAR(256), 'CountryAdminAreaName' NVARCHAR(256), 'CountryAdminAreaCode' NVARCHAR(256))"
	    	};
	    	
	    	for(int i = 0; i < sqlBase.length; i++) {
	    		database.execSQL(sqlBase[i]);
	    	}
	    	
		} catch (Exception ex) {
			throw new ExceptionDatabaseLayer(ex);
	    }
	}
	
    public boolean Exists()  {
    	boolean exists = false;
    	try{
    		this.OpenReadOnlyConnection();
    		exists = true;
    	}catch(Exception ex){
    		exists = false;
    	}
    	return exists;
    }
    
    public SQLiteDatabase OpenReadWriteConnection() throws ExceptionDatabaseLayer {
    	try {
    		if(this.databaseReadWrite == null) {
    			SQLiteDatabase sqlLiteDatabase = 
        				SQLiteDatabase.openDatabase(this.baseConnectionString + this.connectionString, null, SQLiteDatabase.OPEN_READWRITE);
		    	this.databaseReadWrite = sqlLiteDatabase;
    		}
    	} catch(Exception ex) {
    		throw new ExceptionDatabaseLayer(ex);
    	}
    	return this.databaseReadWrite;
    }
    
    public void CloseReadWriteConnection() throws ExceptionDatabaseLayer {
    	try {
    		if(this.databaseReadWrite != null) {
    			this.databaseReadWrite.close();
    			this.databaseReadWrite = null;
    		}
    	} catch(Exception ex) {
    		throw new ExceptionDatabaseLayer(ex);
    	}
    }
    
    public SQLiteDatabase OpenReadOnlyConnection() throws ExceptionDatabaseLayer {
    	try {
    		if(this.databaseReadOnly == null) {
    			SQLiteDatabase sqlLiteDatabase = 
        				SQLiteDatabase.openDatabase(this.baseConnectionString + this.connectionString, null, SQLiteDatabase.OPEN_READONLY);
		    	this.databaseReadOnly = sqlLiteDatabase;
    		}
    	} catch(Exception ex) {
    		throw new ExceptionDatabaseLayer(ex);
    	}
    	return this.databaseReadOnly;
    }
    
    public void CloseReadOnlyConnection() throws ExceptionDatabaseLayer {
    	try {
    		if(this.databaseReadOnly != null) {
    			this.databaseReadOnly.close();
    			this.databaseReadOnly = null;
    		}
    	} catch(Exception ex) {
    		throw new ExceptionDatabaseLayer(ex);
    	}
    }
    
    public void Dispose() {
		try {
			synchronized (this.objectInstanceLock) {
				this.CloseReadWriteConnection();
				this.CloseReadOnlyConnection();
			}
		}
		catch(Exception ex) { }
    }
	
    public boolean UpgradeSchema(int pluginVersionNumericCurrent, int schemaVersionNumericOld) 
    		throws ExceptionDatabaseLayer {
    	
    	boolean upgraded = false;
    	
    	try {   
    		SQLiteDatabase database = this.OpenReadWriteConnection();    		
    		
	    	if(pluginVersionNumericCurrent != schemaVersionNumericOld) {
	    		
	    		ArrayList<String> sqlAlter = null;
	    		
		    	if(schemaVersionNumericOld == -1) {
		    		sqlAlter = this.upgradeSchemaAddSessionAndMeta();
		    	}
		    	
		    	if(sqlAlter != null) {
			    	for(int i = 0; i < sqlAlter.size(); i++) {
			    		database.execSQL(sqlAlter.get(i));
			    	}		    	
		    	}
		    	
		    	upgraded = true;	
	    	}
	    	
		} catch(Exception ex) {
			throw new ExceptionDatabaseLayer(ex);
		}  	
		
		return upgraded;
    	
    }
    
    private ArrayList<String> upgradeSchemaAddSessionAndMeta() 
        	throws ExceptionDatabaseLayer {
    	
    	ArrayList<String> sqlAlters = new ArrayList<String>();
    	
    	sqlAlters.addAll(Arrays.asList(new String[] { 
			"ALTER TABLE 'Crash' ADD 'SessionId' NVARCHAR(36)",
			"ALTER TABLE 'Error' ADD 'SessionId' NVARCHAR(36)",
			"ALTER TABLE 'Event' ADD 'SessionId' NVARCHAR(36)",
			"ALTER TABLE 'Feedback' ADD 'SessionId' NVARCHAR(36)",
			"ALTER TABLE 'User' ADD 'SessionId' NVARCHAR(36)",
			"ALTER TABLE Application ADD SessionId NVARCHAR(36)",
			"ALTER TABLE Application ADD Version NVARCHAR(64)",
			"ALTER TABLE Application ADD Upgraded BOOLEAN",
			"CREATE TABLE Meta ('schemaVersionNumeric' INTEGER)",    			
    	}));
    	
    	return sqlAlters;
    }

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
