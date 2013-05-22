package com.appacts.sampleapplication;

import com.appacts.plugin.AnalyticsSingleton;
import com.appacts.plugin.Models.ExceptionDescriptive;
import com.appacts.plugin.Models.SexType;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class ScreenDemographicActivity extends ActivityBase {

	public ScreenDemographicActivity() {
		super("Screen Demographic");
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.screendemographic);

        ArrayAdapter<CharSequence> adapter;
        
        Spinner snrAge = (Spinner)findViewById(R.id.snrAge);
        adapter = ArrayAdapter.createFromResource(
                this, R.array.snrAgeValues, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        snrAge.setAdapter(adapter);
        
        Spinner snrSex = (Spinner)findViewById(R.id.snrSex);
        adapter = ArrayAdapter.createFromResource(
                this, R.array.snrSexValues, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        snrSex.setAdapter(adapter);
        
        Button btnSkip = (Button)findViewById(R.id.btnskip);
        
        btnSkip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	AnalyticsSingleton.GetInstance().LogEvent(ScreenName, "Skip", null);
            	
            	Intent i = new Intent(ScreenDemographicActivity.this, ScreenDogActivity.class);
    			startActivity(i);
    			finish();
            }
        });
        
        Button btnNext = (Button)findViewById(R.id.btnNext);
        
        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	TextView tvPleaseSelectSex = (TextView)findViewById(R.id.tvPleaseSelectSex);
            	Spinner snrAge = (Spinner)findViewById(R.id.snrAge);
            	Spinner snrSex = (Spinner)findViewById(R.id.snrSex);
            	
            	try {
            		if(!snrAge.getSelectedItem().toString().equals("0")) {
            			tvPleaseSelectSex.setVisibility(View.INVISIBLE);
            			AnalyticsSingleton.GetInstance().SetUserInformation(Integer.parseInt(snrAge.getSelectedItem().toString()), SexType.valueOf((String)snrSex.getSelectedItem()));
            			
            			Intent i = new Intent(ScreenDemographicActivity.this, ScreenDogActivity.class);
            			startActivity(i);
            			finish();
            		}
            		else {
            			AnalyticsSingleton.GetInstance().LogEvent(ScreenName, "Submit Missed Age");
            			tvPleaseSelectSex.setVisibility(View.VISIBLE);
            		}
            	}
            	catch (Exception ex){
            		AnalyticsSingleton.GetInstance().LogError(ScreenName, "Submit", null, new ExceptionDescriptive(ex));
            	}
            }
        });
    }
}
