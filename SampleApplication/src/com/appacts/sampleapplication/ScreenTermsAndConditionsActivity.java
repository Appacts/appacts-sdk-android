package com.appacts.sampleapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.appacts.plugin.AnalyticsSingleton;
import com.appacts.plugin.Models.OptStatusType;

public class ScreenTermsAndConditionsActivity extends ActivityBase {
	
	public ScreenTermsAndConditionsActivity() {
		super("Screen Terms And Conditions");
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screentermsandconditions);
        
        Button btnDontAccept = (Button)findViewById(R.id.btnDontAccept);
        
        btnDontAccept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	AnalyticsSingleton.GetInstance().LogEvent(ScreenName, "Don't Accept", null);
            	
            	AnalyticsSingleton.GetInstance().SetOptStatus(OptStatusType.OptOut);
            	
            	Intent i;
            	
            	if(!AnalyticsSingleton.GetInstance().IsUserInformationSet())
    			{
    				i = new Intent(ScreenTermsAndConditionsActivity.this, ScreenDemographicActivity.class);
    			}
    			else
    			{
    				i = new Intent(ScreenTermsAndConditionsActivity.this, ScreenDogActivity.class);
    			}
            	
    			startActivity(i);
    			finish();
            }
        });
        
        Button btnNext = (Button)findViewById(R.id.btnAccept);
        
        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	AnalyticsSingleton.GetInstance().LogEvent(ScreenName, "Accept", null);
            	
            	AnalyticsSingleton.GetInstance().SetOptStatus(OptStatusType.OptIn);
            	
            	Intent i;
            	
            	if(!AnalyticsSingleton.GetInstance().IsUserInformationSet())
    			{
    				i = new Intent(ScreenTermsAndConditionsActivity.this, ScreenDemographicActivity.class);
    			}
    			else
    			{
    				i = new Intent(ScreenTermsAndConditionsActivity.this, ScreenDogActivity.class);
    			}
            	
    			startActivity(i);
    			finish();
            }
        });
    }
}
