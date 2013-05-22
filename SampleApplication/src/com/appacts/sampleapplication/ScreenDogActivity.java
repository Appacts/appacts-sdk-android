package com.appacts.sampleapplication;

import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.appacts.plugin.AnalyticsSingleton;
import com.appacts.plugin.Models.ExceptionDescriptive;

public class ScreenDogActivity extends ActivityBase {
	
	public ScreenDogActivity() {
		super("Screen Dog");
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.screendog);
        
        Button btnGenerate = (Button)findViewById(R.id.btnGenerate);
        
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	AnalyticsSingleton.GetInstance().ContentLoading(ScreenName, "Generating Dog");
            	
            	try {
            		AnalyticsSingleton.GetInstance().LogEvent(ScreenName, "Generate");
	            	
	            	String[] petNames = { "Laimo", "Smokey", "Lucy", "Fred", "Boy", "Cute", "Butch", "Alpha" };
	            	
	            	Random random = new Random();
	            	
	                TextView textview_randomname = (TextView)findViewById(R.id.textview_randomname);
	                textview_randomname.setText(petNames[random.nextInt(petNames.length+1)]);
	                
	            }catch(Exception ex) {
	            	AnalyticsSingleton.GetInstance().LogError(ScreenName, "Generate", null, new ExceptionDescriptive(ex));
	            }
            	
            	AnalyticsSingleton.GetInstance().ContentLoaded(ScreenName, "Generating Dog");
            }
        });
        
        Button btnCat = (Button)findViewById(R.id.btnCat);
        
        btnCat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent(ScreenDogActivity.this, ScreenCatActivity.class);
    			startActivity(i);
            }
        });
        
        Button btnFeedback = (Button)findViewById(R.id.btnFeedback);
        
        btnFeedback.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent(ScreenDogActivity.this, ScreenFeedbackActivity.class);
    			startActivity(i);
            }
        });
    }
}
