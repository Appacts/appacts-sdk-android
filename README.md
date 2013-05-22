##Example Integrations##

Goto “AndroidManifest.xml” and add these permissions.
  <uses-permissionandroid:name="android.permission.READ_PHONE_STATE"/>
  <uses-permissionandroid:name="android.permission.ACCESS_NETWORK_STATE"/>
  <uses-permissionandroid:name="android.permission.INTERNET"/>
  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
  

###Basic Integration###

####Methods####

For basic integration, you can use the following methods:

  void Start(Context context, String applicationId, String serverUrl);
  void LogEvent(String screenName, String eventName);
  void Stop();


####Sample Usage####

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
          AnalyticsSingleton.GetInstance().Start(this, "9baa4776-ed8f-42ec-b7dc-94f1e2a8ac87", "http://yourserver.com/api/");
          
          
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
          AnalyticsSingleton.GetInstance().Start(this, "9baa4776-ed8f-42ec-b7dc-94f1e2a8ac87", "http://yourserver.com/api/");
          
          
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
  



###Advanced Integration###

####Methods####

For advanced integration, you can use the following methods:

  void LogError(String screenName, String eventName, String data, ExceptionDescriptive ex);
  void LogEvent(String screenName, String eventName, String data);
  void LogFeedback(String screenName, int ratingType, String comment) throws ExceptionDatabaseLayer;
  void ScreenOpen(String screenName);
  void ScreenClosed(String screenName);
  void ContentLoading(String screenName, String contentName);   
  void ContentLoaded(String screenName, String contentName);
  void SetUserInformation(int age, int sexType) throws ExceptionDatabaseLayer;
  boolean IsUserInformationSet();
  void SetOptStatus(int optStatusType);
  int GetOptStatus();
  void UploadWhileUsingAsync();
  void UploadManual();
  
####Getting an instance & Starting Session####

When your application is opened you need to obtain a new instance of IAnalytics. You can do this easily by using AnalyticsSingleton. For example:

#####Import#####

  import java.util.UUID;
  import com.appacts.plugin.AnalyticsSingleton;
  import com.appacts.plugin.Interfaces.IAnalytics;
  import com.appacts.plugin.Models.UploadType;


#####Methods#####

  IAnalytics AnalyticsSingleton.GetInstance()
  void IAnalytics.Start(Context context, String applicationId, String serverUrl)


#####Sample#####

  IAnalytics iAnalytics = AnalyticsSingleton.GetInstance();
  iAnalytics.Start(this, "9baa4776-ed8f-42ec-b7dc-94f1e2a8ac87", "http://yourserver.com/api/");
  

Note: Please make sure that .Start & .Stop is always called from main thread, these two methods need to hook in to network coverage event handler.

#####Optional#####

We suggest that you add an abstract base class into your application so that you have a common area from where everything derives. For example:

  public class ActivityBase extends Activity { 
      public final String ScreenName;
  
      public ActivityBase(String screenName) {
            this.ScreenName = screenName;
      }
  
      @Override
       public void onResume() {       
          AnalyticsSingleton.SetAppIsInForeground(true);
          AnalyticsSingleton.GetInstance().ScreenOpen(this.ScreenName);
          super.onStart();
       }
  
      @Override
      public void onPause() {     
          AnalyticsSingleton.GetInstance().ScreenClosed(this.ScreenName);
          AnalyticsSingleton.SetAppIsInForeground(false);
          super.onPause();
      }
  
      @Override
      public void onStop() {      
           if(!AnalyticsSingleton.GetAppIsInForeground()) { 
                AnalyticsSingleton.GetInstance().Stop();
           }
          super.onStop();
      }
  }


Important: We recommend that you create a Base class for different activities as well (that are required for your application) i.e. if you use TabActivity you would need to create a TabActivityBase class. You would then copy all of our overrides into it to make sure we are logging when your application comes into the foreground or goes into the background.

####Opt In or Opt out?####

Every app is different. Your business needs to make a decision about how it wants to deal with its users. You can be really nice and ask your users whether or not they would like to participate in your customer experience improvement program. If they say yes you can use our services and log their experience. Alternatively you can make them opt in automatically by accepting your terms and conditions. Either way here is how you control opt in/ out in the terms and conditions scenario:

#####Import#####

  import com.appacts.plugin.Models.OptStatusType;


#####Methods#####

  int GetOptStatus();
  void SetOptStatus(int optStatusType);


#####Sample - GetOptStatus#####

  Intent i = null;
  if(AnalyticsSingleton.GetInstance().GetOptStatus() == OptStatusType.None.GetCode()) {
  i = new Intent(ScreenSplashActivity.this, ScreenTermsAndConditionsActivity.class);
  }
  else if(!AnalyticsSingleton.GetInstance().IsUserInformationSet()){
  i = new Intent(ScreenSplashActivity.this, ScreenDemographicActivity.class);
  }
  Else {
  i = new Intent(ScreenSplashActivity.this, ScreenDogActivity.class);
  }   
  startActivity(i);
  finish();


#####Sample - SetOptStatus#####

       btnDontAccept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AnalyticsSingleton.GetInstance().LogEvent("ScreenTermsAndConditions", "Don't Accept", null);
                AnalyticsSingleton.GetInstance().SetOptStatus(OptStatusType.OptOut.GetCode());
                
                Intent i;   
                if(!AnalyticsSingleton.GetInstance().IsUserInformationSet()){
                      i = new Intent(ScreenTermsAndConditionsActivity.this, ScreenDemographicActivity.class);
                }
                else{
                i = new Intent(ScreenTermsAndConditionsActivity.this, ScreenDogActivity.class);
                }
                startActivity(i);
                finish();
            }
        });
        Button btnNext = (Button)findViewById(R.id.btnAccept);
        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AnalyticsSingleton.GetInstance().LogEvent("ScreenTermsAndConditions", "Accept", null);
                AnalyticsSingleton.GetInstance().SetOptStatus(OptStatusType.OptIn.GetCode());
                Intent i;
                if(!AnalyticsSingleton.GetInstance().IsUserInformationSet()){
        i = new Intent(ScreenTermsAndConditionsActivity.this, ScreenDemographicActivity.class);
                }
                else{
                    i = new Intent(ScreenTermsAndConditionsActivity.this, ScreenDogActivity.class);
                }
                startActivity(i);
                finish();
            }
        });


####Demographics####

To improve your app you need to know who is using it, how old they are, what their gender is & where they are from. We have made it easy for you to capture this information:

#####Import#####

  import com.appacts.plugin.Models.ExceptionDescriptive;


#####Methods#####

  IsUserInformationSet();
  void SetUserInformation(int age, int sexType) throws ExceptionDatabaseLayer


#####Sample - IsUserInformationSet#####

  if(!AnalyticsSingleton.GetInstance().IsUserInformationSet()){
     i = new Intent(ScreenTermsAndConditionsActivity.this, ScreenDemographicActivity.class);
  }else{
     i = new Intent(ScreenTermsAndConditionsActivity.this, ScreenDogActivity.class);
  }
  

#####Sample - SetUserInformation#####
   try {
        if(!snrAge.getSelectedItem().toString().equals("0")) {
         tvPleaseSelectSex.setVisibility(View.INVISIBLE);
        AnalyticsSingleton.GetInstance().SetUserInformation
                   (Integer.parseInt(snrAge.getSelectedItem().toString()),                       (int)snrSex.getSelectedItemId());
                          
         Intent i = new Intent(ScreenDemographicActivity.this, ScreenDogActivity.class);
         startActivity(i);
         finish();
      } else {
        AnalyticsSingleton.GetInstance().LogEvent("ScreenDemographic", "Sex Not Selected", null);
         tvPleaseSelectSex.setVisibility(View.VISIBLE);
       }
  } catch (Exception ex){
    AnalyticsSingleton.GetInstance().LogError("ScreenDemographic", "Submit", null, new ExceptionDescriptive(ex));
  }
  
Note: Our plugin throws exception if it can't save users information. Normally this happens when user’s storage card is not present, it was corrupt, or device is full. As we throw an error you can notify a user that there was an issue or just handle it using in your app as per your business requirements.



####Logging and uploading your customers experience####

#####Import#####

  import com.appacts.plugin.Models.ExceptionDescriptive;


#####Methods#####

  void LogError(String screenName, String eventName, String data, ExceptionDescriptive ex);
  void LogEvent(String screenName, String eventName, String data);
  void LogFeedback(String screenName, int ratingType, String comment) throws ExceptionDatabaseLayer;
  void ScreenOpen(String screenName);
  void ScreenClosed(String screenName);
  void ContentLoading(String screenName, String contentName);   
  void ContentLoaded(String screenName, String contentName);
  void UploadWhileUsingAsync();
  void UploadManual();


#####Sample - LogError#####

  try {
    iAnalytics.LogEvent("ScreenDog", "Generate", null);
    String[] petNames = { "Laimo", "Smokey", "Lucy", "Fred", "Boy", "Cute", "Butch", "Alpha" };
    Random random = new Random();
    TextView textview_randomname = (TextView)findViewById(R.id.textview_randomname);
      textview_randomname.setText(petNames[random.nextInt(petNames.length)]); 
  }catch(Exception ex) {
     AnalyticsSingleton.GetInstance().LogError("ScreenDog", "Generate", null, new ExceptionDescriptive(ex));
  }


#####Sample - LogEvent#####

  btnGenerate.setOnClickListener(new View.OnClickListener() {
    public void onClick(View v) {
      AnalyticsSingleton.GetInstance().LogEvent("ScreenDog", "Generate");
    }
  }


#####Sample - LogFeedback#####

  try {
    AnalyticsSingleton.GetInstance().LogFeedback("ScreenFeedback", (int)rbRating.getRating(), etComments.getText().toString());
  } catch (Exception ex){ 
    AnalyticsSingleton.GetInstance().LogError("ScreenFeedback", "Submit", null, new ExceptionDescriptive(ex));
  }
  
Note: Our plugin throws exception if it can't save users information. Normally this happens when user’s storage card is not present, it was corrupt, or device is full. As we throw an error you can notify a user that there was an issue or just handle it using in your app as per your business requirements.



#####Sample - ScreenOpen & ScreenClosed#####

    @Override
     public void onResume() {       
        AnalyticsSingleton.SetAppIsInForeground(true);
        AnalyticsSingleton.GetInstance().ScreenOpen(this.ScreenName);
        super.onStart();
     }

    @Override
    public void onPause() {     
        AnalyticsSingleton.GetInstance().ScreenClosed(this.ScreenName);
        AnalyticsSingleton.SetAppIsInForeground(false);
        super.onPause();
    }


Note: Screen names need to be unique for each screen. We collect screen names in our plugin so that when Screen Closed is called we can calculate how long the user was on the screen for. You can use ScreenOpen many times but it will only register each unique screen name once. This is why ScreenBase & onExposed sometimes call ScreenOpen twice and it works fine.

#####Sample - ContentLoading & ContentLoaded#####

  btnGenerate.setOnClickListener(new View.OnClickListener() {
    public void onClick(View v) {
      AnalyticsSingleton.GetInstance().ContentLoading("ScreenDog", "Generating Dog");
          try {
        AnalyticsSingleton.GetInstance().LogEvent("ScreenDog", "Generate", null);
         String[] petNames = { "Laimo", "Smokey", "Lucy", "Fred", "Boy", "Cute", "Butch", "Alpha" };
            Random random = new Random(); 
         TextView textview_randomname = (TextView)findViewById(R.id.textview_randomname);
                      textview_randomname.setText(petNames[random.nextInt(petNames.length)]);
       }catch(Exception ex) {
          AnalyticsSingleton.GetInstance().LogError("ScreenDog", "Generate", null, new ExceptionDescriptive(ex));
        }
       AnalyticsSingleton.GetInstance().ContentLoaded("ScreenDog", "Generating Dog");
      }
    });


#####Sample - UploadWhileUsingAsync & UploadManual#####

We have created two methods for two different scenarios:

*UploadWhileUsingAsync – use this when you are creating a light application, i.e. utilities, forms, etc.. Using this method we will take care of all data uploading. As soon as the user creates an event we will try and upload this event to our servers and present it to you in your reports. The aim of this approach is to prevent waiting and obtain data straight away. Using this approach is recommended by our team as this will monitor network coverage, event queues and it will do its best to get data to our servers immediately.

*UploadManual – use this when you have a very event heavy application i.e. game. Using this method you will need to raise the upload event manually when you are ready. This is a very light approach and popular among some app makers, however data might not be uploaded to our servers for days/ weeks (depending on the app use) therefore statistics will be delayed.

*UploadWhileUsingAsync & UploadManual – you could always use both together. You can specify that you want to upload manually and later call UploadWhileUsingAsync. The example below will demonstrate this.

#####UploadWhileUsingAsync#####

  IAnalytics iAnalytics = AnalyticsSingleton.GetInstance().Start(
      this,
      UUID.fromString("95f33abd-9111-424b-a19b-9982c4e8c36f", "http://yourserver.com/api/"), 
      UploadType.WhileUsingAsync
  );


By specifying Upload Type While Using Async during the initial singleton request, the plugin will automatically start uploading data while a user is using the app.

#####UploadManual#####

  IAnalytics iAnalytics = AnalyticsSingleton.GetInstance().Start(
      this,
      UUID.fromString("95f33abd-9111-424b-a19b-9982c4e8c36f", "http://yourserver.com/api/"), 
      UploadType.Manual
  );

By specifying Upload Type Manual during the initial singleton request, the plugin will not upload any data. It will just collect it and you will need to manually trigger either “UploadManual” or “UploadWhileUsingAsync” i.e.

    private FieldChangeListener btnCats_OnFieldChanged() {
        return new FieldChangeListener() {
            public void fieldChanged(Field field, int context) {
                AnalyticsSingleton.GetInstance().LogEvent(ScreenName, "Show Cat",);
        AnalyticsSingleton.GetInstance().UploadManual();
                UiApplication.getUiApplication().pushScreen(new ScreenCat());
                closeScreen();
            }
        };
    }


You have to manually trigger upload if you want an upload to take place at a certain point. As mentioned before it has many draw backs, although it must be used in heavy data collection scenarios.

    private FieldChangeListener btnCats_OnFieldChanged() {
        return new FieldChangeListener() {
            public void fieldChanged(Field field, int context) {
                AnalyticsSingleton.GetInstance().LogEvent(ScreenName, "Show Cat", null);
        AnalyticsSingleton.GetInstance().UploadWhileUsingAsync();
                UiApplication.getUiApplication().pushScreen(new ScreenCat());
                closeScreen();
            }
        };
    }


You can call UploadWhileUsingAsync manually later if you called your singleton with “UploadType.Manual”. This can be useful in different scenarios but this approach should be rarely used.

