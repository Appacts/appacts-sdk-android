package com.appacts.sampleapplication;

import android.app.Activity;
import android.os.Bundle;

import com.appacts.plugin.AnalyticsSingleton;

public abstract class ActivityBase extends Activity {	
	public final String ScreenName;
	
	public ActivityBase(String screenName) {
		this.ScreenName = screenName;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
    	/*
    	 * Appacts
    	 * Application was entered, activity has started, 
    	 * call start to start the session, if it was already called already it will be ignored
    	 */
		try {
			AnalyticsSingleton.GetInstance().Start(this, "http://api-dev.appacts.com/", 
					"84ddec93-198a-449c-9069-fa842536d25c");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {	
		
    	/*
    	 * Appacts
    	 * Activity has resumed, call start, if it was already called it will be ignored,
    	 * if application is coming back from background this will start new session
    	 */
		try {
			AnalyticsSingleton.GetInstance().Start(this, "http://api-dev.appacts.com/", 
					"84ddec93-198a-449c-9069-fa842536d25c");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		AnalyticsSingleton.GetInstance().ScreenOpen(this.ScreenName);
		
		AnalyticsSingleton.SetAppIsInForeground(true);
		super.onStart();
	}
    
    @Override
    public void onPause() {    
    	
    	AnalyticsSingleton.GetInstance().ScreenClosed(this.ScreenName);
    	
		/*
		 * Appacts
		 * We use SetAppIsInForeground & GetAppIsInForeground to identify when activity is being put in foreground,
		 * or whether entire application is being put in foreground, please see android life-cycle documentation for more
		 * information.
		 */
    	AnalyticsSingleton.SetAppIsInForeground(false);
    	
    	super.onPause();
    }
    
    @Override
    public void onStop() {    	
    	if(!AnalyticsSingleton.GetAppIsInForeground()) { 
    		
    		/*
    		 * Appacts
    		 * User is exiting your application now, call stop to stop the session tracking
    		 * and to clean up any unmanaged resources i.e. dispose
    		 */	
    		AnalyticsSingleton.GetInstance().Stop();
    	}
    	
    	super.onStop();
    }
}
