package com.appacts.plugin.Data;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.appacts.plugin.Data.Interfaces.IUpload;
import com.appacts.plugin.Handlers.UrlUtf8Encoder;
import com.appacts.plugin.Handlers.Utils;
import com.appacts.plugin.Handlers.WebServices;
import com.appacts.plugin.Models.Crash;
import com.appacts.plugin.Models.DeviceLocation;
import com.appacts.plugin.Models.ErrorItem;
import com.appacts.plugin.Models.EventItem;
import com.appacts.plugin.Models.ExceptionWebServiceLayer;
import com.appacts.plugin.Models.FeedbackItem;
import com.appacts.plugin.Models.KeyValuePair;
import com.appacts.plugin.Models.QueryStringKeyType;
import com.appacts.plugin.Models.Resolution;
import com.appacts.plugin.Models.SystemError;
import com.appacts.plugin.Models.User;
import com.appacts.plugin.Models.WebServiceResponseType;

public class UploadWS implements IUpload {
	private final String baseUrl;
	private final Context context;
	
	public UploadWS(Context context, String baseUrl) {
		this.baseUrl = baseUrl;
		this.context = context;
	}
	
	public int Crash(UUID deviceId, Crash crash) throws ExceptionWebServiceLayer {
		KeyValuePair[] keyValuePairs = new KeyValuePair[] {
	            new KeyValuePair(QueryStringKeyType.DEVICE_GUID, deviceId.toString()),
	            new KeyValuePair(QueryStringKeyType.APPLICATION_GUID, crash.ApplicationId.toString()),
	            new KeyValuePair(QueryStringKeyType.DATE_CREATED, Utils.DateTimeFormat(crash.DateCreated)),
	            new KeyValuePair(QueryStringKeyType.SESSION_ID, crash.SessionId.toString()),
	            new KeyValuePair(QueryStringKeyType.VERSION, Utils.GetValueNotNull(crash.Version))
	        };
	        
	    return this.webServiceCall(this.baseUrl, WebServices.Crash, keyValuePairs, context);
	}
	
	public UUID Device(UUID applicationId, String model, String osVersion, int deviceType,
        String carrier, String applicationVersion, int timeZoneOffset, String locale,
        Resolution resolution, String manufacturer) throws ExceptionWebServiceLayer {
        UUID deviceGuid = null;
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        
        ConnectivityManager connectionManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
        
        try {
        	if(networkInfo != null && networkInfo.isConnected()) {
                String urlString = this.baseUrl + WebServices.Device;
                urlString = this.addToUrl(urlString, QueryStringKeyType.APPLICATION_GUID, applicationId.toString());
                urlString = this.addToUrl(urlString, QueryStringKeyType.MODEL, Utils.GetValueNotNull(model));
                urlString = this.addToUrl(urlString, QueryStringKeyType.PLATFORM_TYPE, Integer.toString(deviceType));
                urlString = this.addToUrl(urlString, QueryStringKeyType.CARRIER, Utils.GetValueNotNull(carrier));
                urlString = this.addToUrl(urlString, QueryStringKeyType.OPERATING_SYSTEM, Utils.GetValueNotNull(osVersion));
                urlString = this.addToUrl(urlString, QueryStringKeyType.VERSION, applicationVersion);
                urlString = this.addToUrl(urlString, QueryStringKeyType.TIME_ZONE_OFFSET, Integer.toString(timeZoneOffset));
                urlString = this.addToUrl(urlString, QueryStringKeyType.LOCALE, Utils.GetValueNotNull(locale));
                urlString = this.addToUrl(urlString, QueryStringKeyType.RESOLUTION_WIDTH, Integer.toString(resolution.Width));
                urlString = this.addToUrl(urlString, QueryStringKeyType.RESOLUTION_HEIGHT, Integer.toString(resolution.Height));
                urlString = this.addToUrl(urlString, QueryStringKeyType.MANUFACTURER, Utils.GetValueNotNull(manufacturer));
                
                System.out.println( urlString );
                
                URL url = new URL(urlString);
                
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                
                if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                    
                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                    Document documentXmlOutput = documentBuilder.parse(inputStream);
                    
                    Element elementRoot = documentXmlOutput.getDocumentElement();
                    elementRoot.normalize();
                    NodeList nodeList = elementRoot.getChildNodes();
                    
                    Node nodeDevice = nodeList.item(1);
                    deviceGuid = UUID.fromString(nodeDevice.getFirstChild().getNodeValue());
                    
                    System.out.println("device uuid: " + deviceGuid.toString());
                }
            }
        }
        catch(Exception ex) {
            throw new ExceptionWebServiceLayer(ex);
        } finally {
            if(inputStream != null) {
            	try {
            		inputStream.close();
            	}
            	catch(Exception exceptionInputStream) {
            		
            	}
            }
            if(httpURLConnection != null) {
                try {
            		httpURLConnection.disconnect();
                }
                catch (Exception exceptionHttpURLConnection) {
                	
                }
                
            }
        }
        
        return deviceGuid;
    }
	
	public int Error(UUID deviceId, ErrorItem errorItem) throws ExceptionWebServiceLayer {
		KeyValuePair[] keyValuePairs = new KeyValuePair[] {
            new KeyValuePair(QueryStringKeyType.DEVICE_GUID, deviceId.toString()),
            new KeyValuePair(QueryStringKeyType.APPLICATION_GUID, errorItem.ApplicationId.toString()),
            new KeyValuePair(QueryStringKeyType.DATE_CREATED, Utils.DateTimeFormat(errorItem.DateCreated)),
            new KeyValuePair(QueryStringKeyType.DATA, Utils.GetValueNotNull(errorItem.Data)),
            new KeyValuePair(QueryStringKeyType.EVENT_NAME, Utils.GetValueNotNull(errorItem.EventName)),
            new KeyValuePair(QueryStringKeyType.AVAILABLE_FLASH_DRIVE_SIZE, Long.toString(errorItem.DeviceInformation.AvailableFlashDriveSize)),
            new KeyValuePair(QueryStringKeyType.AVAILABLE_MEMORY_SIZE, Long.toString(errorItem.DeviceInformation.AvailableMemorySize)),
            new KeyValuePair(QueryStringKeyType.BATTERY, Integer.toString(errorItem.DeviceInformation.Battery)),
            new KeyValuePair(QueryStringKeyType.ERROR_MESSAGE, errorItem.Error.getMessage()),
            new KeyValuePair(QueryStringKeyType.SCREEN_NAME, Utils.GetValueNotNull(errorItem.ScreenName)),
            new KeyValuePair(QueryStringKeyType.SESSION_ID, errorItem.SessionId.toString()),
            new KeyValuePair(QueryStringKeyType.VERSION, Utils.GetValueNotNull(errorItem.Version))
        };
        
        return this.webServiceCall(this.baseUrl, WebServices.Error, keyValuePairs, context);  
    }
	
	public int Event(UUID deviceId, EventItem eventItem) throws ExceptionWebServiceLayer {
		KeyValuePair[] keyValuePairs = new KeyValuePair[] {
            new KeyValuePair(QueryStringKeyType.DEVICE_GUID, deviceId.toString()),
            new KeyValuePair(QueryStringKeyType.APPLICATION_GUID, eventItem.ApplicationId.toString()),
            new KeyValuePair(QueryStringKeyType.DATE_CREATED, Utils.DateTimeFormat(eventItem.DateCreated)),
            new KeyValuePair(QueryStringKeyType.DATA, Utils.GetValueNotNull(eventItem.Data)),
            new KeyValuePair(QueryStringKeyType.EVENT_TYPE, Integer.toString(eventItem.EventType)),
            new KeyValuePair(QueryStringKeyType.EVENT_NAME, Utils.GetValueNotNull(eventItem.EventName)),
            new KeyValuePair(QueryStringKeyType.LENGTH, Long.toString(eventItem.Length)),
            new KeyValuePair(QueryStringKeyType.SCREEN_NAME, Utils.GetValueNotNull(eventItem.ScreenName)),
            new KeyValuePair(QueryStringKeyType.SESSION_ID, eventItem.SessionId.toString()),
            new KeyValuePair(QueryStringKeyType.VERSION, Utils.GetValueNotNull(eventItem.Version))
        };
        
        return this.webServiceCall(this.baseUrl, WebServices.Event, keyValuePairs, context); 
    }
	
	public int Feedback(UUID deviceId, FeedbackItem feedbackItem) throws ExceptionWebServiceLayer {
		KeyValuePair[] keyValuePairs = new KeyValuePair[] {
            new KeyValuePair(QueryStringKeyType.DEVICE_GUID, deviceId.toString()),
            new KeyValuePair(QueryStringKeyType.APPLICATION_GUID, feedbackItem.ApplicationId.toString()),
            new KeyValuePair(QueryStringKeyType.DATE_CREATED, Utils.DateTimeFormat(feedbackItem.DateCreated)),
            new KeyValuePair(QueryStringKeyType.VERSION, Utils.GetValueNotNull(feedbackItem.Version)),
            new KeyValuePair(QueryStringKeyType.SCREEN_NAME, Utils.GetValueNotNull(feedbackItem.ScreenName)),
            new KeyValuePair(QueryStringKeyType.FEEDBACK, Utils.GetValueNotNull(feedbackItem.Message)),
            new KeyValuePair(QueryStringKeyType.FEEDBACK_RATING_TYPE, Integer.toString(feedbackItem.Rating)),
            new KeyValuePair(QueryStringKeyType.SESSION_ID, feedbackItem.SessionId.toString())
        };
        
        return this.webServiceCall(this.baseUrl, WebServices.Feedback, keyValuePairs, context);
    }
	
	public int SystemError(UUID deviceId, SystemError systemError) throws ExceptionWebServiceLayer {
		KeyValuePair[] keyValuePairs = new KeyValuePair[] {
            new KeyValuePair(QueryStringKeyType.DEVICE_GUID, deviceId.toString()),
            new KeyValuePair(QueryStringKeyType.APPLICATION_GUID, systemError.ApplicationId.toString()),
            new KeyValuePair(QueryStringKeyType.DATE_CREATED, Utils.DateTimeFormat(systemError.DateCreated)),
            new KeyValuePair(QueryStringKeyType.VERSION, Utils.GetValueNotNull(systemError.Version)),
            new KeyValuePair(QueryStringKeyType.ERROR_MESSAGE, systemError.Error.getMessage()),
            new KeyValuePair(QueryStringKeyType.PLATFORM_TYPE, Integer.toString(systemError.System.DeviceType)),
            new KeyValuePair(QueryStringKeyType.SYSTEM_VERSION, Utils.GetValueNotNull(systemError.System.Version))
        };
        
        return this.webServiceCall(this.baseUrl, WebServices.SystemError, keyValuePairs, context); 
    }
	
	public int User(UUID deviceId, User user) throws ExceptionWebServiceLayer {
		KeyValuePair[] keyValuePairs = new KeyValuePair[] {
            new KeyValuePair(QueryStringKeyType.DEVICE_GUID, deviceId.toString()),
            new KeyValuePair(QueryStringKeyType.APPLICATION_GUID, user.ApplicationId.toString()),
            new KeyValuePair(QueryStringKeyType.DATE_CREATED, Utils.DateTimeFormat(user.DateCreated)),
            new KeyValuePair(QueryStringKeyType.VERSION, Utils.GetValueNotNull(user.Version)),
            new KeyValuePair(QueryStringKeyType.AGE, Integer.toString(user.Age)),
            new KeyValuePair(QueryStringKeyType.SEX_TYPE, Integer.toString(user.Sex)),
            new KeyValuePair(QueryStringKeyType.SESSION_ID, user.SessionId.toString()),
        };
        
        return this.webServiceCall(this.baseUrl, WebServices.User, keyValuePairs, context);
    }
	
	public int Location(UUID deviceId, UUID applicationId, DeviceLocation deviceLocation) 
	        throws ExceptionWebServiceLayer {
	        
		KeyValuePair[] keyValuePairs = new KeyValuePair[] {
            new KeyValuePair(QueryStringKeyType.LOCATION_LONGITUDE, Double.toString(deviceLocation.Longitude)),
            new KeyValuePair(QueryStringKeyType.LOCATION_LATITUDE, Double.toString(deviceLocation.Latitude)),
            new KeyValuePair(QueryStringKeyType.LOCATION_COUNTRY, Utils.GetValueNotNull(deviceLocation.CountryName)),
            new KeyValuePair(QueryStringKeyType.LOCATION_COUNTRY_CODE, Utils.GetValueNotNull(deviceLocation.CountryCode)),
            new KeyValuePair(QueryStringKeyType.LOCATION_ADMIN, Utils.GetValueNotNull(deviceLocation.CountryAdminName)),
            new KeyValuePair(QueryStringKeyType.LOCATION_ADMIN_CODE, Utils.GetValueNotNull(deviceLocation.CountryAdminCode)),
            new KeyValuePair(QueryStringKeyType.APPLICATION_GUID, applicationId.toString()),
            new KeyValuePair(QueryStringKeyType.DEVICE_GUID, deviceId.toString())
        };
        
        return this.webServiceCall(this.baseUrl, WebServices.Location, keyValuePairs, context);
    }
	
	public int Upgrade(UUID deviceId, UUID applicationId, String version)
			throws ExceptionWebServiceLayer {
			
	        KeyValuePair[] keyValuePairs = new KeyValuePair[] {
	                new KeyValuePair(QueryStringKeyType.APPLICATION_GUID, applicationId.toString()),
	                new KeyValuePair(QueryStringKeyType.DEVICE_GUID, deviceId.toString()),
	                new KeyValuePair(QueryStringKeyType.VERSION, version)
	            };
	            
	        return this.webServiceCall(this.baseUrl, WebServices.Upgrade, keyValuePairs, context);
		}
	
	private int webServiceCall(String baseUrl, String serviceUrl, KeyValuePair[] keyValuePair, Context context)
			throws ExceptionWebServiceLayer {
	    
	    int responseCode = WebServiceResponseType.GeneralError.GetCode();
	    HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        
        ConnectivityManager connectionManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
        
        try {
        	if(networkInfo != null && networkInfo.isConnected()) {
	            String urlString = baseUrl + serviceUrl;
	            
	            for(int i = 0; i < keyValuePair.length; i++) {
	                urlString = this.addToUrl(urlString, keyValuePair[i].Key, Utils.GetValueNotNull(keyValuePair[i].Value));
	            }
	            
	            System.out.println( urlString );
	            
            	URL url = new URL(urlString);
                
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                
                if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                	inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                    
                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                    Document documentXmlOutput = documentBuilder.parse(inputStream);
	                
	                Element elementRoot = documentXmlOutput.getDocumentElement();
	                elementRoot.normalize();
	                NodeList nodeList = elementRoot.getChildNodes();
	                Node node = nodeList.item(0);
	                
	                responseCode = Integer.parseInt(node.getFirstChild().getNodeValue());
	                
	                System.out.println("repsonse code: ".concat(Integer.toString(responseCode)));
	            }
	        }
	    }
	    catch(Exception ex) {
	        throw new ExceptionWebServiceLayer(ex);
	    } finally {
	        if(inputStream != null) {
	            try {
	                inputStream.close();
	            } catch(Exception exceptionInputStream) { }
	        }
	        if(httpURLConnection != null) {
	            try {
	                httpURLConnection.disconnect();
	            } catch(Exception exceptionHttpConnection) {  }
	        }
	    }
	    return responseCode;  
	    
	}
	
	private String addToUrl(String url, String key, String value) {
        if(url.indexOf("?") > 0) {
            return url + "&" + key + "=" +  UrlUtf8Encoder.encode(value);
        }
        return  url + "?" + key + "=" +  UrlUtf8Encoder.encode(value);
    }
}
