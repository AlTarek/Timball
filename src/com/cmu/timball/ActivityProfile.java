package com.cmu.timball;

import com.cmu.timball.ActivityDetailPlace.JoingameAsyncTask;
import com.cmu.timball.libraries.UserFunctions;
import com.cmu.timball.utils.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityProfile extends ActionBarActivity {

	private ActionBar actionbar;
	TextView lbl_email_id1,lbl_game_type1,lbl_location1,lbl_email_id,lbl_game_type,lbl_location, cl1,cl2,cl3;
	Button btn_logout;
	Global_data gda;
	Context cntxt;
	private WebView web;
	String game_t="", location ="";
	private UserFunctions userFunction;
	ProgressDialog pd;
	private ProgressBar prgPageLoading;
	private Utils utils;
    private MenuItem miPrev, miNext;

	// Declare view objects
	private Button btnRetry; 
	private LinearLayout lytRetry;
    
    private String mGetDealTitle;
	private String url;
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar_browser, menu);
		miPrev = (MenuItem) menu.findItem(R.id.abPrevious);
		miNext = (MenuItem) menu.findItem(R.id.abNext);
		
		return true;      
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_profile);
		
        // Declare object of Utils class
        utils	= new Utils(this);
		
        // Get intent Data from ActivityDetailPlace
        Intent i = getIntent();

    	url 		  = i.getStringExtra(utils.EXTRA_LOCATION_URL);
        mGetDealTitle = i.getStringExtra(utils.EXTRA_LOCATION_NAME);
        
		// Get ActionBar and set back button on actionbar
     	actionbar = getSupportActionBar();
     	actionbar.setDisplayHomeAsUpEnabled(true);  
     	
      	// Connect view object and xml ids
        web 			= (WebView) findViewById(R.id.web);
        prgPageLoading  = (ProgressBar) findViewById(R.id.prgPageLoading);
        lytRetry 	  	= (LinearLayout) findViewById(R.id.lytRetry);
		btnRetry 	  	= (Button) findViewById(R.id.btnRetry);
		
		//btnRetry.setOnClickListener(this);
		
		// Configuration Webview
        web.setHorizontalScrollBarEnabled(true); 
        web.getSettings().setAllowFileAccess(true);
        web.getSettings().setJavaScriptEnabled(true);
        setProgressBarVisibility(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setUseWideViewPort(true);
        web.setInitialScale(1);
        
		// Check the connection
		if(utils.isNetworkAvailable()){
			webViewSocial();
		} else {
			lytRetry.setVisibility(View.VISIBLE);
		} 
    
     	gda = new Global_data();
		cntxt = this;
		userFunction = new UserFunctions();
     	
     	btn_logout = (Button) findViewById(R.id.btn_logout);
     	
     	game_t = gda.loadSavedPreferences_string(gda.TAG_GAME_TYPE, cntxt);
     	location = gda.loadSavedPreferences_string(gda.TAG_LOCATION, cntxt);
     	lbl_email_id1.setText("Email Address");
     	cl1.setText(": ");
     	lbl_email_id.setText(gda.loadSavedPreferences_string(gda.TAG_EMAIL, cntxt));
     	
     	btn_logout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gda.savePreferences(gda.TAG_LOGIN, false, cntxt);
				gda.removePreferences(gda.TAG_EMAIL, cntxt);
				gda.removePreferences(gda.TAG_LOGIN, cntxt);
				gda.removePreferences(gda.TAG_GAME_TYPE, cntxt);
				gda.removePreferences(gda.TAG_LOCATION, cntxt);
				
				Intent i = new Intent(ActivityProfile.this, ActivityLogin.class);
				startActivity(i);
				ActivityProfile.this.finish();
			}
		});
     	
     	if(location.trim().length()>0){
     		location = location.replaceAll("," , "\n");
     	}     	
	}
	
	private void webViewSocial(){
		web.loadUrl(url);
	    
	    final Activity act = this;
	    
	    // Setting loading when data request to server
	    web.setWebChromeClient(new WebChromeClient(){
	    	public void onProgressChanged(WebView webview, int progress){
	    		
	    		act.setProgress(progress*100);
	    		prgPageLoading.setProgress(progress);
	    		
	    	}
	    	
	    });
	    
	    web.setWebViewClient(new WebViewClient() {
	    	@Override
	        public void onPageStarted( WebView view, String url, Bitmap favicon ) {
	
	            super.onPageStarted( web, url, favicon );
	            prgPageLoading.setVisibility(View.VISIBLE);
	            
	        }
	
	        @Override
	        public void onPageFinished( WebView view, String url ) {
	
	            super.onPageFinished( web, url );
	            
	            prgPageLoading.setProgress(0);
	            prgPageLoading.setVisibility(View.GONE);
	            
	            if(web.canGoBack()){
	            	miPrev.setEnabled(true);
	            	miPrev.setIcon(R.drawable.ic_action_previous_item);
	            }else{
	            	miPrev.setEnabled(false);
	            	miPrev.setIcon(R.drawable.ic_action_previous_item_disabled);
	            }
	            
	            if(web.canGoForward()){
	            	miNext.setEnabled(true);
	            	miNext.setIcon(R.drawable.ic_action_next_item);
	            }else{
	            	miNext.setEnabled(false);
	            	miNext.setIcon(R.drawable.ic_action_next_item_disabled);
	            }
	        }   
	        
	    	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
	    		view.stopLoading();  // may not be needed
	            view.loadData(utils.timeoutMessageHtml, "text/html", "utf-8");
	    	}
	    	
	    	
	    });
	}
	
	 // Listener when item selected Menu in action bar
	 	@Override
	 	public boolean onOptionsItemSelected(MenuItem item) {
	 	    // Handle presses on the action bar items
	 	    switch (item.getItemId()) {
	 	    case android.R.id.home:
	     		// Previous page or exit
	     		finish();
	     		
	    		// Show transition when push back button in actionbar
	     		overridePendingTransition (R.anim.open_main, R.anim.close_next);
	     		
	     		return true;
	     		
	 	    case R.id.abPrevious:
	 	    	if(web.canGoBack()){
					web.goBack();
				}
	 	        break;
	 	    case R.id.abNext:
	 	    	if(web.canGoForward()){
					web.goForward();
				}
	 	    	break;
	 	    case R.id.abRefresh:
	 	    	web.reload();
	 	    	break;
	 	    case R.id.abStop:
	 	    	web.stopLoading();
	 	    	break;
	 	    case R.id.abBrowser:
	 	    	Intent iBrowser = new Intent(Intent.ACTION_VIEW);
				iBrowser.setData(Uri.parse(url));
				startActivity(iBrowser);
				
				// Show transition when button pressed
				overridePendingTransition (R.anim.open_next, R.anim.close_main);
				break;
	        default:
	            return super.onOptionsItemSelected(item);
	 	    }
	 		return true;
	 	}

		/*@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
				case R.id.btnRetry:
					if(utils.isNetworkAvailable()){	
						// Load data from url 
						lytRetry.setVisibility(View.GONE);
						webViewSocial();
					} else {
						lytRetry.setVisibility(View.VISIBLE);
					}
					break;
					
				default:
					break;
			
			}
		}*/
	
/*
	public class GetgameAsyncTask extends AsyncTask<String,Void,String> {

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(ActivityProfile.this);
			pd.setTitle("Loading Game & Location..");
			pd.setMessage("Please wait...");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			
			String email = gda.loadSavedPreferences_string(gda.TAG_EMAIL, cntxt);
			return userFunction.getgame(email);
        }

		@Override
		protected void onPostExecute(String response) {
			try{
				if(response!=null && !(response.equals("0"))){
					gda.savePreferences(gda.TAG_GAME_TYPE, response, cntxt);
					response = response.replaceAll("," , "\n");
					lbl_game_type1.setText("Games Joined (by ID):");
					lbl_game_type.setText(response);
					cl2.setText(": ");
				}
				
			}
			catch(Exception e){	}
			new GetlocationAsyncTask().execute("");
		}
	}
	
	public class GetlocationAsyncTask extends AsyncTask<String,Void,String> {
		@Override
		protected String doInBackground(String... params) {
			String email = gda.loadSavedPreferences_string(gda.TAG_EMAIL, cntxt);
			return userFunction.getlocation(email);
        }

		@Override
		protected void onPostExecute(String response) {
			if(pd!=null){ pd.dismiss(); }
			
			try{
				if(response!=null && !(response.equals("0"))){
					gda.savePreferences(gda.TAG_LOCATION, response, cntxt);
					response = response.replaceAll("," , "\n");
		     		
		     		
				}
				
			}
			catch(Exception e){	}			
		}
	}

	// Listener for option menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case android.R.id.home:
	    		
	    		// Previous page or exit
	    		onBackPressed();
	    		
	    		return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
*/	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
super.onBackPressed();
		
		// Show transition when back button pressed
		overridePendingTransition (R.anim.open_main, R.anim.close_next);
		
	}

}
