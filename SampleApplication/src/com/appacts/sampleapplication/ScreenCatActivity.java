package com.appacts.sampleapplication;

import java.util.Random;

import com.appacts.plugin.AnalyticsSingleton;
import com.appacts.plugin.Models.ExceptionDescriptive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScreenCatActivity extends ActivityBase {

	public ScreenCatActivity() {
		super("Screen Cat");
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.screencat);
        
        Button btnGenerate = (Button)findViewById(R.id.btnGenerate);
        
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	AnalyticsSingleton.GetInstance().ContentLoading(ScreenName, "Generating Cat");
            	
            	try {
            		AnalyticsSingleton.GetInstance().LogEvent(ScreenName, "Generate", null);
	            	
	            	String[] petNames = { "Lilly", "Bella", "Hun", "Queen", "Sleepy", "Cute", "PoP", "Beta" };
	            	
	            	Random random = new Random();
	            	
	                TextView textview_randomname = (TextView)findViewById(R.id.textview_randomname);
	                textview_randomname.setText(petNames[random.nextInt(petNames.length+1)]);
	                
	            }catch(Exception ex) {
	            	AnalyticsSingleton.GetInstance().LogError(ScreenName, "Generate", null, new ExceptionDescriptive(ex));
	            }
            	
            	AnalyticsSingleton.GetInstance().ContentLoading(ScreenName ,"Generating Cat");
            }
        });
        
        Button btnDog = (Button)findViewById(R.id.btnDog);
        
        btnDog.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent(ScreenCatActivity.this, ScreenDogActivity.class);
    			startActivity(i);
            }
        });
        
        Button btnFeedback = (Button)findViewById(R.id.btnFeedback);
        
        btnFeedback.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent(ScreenCatActivity.this, ScreenFeedbackActivity.class);
    			startActivity(i);
            }
        });
    }
}
