package com.appacts.sampleapplication;

import java.util.Timer;
import java.util.TimerTask;

import com.appacts.plugin.AnalyticsSingleton;
import com.appacts.plugin.Models.OptStatusType;

import android.content.Intent;
import android.os.Bundle;

public class ScreenSplashActivity extends ActivityBase {

	public ScreenSplashActivity() {
		super("Screen Splash");
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screensplash);
        
        this.initTimer();
    }
        
    private void initTimer() {
    	Timer t = new Timer();
    	TimerTask timerTask = new TimerTask() {           
    		@Override
    		public void run()
    		{
    			Intent i = null;
    			
    			if(AnalyticsSingleton.GetInstance().GetOptStatus() == OptStatusType.None.GetCode())
    			{
    				i = new Intent(ScreenSplashActivity.this, ScreenTermsAndConditionsActivity.class);
    			}
    			else if(!AnalyticsSingleton.GetInstance().IsUserInformationSet())
    			{
    				i = new Intent(ScreenSplashActivity.this, ScreenDemographicActivity.class);
    			}
    			else
    			{
    				i = new Intent(ScreenSplashActivity.this, ScreenDogActivity.class);
    			}
    			
    			startActivity(i);
    			finish();
    		}
    	};
    	
    	t.schedule(timerTask, 3000);
    }
}
