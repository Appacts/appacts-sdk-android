package com.appacts.plugin;

import java.util.UUID;
import java.util.Vector;

import com.appacts.plugin.Data.DataDB;
import com.appacts.plugin.Data.LoggerDB;
import com.appacts.plugin.Data.SettingsDB;
import com.appacts.plugin.Data.UploadWS;
import com.appacts.plugin.Data.Interfaces.IData;
import com.appacts.plugin.Data.Interfaces.ILogger;
import com.appacts.plugin.Data.Interfaces.ISettings;
import com.appacts.plugin.Data.Interfaces.IUpload;
import com.appacts.plugin.Device.DeviceDynamicInformation;
import com.appacts.plugin.Device.DeviceInformation;
import com.appacts.plugin.Device.Platform;
import com.appacts.plugin.Device.Interfaces.IDeviceDynamicInformation;
import com.appacts.plugin.Device.Interfaces.IDeviceInformation;
import com.appacts.plugin.Device.Interfaces.IPlatform;
import com.appacts.plugin.Handlers.HttpConnectionManager;
import com.appacts.plugin.Handlers.Utils;
import com.appacts.plugin.Interfaces.IAnalytics;
import com.appacts.plugin.Models.AnalyticsSystem;
import com.appacts.plugin.Models.ApplicationMeta;
import com.appacts.plugin.Models.ApplicationStateType;
import com.appacts.plugin.Models.Crash;
import com.appacts.plugin.Models.DeviceLocation;
import com.appacts.plugin.Models.DeviceType;
import com.appacts.plugin.Models.ErrorItem;
import com.appacts.plugin.Models.EventItem;
import com.appacts.plugin.Models.EventType;
import com.appacts.plugin.Models.ExceptionDatabaseLayer;
import com.appacts.plugin.Models.ExceptionDescriptive;
import com.appacts.plugin.Models.ExceptionWebServiceLayer;
import com.appacts.plugin.Models.FeedbackItem;
import com.appacts.plugin.Models.OptStatusType;
import com.appacts.plugin.Models.PluginMeta;
import com.appacts.plugin.Models.RatingType;
import com.appacts.plugin.Models.Session;
import com.appacts.plugin.Models.SexType;
import com.appacts.plugin.Models.StatusType;
import com.appacts.plugin.Models.SystemError;
import com.appacts.plugin.Models.UploadType;
import com.appacts.plugin.Models.User;
import com.appacts.plugin.Models.WebServiceResponseType;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public final class Analytics implements IAnalytics {
	
	private final String connectionString = "appacts.db";
	private final int databaseVersion = 1;
    private String baseUrl = "";
    private final boolean debugMode = true;
  
    private UUID applicationId;
    private String applicationVersion;
    private UUID sessionId;
    
    private ILogger iLogger;
    private ISettings iSettings;
    private IData iData;
    private IUpload iUpload;
    
    private IDeviceInformation iDeviceInformation;
    private IDeviceDynamicInformation iDeviceDynamicInformation;
    private IPlatform iPlatform;
    
    private final Vector<Session> vectorScreenOpen = new Vector<Session>();
    private final Vector<Session> vectoreContentLoading = new Vector<Session>();
    
    private boolean authenticationFailure = false;
    private boolean databaseExists = false;
    private boolean itemsWaitingToBeUploaded = true;
    private int numberOfItemsWaitingToBeUploaded = 0;
    private int optStatusType = OptStatusType.OptIn.GetCode();
    private boolean uploadWhileUsing = false;
    
    //these three upload types only need to be uploaded once succesfully
    private boolean userProcessed = false;
    private boolean deviceLocationProcessed = false;
    private boolean upgradedProcessed = false;
    
    private Thread threadUpload = null;
    private Object threadUploadLock = new Object();
    private Object threadIsUploadingLock = new Object();
    private boolean threadIsUploading = false;
    private Session session = null;
    private boolean threadUploadInterrupted = false;
    private boolean stopped = false;
    private boolean started = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    
    private Context context;
    
	public Analytics() {      

	}

	public void Start(Context context, String baseUrl, String applicationId, UploadType uploadType) 
			throws Exception {
		if(!this.started) {
			
			if(baseUrl == null || baseUrl.length() == 0) {
				throw new Exception("You need to specify baseUrl, i.e. your server api url http://yoursite.com/api/");
			}
			
			this.baseUrl = baseUrl;
			String appVersion;
			
			try {
				appVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0 ).versionName;
			} catch(Exception ex) { 
				appVersion = "0";
			}	
			
			this.init(context, UUID.fromString(applicationId), appVersion);
			
			this.setThreadUploadInterrupted(false);
			this.started = true;
			this.stopped = false;
			
			if(uploadType == UploadType.WhileUsingAsync) {
				this.UploadWhileUsingAsync();
			}			
		}
	}
	
	public void Start(Context context, String baseUrl, String applicationId) 
			throws Exception {
		this.Start(context, baseUrl, applicationId, UploadType.WhileUsingAsync);
	}
        
    public void UploadWhileUsingAsync() {
        if(!this.getUploadWhileUsing()) {
        	
            this.phoneStateListener = new PhoneStateListener() {
                public void onDataConnectionStateChanged(int state) {
                	if(state == TelephonyManager.DATA_CONNECTED)
                	{
                		UploadIntelligent();
                	}
                }
            };

            this.telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
            
            this.setUploadWhileUsng(true);
        }
        this.UploadIntelligent();
    }
	
	public void UploadManual() {
        if(this.getItemsWaitingToBeUploaded() && HttpConnectionManager.HasNetworkCoverage(this.context) && !this.getAuthenticationFailure() 
                && this.optStatusType == OptStatusType.OptIn.GetCode() 
                && !this.getThreadUploadInterrupted() && this.databaseExists && this.started) {
    		synchronized(this.threadUploadLock) {
    			if(this.getThreadIsUploading()) {
	                this.threadUpload = new Thread(new Runnable() {
	                    public void run() {
	                        Upload();
	                    }
	                });
	                this.threadUpload.start();
	                this.threadUpload.setPriority(Thread.MIN_PRIORITY);
    			}
    		}
    	}
	}
	
    public void LogError(String screenName, String eventName, String data, ExceptionDescriptive ex) {
         if(this.databaseExists) {
            try {
                ErrorItem errorItem = new ErrorItem(this.applicationId, screenName, data,
                    this.iDeviceDynamicInformation.GetDeviceGeneralInformation(), eventName, ex, this.sessionId, this.applicationVersion);
                
                this.iLogger.Save(errorItem);
                
                this.SetItemsWaitingToBeUploaded();
                
                if(this.getUploadWhileUsing()) {
                    this.UploadIntelligent();
                }
                
            } catch(ExceptionDatabaseLayer exceptionDatabaseLayer) {
            	this.logSystemError(exceptionDatabaseLayer);
            }
        }
    }
    
    public void LogEvent(String screenName, String eventName, String data) {
         if(this.started && this.databaseExists && this.optStatusType == OptStatusType.OptIn.GetCode()) {
            try {
                EventItem eventItem = new EventItem(this.applicationId, screenName, data, 
                    EventType.Event.GetCode(), eventName, 0, this.sessionId, this.applicationVersion);
                
                this.iLogger.Save(eventItem);
                
                this.SetItemsWaitingToBeUploaded();
                
                if(this.getUploadWhileUsing()) {
                    this.UploadIntelligent();
                }
                
            } catch(ExceptionDatabaseLayer ex) {
            	this.logSystemError(ex);
            }
        }
    }
    
    public void LogEvent(String screenName, String eventName) {
    	this.LogEvent(screenName, eventName, null);
    }
    
    public void LogFeedback(String screenName, RatingType rating, String comment) throws ExceptionDatabaseLayer {
        try {
            FeedbackItem feedbackItem = new FeedbackItem(this.applicationId, screenName, 
                comment, rating.GetCode(), this.sessionId, this.applicationVersion);
            
            this.iLogger.Save(feedbackItem);
            
            this.SetItemsWaitingToBeUploaded();
            
            if(this.getUploadWhileUsing()) {
                this.UploadIntelligent();
            }
            
        } catch(ExceptionDatabaseLayer ex) {
        	this.logSystemError(ex);
            throw ex;
        }
    }
    
    public void ScreenOpen(String screenName) {
         if(this.started && this.databaseExists && this.optStatusType == OptStatusType.OptIn.GetCode()) {
            try {
            	
            	Session session = new Session(screenName);
            	
            	synchronized(this.vectorScreenOpen) {
	            	if(!this.vectorScreenOpen.contains(session)) {
		                this.vectorScreenOpen.addElement(session);
		                
		                EventItem eventItem = new EventItem(this.applicationId, screenName, null, 
		                    EventType.ScreenOpen.GetCode(), null, 0, this.sessionId, this.applicationVersion);
		                
		                this.iLogger.Save(eventItem);
		                
		                this.SetItemsWaitingToBeUploaded();
		                
		                if(this.getUploadWhileUsing()) {
		                    this.UploadIntelligent();
		                }
	            	}
            	}
                
            } catch(ExceptionDatabaseLayer ex) {
            	this.logSystemError(ex);
            }
        }
    }    
    
    
    public void ScreenClosed(String screenName) {
        if(this.started && this.databaseExists && this.optStatusType == OptStatusType.OptIn.GetCode()) {
            try {
                
                long miliSeconds = 0;
                int index = -1;
                
                synchronized(this.vectorScreenOpen) {
	                for(int i = 0; i < this.vectorScreenOpen.size(); i++) {
	                    Session session = (Session)this.vectorScreenOpen.elementAt(i);
	                    if(session.Name.equals(screenName)) {
	                        index = i;
	                        miliSeconds = session.End();
	                        break;
	                    }
	                }
	                
	                if(index != -1) {
	                    this.vectorScreenOpen.removeElementAt(index);
	                
		                EventItem eventItem = new EventItem(this.applicationId, screenName, null, 
		                    EventType.ScreenClosed.GetCode(), null, miliSeconds, this.sessionId, this.applicationVersion);
		                
		                this.iLogger.Save(eventItem);
		                
		                this.SetItemsWaitingToBeUploaded();
		                
		                if(this.getUploadWhileUsing()) {
		                    this.UploadIntelligent();
		                }
	                }
                }
            } catch(ExceptionDatabaseLayer ex) {
            	this.logSystemError(ex);
            }
        }
    }
    
    public void ContentLoading(String screenName, String contentName) {
        if(this.started && this.databaseExists && this.optStatusType == OptStatusType.OptIn.GetCode()) {
            try {
            	Session session = new Session(screenName.concat(contentName));
            	
            	synchronized(this.vectoreContentLoading) {
	            	if(!this.vectoreContentLoading.contains(session)) {
	            		
	            		this.vectoreContentLoading.addElement(session);
	            		
		                EventItem eventItem = new EventItem(this.applicationId, screenName, null, 
		                    EventType.ContentLoading.GetCode(), contentName, 0, this.sessionId, this.applicationVersion);
		                this.iLogger.Save(eventItem);
		                
		                this.SetItemsWaitingToBeUploaded();
		                
		                if(this.getUploadWhileUsing()) {
		                    this.UploadIntelligent();
		                }
	            	}
            	}
            } catch(ExceptionDatabaseLayer ex) {
            	this.logSystemError(ex);
            }
        }
    }
    
    public void ContentLoaded(String screenName, String contentName) {
        if(this.started && this.databaseExists && this.optStatusType == OptStatusType.OptIn.GetCode()) {
            try {
                
                long miliSeconds = 0;
                int index = -1;
                
                synchronized(this.vectoreContentLoading) {
                	
                	String sessionName = screenName.concat(contentName);
                	
	                for(int i = 0; i < this.vectoreContentLoading.size(); i++) {
	                    Session session = (Session)this.vectoreContentLoading.elementAt(i);
	                    if(session.Name.equals(sessionName)) {
	                        index = i;
	                        miliSeconds = session.End();
	                        break;
	                    }
	                }
	                
	                if(index != -1) {
	                    this.vectoreContentLoading.removeElementAt(index);
	                
		                EventItem eventItem = new EventItem(this.applicationId, screenName, null, 
		                    EventType.ContentLoaded.GetCode(), contentName, miliSeconds, this.sessionId, this.applicationVersion);
		                
		                this.iLogger.Save(eventItem);
		                
		                this.SetItemsWaitingToBeUploaded();
		                
		                if(this.getUploadWhileUsing()) {
		                    this.UploadIntelligent();
		                }
	                }
                }
            } catch(ExceptionDatabaseLayer ex) {
            	this.logSystemError(ex);
            }
        }
    }
    
    
    public void SetUserInformation(int age, SexType sexType) throws ExceptionDatabaseLayer {
        try {
            User user = new User(age, sexType.GetCode(), StatusType.Pending.GetCode(), this.applicationId, this.sessionId, this.applicationVersion);
            this.iSettings.Save(user);
            
            this.SetItemsWaitingToBeUploaded();
            
            if(this.getUploadWhileUsing()) {
                this.UploadIntelligent();
            }
            
        } catch(ExceptionDatabaseLayer ex) {
        	this.logSystemError(ex);
            throw ex;
        }
    }
    
    public boolean IsUserInformationSet() {
        boolean isUserInformationSet = true;
       
        if(this.started && this.databaseExists) {
            try {
                if(this.iSettings.GetUser(this.applicationId, StatusType.All.GetCode()) == null) {
                    isUserInformationSet = false;
                }
            } catch(ExceptionDatabaseLayer ex) {
            	this.logSystemError(ex);
            }
        }
        
        return isUserInformationSet;
    }
    
    public void SetOptStatus(OptStatusType optStatusType) {
        try {
        	this.optStatusType = optStatusType.GetCode();
        	
            if(this.started && this.databaseExists) {
            	ApplicationMeta applicationMeta = this.iSettings.LoadApplication(this.applicationId);
                applicationMeta.OptStatus = optStatusType.GetCode();
                this.iSettings.Update(applicationMeta);
            }
        } catch(ExceptionDatabaseLayer ex) {
        	this.logSystemError(ex);
        }
    }

    public int GetOptStatus() {
        int optStatusType = OptStatusType.None.GetCode();
        try {
            if(this.started && this.databaseExists) {
                if(this.databaseExists) {
                	ApplicationMeta applicationMeta = this.iSettings.LoadApplication(this.applicationId);
                    optStatusType = applicationMeta.OptStatus;
                }
            }
        } catch(ExceptionDatabaseLayer ex) {
        	this.logSystemError(ex);
        }
        return optStatusType;
    }
    
    public void Stop() {
    	if(this.started) {
	    	try {
	    		this.setThreadUploadInterrupted(true);
	        	
	        	//Removes listener
	            this.phoneStateListener = new PhoneStateListener() {
	            	
	            };
	            
	            if(this.getThreadIsUploading()) {
	                this.threadUpload.interrupt();
	            }
	        } catch(Exception ex) { 
	            System.out.println( ex.toString() ); 
	        }
	    	
	        if(this.databaseExists) {
	            try {
	                EventItem eventItem = new EventItem(this.applicationId, null, 
	                    null, EventType.ApplicationClose.GetCode(), null, this.session.End(), this.sessionId, this.applicationVersion);
	                
	                this.iLogger.Save(eventItem);
	                
	                ApplicationMeta applicationState = this.iSettings.LoadApplication(this.applicationId);
	                applicationState.State = ApplicationStateType.Closed.GetCode();
	                
	                if(debugMode) {
	                	//need to see the crash upload
	                	applicationState.State = ApplicationStateType.Open.GetCode();
	                }
	                
	                applicationState.SessionId = this.sessionId;
	                this.iSettings.Update(applicationState);
	                
	            } catch(ExceptionDatabaseLayer ex) {
	            	this.logSystemError(ex);
	            }
	            
	            try {
	            	synchronized(this.threadIsUploadingLock) {
		            	if(this.getThreadIsUploading()) {
			            	while(this.getThreadIsUploading()) {
			            		this.threadIsUploadingLock.wait();
			            	}
		            	}
	            	}
	            } catch(Exception ex) {
	            	
	            } finally {
	            	this.iData.Dispose();
	            }
	            
	            this.stopped = true;
	            this.started = false;
	            
	            System.out.println( "analytics stop" );
	        }
    	}
    }
    
    
	private void init(Context context, UUID applicationId, String appVersion) {   
		
        this.applicationId = applicationId;
        this.applicationVersion = appVersion;
        this.sessionId = UUID.randomUUID();

        if(debugMode) {
            System.out.println("Session Id");
            System.out.println(this.sessionId.toString());
        }
        
        this.iData = new DataDB(context, connectionString, databaseVersion);
        this.iUpload = new UploadWS(context, baseUrl);
        
        this.iDeviceInformation = new DeviceInformation(context);
        this.iDeviceDynamicInformation = new DeviceDynamicInformation(context);
        this.iPlatform = new Platform(context);
        
        this.context = context;
        
        this.telephonyManager =
    			(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);		
		
        try {
            this.initDatabase();
            
            this.databaseExists = true;
        } catch(ExceptionDatabaseLayer ex) {
        	System.out.println("Init");
        	System.out.println(ex.toString());
        }
        
    
        if(this.databaseExists) {
        	
        	ApplicationMeta applicationMeta = null; 
        	boolean applicationInitialSetup = false;
        	
        	try {
        		applicationMeta = this.iSettings.LoadApplication(this.applicationId);
        		
        		if(applicationMeta == null) {
        			applicationMeta = new ApplicationMeta(this.applicationId, ApplicationStateType.Closed.GetCode(), 
        					Utils.GetDateTimeNow(), OptStatusType.OptIn.GetCode());
        			
        			this.iSettings.Save(applicationMeta);
        			applicationInitialSetup = true;
        		} 
        		
        	} catch(ExceptionDatabaseLayer ex) {
                this.logSystemError(ex);
            }
        	
        	this.optStatusType = applicationMeta.OptStatus;
        	
	        try {
	        	
	            if(applicationMeta.State == ApplicationStateType.Open.GetCode()) {
	                Crash crash = new Crash(this.applicationId, this.sessionId, this.applicationVersion);
	                this.iLogger.Save(crash);
	            }
	            
	            EventItem eventItem = new EventItem(this.applicationId, null, null, 
	                EventType.ApplicationOpen.GetCode(), null, 0, this.sessionId, this.applicationVersion);
	            this.iLogger.Save(eventItem);
	            
	            
	            applicationMeta.SessionId = this.sessionId;
	            applicationMeta.State = ApplicationStateType.Open.GetCode();
	            
                if(applicationMeta.Version == null || 
                		!applicationMeta.Version.equals(this.applicationVersion)) {
                	applicationMeta.Version = this.applicationVersion;
            		applicationMeta.Upgraded = !applicationInitialSetup;
                }
                
                this.iSettings.Update(applicationMeta);
                
                if(debugMode) {
                	throw new ExceptionDatabaseLayer(new Exception("Error testing"));
                }
                
	        } catch(ExceptionDatabaseLayer ex) {
	        	this.logSystemError(ex);
	        }
        }
        
        this.session = new Session();
	}
    
	private void initDatabase() throws ExceptionDatabaseLayer {
		
		if(!this.iData.Exists()) {
		    this.iData.Create();
		    this.iData.Setup(this.applicationId);
		}
		
		this.iLogger = new LoggerDB(this.iData.OpenReadWriteConnection(), this.iData.OpenReadOnlyConnection());
		this.iSettings = new SettingsDB(this.iData.OpenReadWriteConnection(), this.iData.OpenReadOnlyConnection());
		
		PluginMeta pluginMeta = null;
		
        try {
        	pluginMeta = this.iSettings.LoadPlugin();
        } catch(ExceptionDatabaseLayer ex) {  
        	System.out.println("LoadPlugin");
        	System.out.println(ex.toString());
        }
		
        if(pluginMeta == null) {
        	pluginMeta = new PluginMeta(-1);
        }
        
        int schemaVersionNumericCurrent = this.iDeviceInformation.GetPluginVersionCode();
        
		if(this.iData.UpgradeSchema(this.iDeviceInformation.GetPluginVersionCode(), pluginMeta.SchemaVersionNumeric)) {
			if(pluginMeta.SchemaVersionNumeric == -1) {
				pluginMeta.SchemaVersionNumeric = schemaVersionNumericCurrent;
				this.iSettings.Save(pluginMeta);
			} else {
				pluginMeta.SchemaVersionNumeric = schemaVersionNumericCurrent;
				this.iSettings.Update(pluginMeta);
			}
			
			//need to reopen all connections to re-cache the schema, otherwise you will encounter issues
			this.iData.CloseReadOnlyConnection();
			this.iData.OpenReadWriteConnection();
			
			this.iLogger = new LoggerDB(this.iData.OpenReadWriteConnection(), this.iData.OpenReadOnlyConnection());
			this.iSettings = new SettingsDB(this.iData.OpenReadWriteConnection(), this.iData.OpenReadOnlyConnection());
		}
		
	}
    
    protected void logSystemError(ExceptionDescriptive ex) {
        try {
        	if(this.databaseExists) {
        		
        		if(debugMode) {
            		System.out.println("errorIdentified"); 
            		System.out.println(ex.getMessage()); 
            		System.out.println(ex.StackTrace);
            		System.out.println(ex.Source);
        		}
        		
        		this.iLogger.Save(new SystemError(this.applicationId, ex, 
        				new AnalyticsSystem(this.iDeviceInformation.GetDeviceType(), this.iDeviceInformation.GetPluginVersion()), this.applicationVersion));
    
        		this.SetItemsWaitingToBeUploaded();
        	}
        } catch(ExceptionDatabaseLayer exceptionDatabaseLayer) { 
        	if(debugMode) {
        		System.out.println("errorIdentified while saving error"); 
	    		System.out.println(exceptionDatabaseLayer.getMessage()); 
	    		System.out.println(exceptionDatabaseLayer.StackTrace);
	    		System.out.println(exceptionDatabaseLayer.Source);
        	}
        }
    }
    
    
    protected void SetItemsWaitingToBeUploaded() {
    	this.setNumberOfItemsWaitingToBeUploaded(this.getNumberOfItemsWaitingToBeUploaded()+1);
    	this.setItemsWaitingToBeUploaded(true);
    }
    
    protected void UploadIntelligent() {
        if(this.getItemsWaitingToBeUploaded() && HttpConnectionManager.HasNetworkCoverage(this.context) && !this.getAuthenticationFailure()
                && this.optStatusType == OptStatusType.OptIn.GetCode() && !this.stopped && this.databaseExists && this.started) {
    		synchronized(this.threadUploadLock) {
                System.out.println("upload thread is active:");
                System.out.println(this.getThreadIsUploading());
            
                if(!this.getThreadIsUploading()) {
                	
                	this.setThreadIsUploading(true);
                	
	                this.threadUpload = new Thread(new Runnable() {
	                    public void run() {
	                        Upload();
	                    }
	                });
	                this.threadUpload.setPriority(Thread.MIN_PRIORITY);
	                this.threadUpload.start();
                }
            }
        }
    }
     
    protected void Upload() {
        UUID deviceId = null;
        boolean exceptionWasRaised = false;
        
        if(!this.getThreadUploadInterrupted()) {
	        try {
	            deviceId = iSettings.GetDeviceId();
	            
	            if(deviceId == null) {
	                deviceId = iUpload.Device
	                    (this.applicationId, iDeviceInformation.GetModel(), iPlatform.GetOS(),  
	                        DeviceType.Android.GetCode(), iPlatform.GetCarrier(), this.applicationVersion,
	                        Utils.TimeOffSet(), iDeviceInformation.GetLocale(), iDeviceInformation.GetResolution(),
	                        iDeviceInformation.GetManufacturer());
	
	                iSettings.SaveDeviceId(deviceId, Utils.GetDateTimeNow());   
	            } else {
	            	if(!this.getUpgradeProcessed()) {
		            	  this.setUpgradeProcessed(this.uploadUpgraded(deviceId));
		            	}
	            }
	        } catch(ExceptionDatabaseLayer ex) {
	            System.out.println( ex.toString() );
	            ex.printStackTrace();
	            exceptionWasRaised = true;
	        } catch(ExceptionWebServiceLayer ex) { 
	            System.out.println( ex.toString() );
	            ex.printStackTrace();
	            exceptionWasRaised = true;
	        }
        }
        
        //remember current count before we start processing items, so later we can
        //find out if we need to call upload thread again
    	int numberOfItemsWaitingToBeUploadedBefore = this.getNumberOfItemsWaitingToBeUploaded();
	    
        if(!this.getThreadUploadInterrupted() && !exceptionWasRaised) {
	        try {
	            this.uploadSystemError(deviceId);
	        } catch(ExceptionDatabaseLayer ex) {
	            System.out.println( ex.toString() );
	            System.out.println( "uploadSytemError" );
	            exceptionWasRaised = true;
	        } catch(ExceptionWebServiceLayer ex) { 
	            System.out.println( ex.toString() );
	            System.out.println( "uploadSytemError" );
	            exceptionWasRaised = true;
	        }
        }
        
        if(!this.getThreadUploadInterrupted() && !exceptionWasRaised) {	        
	        try {
	            this.uploadCrash(deviceId);
	        } catch(ExceptionDatabaseLayer ex) {
	            System.out.println( ex.toString() );
	            System.out.println( "uploadCrash" );
	            exceptionWasRaised = true;
	        } catch(ExceptionWebServiceLayer ex) { 
	            System.out.println( ex.toString() );
	            System.out.println( "uploadCrash" );
	            exceptionWasRaised = true;
	        }   
        }
	     
        if(!this.getThreadUploadInterrupted() && !exceptionWasRaised && !this.getUserProcessed()) {
	        try {
	             this.setUserProcessed(this.uploadUser(deviceId));
	        } catch(ExceptionDatabaseLayer ex) {
	            System.out.println( ex.toString() );
	            System.out.println( "uploadUser" );
	            exceptionWasRaised = true;
	        } catch(ExceptionWebServiceLayer ex) { 
	            System.out.println( ex.toString() );
	            System.out.println( "uploadUser" );
	            exceptionWasRaised = true;
	        }
        }
	     
        if(!this.getThreadUploadInterrupted() && !exceptionWasRaised) {
	        try {
	            this.uploadError(deviceId);
	        } catch(ExceptionDatabaseLayer ex) {
	            System.out.println( ex.toString() );
	            System.out.println( "uploadError" );
	            exceptionWasRaised = true;
	        } catch(ExceptionWebServiceLayer ex) { 
	            System.out.println( ex.toString() );
	            System.out.println( "uploadError" );
	            exceptionWasRaised = true;
	        } 
        }
	     
        if(!this.getThreadUploadInterrupted() && !exceptionWasRaised) {
	        try {
	            this.uploadEvent(deviceId);
	        } catch(ExceptionDatabaseLayer ex) {
	            System.out.println( ex.toString() );
	            System.out.println( "uploadEvent" );
	            exceptionWasRaised = true;
	        } catch(ExceptionWebServiceLayer ex) { 
	            System.out.println( ex.toString() );
	            System.out.println( "uploadEvent" );
	            exceptionWasRaised = true;
	        }  
        }
	     
        if(!this.getThreadUploadInterrupted() && !exceptionWasRaised) {
	        try {
	            this.uploadFeedback(deviceId);
	        } catch(ExceptionDatabaseLayer ex) {
	            System.out.println( ex.toString() );
	            System.out.println( "uploadFeedback" );
	            exceptionWasRaised = true;
	        } catch(ExceptionWebServiceLayer ex) { 
	            System.out.println( ex.toString() );
	            System.out.println( "uploadFeedback" );
	            exceptionWasRaised = true;
	        }   
        }
        
        if(!this.getThreadUploadInterrupted() && !exceptionWasRaised && !this.getDeviceLocationProcessed()) {	        
	        try {
	            this.setDeviceLocationProcessed(this.uploadDeviceLocation(deviceId));
	        } catch(ExceptionDatabaseLayer ex) {
	            System.out.println( ex.toString() );
	            System.out.println( "uploadDeviceLocation" );
	            exceptionWasRaised = true;
	        } catch(ExceptionWebServiceLayer ex) { 
	            System.out.println( ex.toString() );
	            System.out.println( "uploadDeviceLocation" );
	            exceptionWasRaised = true;
	        }   
        }
	     
        if(!exceptionWasRaised) {
        	this.uploadSuccesfullyCompleted(numberOfItemsWaitingToBeUploadedBefore);
        }
        
        synchronized(this.threadIsUploadingLock) {
        	this.setThreadIsUploading(false);
        	this.threadIsUploadingLock.notifyAll();
        }
    }
    
    private void uploadSuccesfullyCompleted(int numberOfItemsWaitingToBeUploadedBefore) {
    	if(!threadUploadInterrupted) {
        	if(numberOfItemsWaitingToBeUploadedBefore == this.getNumberOfItemsWaitingToBeUploaded()) {
        		this.setItemsWaitingToBeUploaded(false);
        	}
        }
    }
    
    private void uploadSystemError(UUID deviceId) 
        throws ExceptionDatabaseLayer, ExceptionWebServiceLayer {
        SystemError systemError = null;
        do
        {
            systemError = iLogger.GetSystemError(this.applicationId);
            
            if(systemError != null) {
                int responseCode = iUpload.SystemError(deviceId, systemError);
                    
                if(responseCode == WebServiceResponseType.Ok.GetCode()) {
                    iLogger.Remove(systemError);
                } else if(responseCode == WebServiceResponseType.InactiveAccount.GetCode() 
                    || responseCode ==  WebServiceResponseType.InactiveApplication.GetCode()) {
                	this.setAuthenticationFailure(true);
                    return;
                } else if(responseCode == WebServiceResponseType.GeneralError.GetCode()) {
                    return;
                }
            }
        } while(systemError != null && !this.getThreadUploadInterrupted());
    }
    
    private void uploadCrash(UUID deviceId) 
        throws ExceptionDatabaseLayer, ExceptionWebServiceLayer {
        Crash crash = null;
        do
        {
            crash = iLogger.GetCrash(applicationId);
            
            if(crash != null) {
                int responseCode = iUpload.Crash(deviceId, crash);
                
                if(responseCode == WebServiceResponseType.Ok.GetCode()) {
                    iLogger.Remove(crash);
                } else if(responseCode == WebServiceResponseType.InactiveAccount.GetCode() 
                    || responseCode ==  WebServiceResponseType.InactiveApplication.GetCode()) {
                	this.setAuthenticationFailure(true);
                    return;
                } else if(responseCode == WebServiceResponseType.GeneralError.GetCode()) {
                    return;
                }
            }
        } while(crash != null && !this.getThreadUploadInterrupted());
    }
    
    private boolean uploadUser(UUID deviceId) 
        throws ExceptionDatabaseLayer, ExceptionWebServiceLayer {
       User user = iSettings.GetUser(this.applicationId, StatusType.Pending.GetCode());

       if(user != null && user.Status == StatusType.Pending.GetCode()) {
            int responseCode = iUpload.User(deviceId, user);
    
            if(responseCode == WebServiceResponseType.Ok.GetCode()) {
                iSettings.Update(user, StatusType.Processed.GetCode());
                return true;
            } else if(responseCode == WebServiceResponseType.InactiveAccount.GetCode() 
                || responseCode ==  WebServiceResponseType.InactiveApplication.GetCode()) {
            	this.setAuthenticationFailure(true);
            	return false;
            }
       } else if(user == null) {
    	   return false;
       }
       
       return true;
    }
    
    private void uploadError(UUID deviceId) 
        throws ExceptionDatabaseLayer, ExceptionWebServiceLayer {
        ErrorItem errorItem = null;
        do
        {
            errorItem = iLogger.GetErrorItem(this.applicationId);
            
            if(errorItem != null) {
        
                int responseCode = iUpload.Error(deviceId, errorItem);
                    
                if(responseCode == WebServiceResponseType.Ok.GetCode()) {
                    iLogger.Remove(errorItem);
                } else if(responseCode == WebServiceResponseType.InactiveAccount.GetCode() 
                    || responseCode ==  WebServiceResponseType.InactiveApplication.GetCode()) {
                	this.setAuthenticationFailure(true);
                    return;
                } else if(responseCode == WebServiceResponseType.GeneralError.GetCode()) {
                    return;
                }
            }
        } while(errorItem != null && !this.getThreadUploadInterrupted());
    }
    
    private void uploadEvent(UUID deviceId) 
        throws ExceptionDatabaseLayer, ExceptionWebServiceLayer {
        EventItem eventItem = null;
        do
        {
            eventItem = iLogger.GetEventItem(this.applicationId);
            
            if(eventItem != null) {
                int responseCode = iUpload.Event(deviceId, eventItem);
                    
                if(responseCode == WebServiceResponseType.Ok.GetCode()) {
                    iLogger.Remove(eventItem);
                } else if(responseCode == WebServiceResponseType.InactiveAccount.GetCode()
                    || responseCode ==  WebServiceResponseType.InactiveApplication.GetCode()) {
                	this.setAuthenticationFailure(true);
                    return;
                } else if(responseCode == WebServiceResponseType.GeneralError.GetCode()) {
                    return;
                }
              
            }
        } while(eventItem != null && !this.getThreadUploadInterrupted());
    }
    
    private void uploadFeedback(UUID deviceId) 
        throws ExceptionDatabaseLayer, ExceptionWebServiceLayer {
        FeedbackItem feedbackItem = null;
        do
        {
            feedbackItem = iLogger.GetFeedbackItem(this.applicationId);
            
            if(feedbackItem != null) {
                int responseCode = iUpload.Feedback(deviceId, feedbackItem);

                if(responseCode == WebServiceResponseType.Ok.GetCode()) {
                    iLogger.Remove(feedbackItem);
                } else if(responseCode == WebServiceResponseType.InactiveAccount.GetCode() 
                    || responseCode ==  WebServiceResponseType.InactiveApplication.GetCode()) {
                	this.setAuthenticationFailure(true);
                    return;
                } else if(responseCode == WebServiceResponseType.GeneralError.GetCode()) {
                    return;
                }
            }
        } while(feedbackItem != null && !this.getThreadUploadInterrupted());
    }
    
    private boolean uploadDeviceLocation(UUID deviceId) 
        throws ExceptionDatabaseLayer, ExceptionWebServiceLayer {
        
    	DeviceLocation deviceLocationProcessed = iSettings.GetDeviceLocation(StatusType.Processed.GetCode());
        
        if(deviceLocationProcessed == null) {
            
            DeviceLocation deviceLocationPending = iSettings.GetDeviceLocation(StatusType.Pending.GetCode());
            
            if(deviceLocationPending == null) {
                try {
                    deviceLocationPending = this.iDeviceDynamicInformation.GetDeviceLocation();
                    this.iSettings.Save(deviceLocationPending, StatusType.Pending.GetCode());
                } catch(Exception ex) {
                    System.out.println( ex.toString() );
                    deviceLocationPending = null;
                }
            }
            
            if(deviceLocationPending != null) {
                int responseCode = iUpload.Location(deviceId, applicationId, deviceLocationPending);
                if(responseCode == WebServiceResponseType.Ok.GetCode()) {
                    iSettings.Save(deviceLocationPending, StatusType.Processed.GetCode());
                    return true;
                } else if(responseCode == WebServiceResponseType.InactiveAccount.GetCode() 
                		|| responseCode ==  WebServiceResponseType.InactiveApplication.GetCode()) {
                	this.setAuthenticationFailure(true);
                    return false;
                } else if(responseCode == WebServiceResponseType.GeneralError.GetCode()) {
                    return false;
                }
            } else {
            	return false;
            }
        }
        
        return true;
    }
    
    private boolean uploadUpgraded(UUID deviceId) 
        	throws ExceptionDatabaseLayer, ExceptionWebServiceLayer {
        	
        	ApplicationMeta applicationMeta = iSettings.LoadApplication(applicationId);
            
            if(applicationMeta.Upgraded) {
                int responseCode = iUpload.Upgrade(deviceId, applicationId, applicationMeta.Version);

                if(responseCode == WebServiceResponseType.Ok.GetCode()) {
                	applicationMeta.Upgraded = false;
                    iSettings.Update(applicationMeta);
                    return true;
                } else if(responseCode == WebServiceResponseType.InactiveAccount.GetCode() 
                    || responseCode ==  WebServiceResponseType.InactiveApplication.GetCode()) {
                	this.setAuthenticationFailure(true);
                    return false;
                } else if(responseCode == WebServiceResponseType.GeneralError.GetCode()) {
                    return false;
                }
            }
            
            return true;
        }
    
    private synchronized int getNumberOfItemsWaitingToBeUploaded(){
    	return this.numberOfItemsWaitingToBeUploaded;
    }
    
    private synchronized void setNumberOfItemsWaitingToBeUploaded(int numberOfItemsWaitingToBeUploaded) {
    	this.numberOfItemsWaitingToBeUploaded = numberOfItemsWaitingToBeUploaded;
    }
    
    private synchronized boolean getThreadUploadInterrupted() {
    	return this.threadUploadInterrupted;
    }
    
    private synchronized void setThreadUploadInterrupted(boolean threadUploadInterrupted) {
    	this.threadUploadInterrupted = threadUploadInterrupted;
    }
    
    private synchronized boolean getItemsWaitingToBeUploaded(){
    	return this.itemsWaitingToBeUploaded;
    }    
    
    private synchronized void setItemsWaitingToBeUploaded(boolean itemsWaitingToBeUploaded) {
    	this.itemsWaitingToBeUploaded = itemsWaitingToBeUploaded;
    }
    
    private synchronized boolean getAuthenticationFailure() {
    	return this.authenticationFailure;
    }
    
    private synchronized void setAuthenticationFailure(boolean authenticationFailure) {
    	this.authenticationFailure = authenticationFailure;
    }
    
    private synchronized boolean getUploadWhileUsing(){
    	return this.uploadWhileUsing;
    }  
    
    private synchronized void setUploadWhileUsng(boolean uploadWhileUsing){
    	this.uploadWhileUsing = uploadWhileUsing;
    }
    
    private synchronized void setThreadIsUploading(boolean threadIsUploading) {
    	this.threadIsUploading = threadIsUploading;
    }
    
    private synchronized boolean getThreadIsUploading() {
    	return this.threadIsUploading;
    }
    
    private synchronized boolean getUserProcessed() {
    	return this.userProcessed;
    }
    
    private synchronized void setUserProcessed(boolean userProcessed) {
    	this.userProcessed = userProcessed;
    }
    
    private synchronized boolean getDeviceLocationProcessed(){
    	return this.deviceLocationProcessed;
    }
    
    private synchronized void setDeviceLocationProcessed(boolean deviceLocationProcessed) {
    	this.deviceLocationProcessed = deviceLocationProcessed;
    }
    
    private synchronized void setUpgradeProcessed(boolean upgradeProcessed) {
    	this.upgradedProcessed  = upgradeProcessed;
    }
    
    private synchronized boolean getUpgradeProcessed() {
    	return this.upgradedProcessed;
    }
}