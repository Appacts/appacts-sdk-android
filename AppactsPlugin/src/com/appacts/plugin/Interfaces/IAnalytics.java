package com.appacts.plugin.Interfaces;

import android.content.Context;

import com.appacts.plugin.Models.ExceptionDatabaseLayer;
import com.appacts.plugin.Models.ExceptionDescriptive;
import com.appacts.plugin.Models.OptStatusType;
import com.appacts.plugin.Models.RatingType;
import com.appacts.plugin.Models.SexType;
import com.appacts.plugin.Models.UploadType;

public interface IAnalytics {
	
	/*
	 * This starts a new session
	 */
	void Start(Context context, String baseUrl, String applicationId) throws Exception;
	
	/*
	 * This starts a new session
	 */
	void Start(Context context, String baseUrl, String applicationId, UploadType uploadType) throws Exception;
	
	/*
	 * Log Error from your application
	 * @param screenName is a screen name that you want to log error against
	 * @param eventName is a event name that you want to log error against
	 * @param data is data that you want to log error against
	 * @param ExceptionDescriptive is an Exception that you want to log error against
	 * @see Integration Guidelines SDK Document for examples
	 */
    void LogError(String screenName, String eventName, String data, ExceptionDescriptive ex);
    
	/*
	 * Log Event from your application
	 * @param screenName is a screen name that you want to log event against
	 * @param eventName is a event name that you want to log event against
	 * @param data is data that you want to log event against
	 * @see Integration Guidelines SDK Document for examples
	 */
    void LogEvent(String screenName, String eventName, String data);
    
	/*
	 * Log Event from your application
	 * @param screenName is a screen name that you want to log event against
	 * @param eventName is a event name that you want to log event against
	 * @see Integration Guidelines SDK Document for examples
	 */
    void LogEvent(String screenName, String eventName);
    
	/*
	 * Log Feedback from your application
	 * @param screenName is a screen name that you want to log feedback against
	 * @param ratingType is a rating 1 - 5 see RatingType enum
	 * @param comment is a comment that user made about your screen
	 * @throws ExceptionDatabaseLayer this will notify you that users feedback was not stored, 
	 * so your application can handle this situation
	 * @see Integration Guidelines SDK Document for examples
	 */
    void LogFeedback(String screenName, RatingType ratingType, String comment) throws ExceptionDatabaseLayer;
    
	/*
	 * Screen Open, specify screen that just opened, this helps us to start timer on to measure how long
	 * user stays on this screen, soon as users is finished with this screen call ScreenClosed
	 * @param screenName
	 * @see Integration Guidelines SDK Document for examples
	*/
    void ScreenOpen(String screenName);
    
	/*
	 * Screen Closed, specify the screen that has just closed, this helps us calculate time spent on the screen
	 * @param screenName
	 * @see Integration Guidelines SDK Document for examples
	*/
    void ScreenClosed(String screenName);
    
	/*
	 * Screen Loading, specify the screen that has started to load, this helps us to start timer to measure how long
	 * screen takes to load, soon as screen has loaded call ScreenLoaded
	 * @param screenName
	 * @see Integration Guidelines SDK Document for examples
	*/
    void ContentLoading(String screenName, String contentName);   
  
	/*
	 * Screen Loaded, specify the screen that has just loaded, this helps us calculate time it took to load
	 * @param screenName
	 * @see Integration Guidelines SDK Document for examples
	*/
    void ContentLoaded(String screenName, String contentName);
    
	/*
	 * Set User Information, when you want to log away demographic information use this to log age and sex
	 * @param age that user has entered in your application
	 * @param sexType that user has entered in your application see SexType enum
	 * @throws ExceptionDatabaseLayer  this will notify you that users information was not stored, 
	 * so your application can handle this situation
	 * @see Integration Guidelines SDK Document for examples
	*/
    void SetUserInformation(int age, SexType sexType) throws ExceptionDatabaseLayer;
    
	/*
	 * Is User Information Set, use this to check whether information was already saved
	 * return boolean weather or not we have already asked user for demographic information
	 * @see Integration Guidelines SDK Document for examples
	*/
    boolean IsUserInformationSet();
    
	/*
	 * Set Opt Status, by OptStatusType.OptOut we will not log any data, by OptStatusType.OptIn we will log all data
	 * @param optStatusType, by default user is set to OptStatusType.OptIn
	 * @see Integration Guidelines SDK Document for examples
	*/
    void SetOptStatus(OptStatusType optStatusType);
    
    
	/*
	 * Get Opt Status, when nothing was set OptStatusType.None will be required i.e. 0
	 * when you receive OptStatusType.None you will know that this is a new user you will be able to
	 * give user an option to OptStatusType.OptOut
	 * @return OptStatusType what is OptStatusType currently set to
	 * @see Integration Guidelines SDK Document for examples
	*/
    int GetOptStatus();
    
	/*
	 * Upload While Using Async
	 * For not heavy applications, i.e. forms, utilities, we recommend you use this methodology
	 * this enables automatic upload we check network coverage, usage, and when is the best time
	 * to upload data to our severs 
	 * @see Integration Guidelines SDK Document for examples
	*/
    void UploadWhileUsingAsync();
    
	/*
	 * Upload Manual
	 * For heavy applications i.e. games we recommend you use this upload methodology
	 * using this you can easily log many events and then when you are ready call this method to log 
	 * everything in one go, however there might not be network coverage so data might not be uploaded for  
	 * a while
	 * @see Integration Guidelines SDK Document for examples
	*/
    void UploadManual();
    
    /*
     * Stops the current session
     */
    void Stop();
} 
