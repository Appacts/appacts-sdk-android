package com.appacts.sampleapplicationbasic;

import java.util.Random;

import com.appacts.plugin.AnalyticsSingleton;
import com.appacts.sampleapplicationbasic.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SampleApplicationBasicActivity extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    	/*
    	 * Appacts
    	 * Application was entered, activity has started, 
    	 * call start to start the session, if it was already called already it will be ignored
    	 */
        try {
			AnalyticsSingleton.GetInstance().Start(this, "http://api-dev.appacts.com/api/", 
					"84ddec93-198a-449c-9069-fa842536d25c");
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        
        Button btnGenerate = (Button)findViewById(R.id.btnGenerate);
        
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	/*
            	 * Appacts 
            	 * There is an action that we want to log on this screen, 
            	 * simply call .LogEvent(screenName, eventName)
            	 */
            	AnalyticsSingleton.GetInstance().LogEvent("Main", "Generate");
	        	
	        	String[] petNames = { "Lilly", "Bella", "Hun", "Queen", "Sleepy", "Cute", "PoP", "Beta" };
	        	
	        	Random random = new Random();
	        	
	            TextView txtvwResult = (TextView)findViewById(R.id.txtvwResult);
	            txtvwResult.setText(petNames[random.nextInt(petNames.length)]); 

            }
        });
        
    }
    
	@Override
	public void onResume() {	
		
    	/*
    	 * Appacts
    	 * Activity has resumed, call start, if it was already called it will be ignored,
    	 * if application is coming back from background this will start new session
    	 */
        try {
			AnalyticsSingleton.GetInstance().Start(this, "http://api-dev.appacts.com/api/",
					"9baa4776-ed8f-42ec-b7dc-94f1e2a8ac87");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        
		/*
		 * Appacts
		 * We use SetAppIsInForeground & GetAppIsInForeground to identify when activity is being put in foreground,
		 * or whether entire application is being put in foreground, please see android life-cycle documentation for more
		 * information.
		 */
		AnalyticsSingleton.SetAppIsInForeground(true);
		super.onStart();
	}
    
    @Override
    public void onPause() {    	
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