package com.appacts.sampleapplication;

import com.appacts.plugin.AnalyticsSingleton;
import com.appacts.plugin.Models.ExceptionDescriptive;
import com.appacts.plugin.Models.RatingType;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

public class ScreenFeedbackActivity extends ActivityBase {
	
	public ScreenFeedbackActivity() {
		super("Screen Feedback");
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screenfeedback);
        
        Button btnSubmit = (Button)findViewById(R.id.btnSubmit);
        
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	RatingBar rbRating = (RatingBar)findViewById(R.id.rbRating);
            	EditText etComments = (EditText)findViewById(R.id.etComments);
            	
            	try {
            		AnalyticsSingleton.GetInstance().LogFeedback(ScreenName, RatingType.values()[((int)rbRating.getRating())-1], etComments.getText().toString());
            	}
            	catch (Exception ex){
            		AnalyticsSingleton.GetInstance().LogError(ScreenName, "Submit", null, new ExceptionDescriptive(ex));
            	}            	
            	
            	Intent i = new Intent(ScreenFeedbackActivity.this, ScreenDogActivity.class);
    			startActivity(i);
            }
        });
        
        Button btnCancel = (Button)findViewById(R.id.btnCancel);
        
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	AnalyticsSingleton.GetInstance().LogEvent(ScreenName, "Cancel", null);
            	
            	Intent i = new Intent(ScreenFeedbackActivity.this, ScreenDogActivity.class);
    			startActivity(i);
            }
        });
    }
}
