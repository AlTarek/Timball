/**
    * File        : ActivityDetailPlace.java
    * App name    : Perkutut
    * Version     : 1.2.0
    * Created     : 01/19/14

    * Created by Taufan Erfiyanto & Cahaya Pangripta Alam on 11/24/13.
    * Copyright (c) 2013 pongodev. All rights reserved.
    */

package com.cmu.timball;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cmu.timball.ads.Ads;
import com.cmu.timball.libraries.UserFunctions;
import com.cmu.timball.utils.Utils;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

public class ActivityDetailPlace extends ActionBarActivity implements OnClickListener{
	
	// Create an instance of ActionBar, AdView, UserFunctions, JSONObject, Utils, WebView
	private ActionBar actionbar;
	private AdView adView;
	private UserFunctions userFunction;
	private JSONObject json;
	private Utils utils;
	private WebView webDesc;
	
	// Declare variables to store data
	private String mGetLocationId;
	private String mLocationName;
	private String mAddress;
	private String mPhone;
	private String mWebsite;
	private String mDesc;
	private String mIcMarkerLocation;
	private String mImgLocation;
	private Double mDblLatitude;
	private Double mDblLongitude;
	private int mSelectedMapType;
	private float mSelectedMapZoom;
	private String mdate, mstime, metime, mgame_type, mcateg_name;
	
	// Declare view objects
	private TextView lblPlaceName, lblAddress, lblPhone, lblWebsite, lblNoResult, lblAlert, lbldate, lblstime, lbletime, lbl_players;
	private ImageView imgThumbnail;
	private LinearLayout lytMedia, lytRetry, lytDetail;
	private Button btnRetry, btn_join_game, btn_leave_game;
	private ImageButton imgBtnShare, imgBtnDirection;
//	private Spinner sp_no_players;
	// Declare object to handle map
    private GoogleMap map;
    private SupportMapFragment fragMap;
	Global_data gda;
	Context cntxt;
	int dec_players=0;
	String game_string, location_string;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info);
        
     	// Get ActionBar and set back button on actionbar
     	actionbar = getSupportActionBar();
     	actionbar.setDisplayHomeAsUpEnabled(true);  
     	
        // Declare object of userFunction and utils class
		userFunction = new UserFunctions();
		utils		 = new Utils(this);
		gda 		 = new Global_data();
		cntxt		 = this;
		
		// Get intent Data from ActivityHome, ActivityPlaceAroundYou, ActivityPlaceList OR ActivitySearch
        Intent i = getIntent();
        mGetLocationId = i.getStringExtra(utils.EXTRA_LOCATION_ID);
        
        // Load Map Type and Map zoom in preferences if have, if doesnt have it using default
    	mSelectedMapType = utils.loadPreferences(getString(R.string.preferences_type));
    	mSelectedMapZoom = utils.loadPreferences(getString(R.string.preferences_zoom));
    	
    	// Condition if Map Zoom is 0
    	if(mSelectedMapZoom == 0){
    		mSelectedMapZoom = 15;
    	}
     	
     	// Connect view objects and xml ids
		lblPlaceName	= (TextView) findViewById(R.id.lblPlaceName);
		lblAddress		= (TextView) findViewById(R.id.lblAddress);
		lblPhone		= (TextView) findViewById(R.id.lblPhone);
		lblWebsite		= (TextView) findViewById(R.id.lblWebsite);
		lytMedia		= (LinearLayout) findViewById(R.id.lytMedia);
		lytRetry		= (LinearLayout) findViewById(R.id.lytRetry);
		lytDetail		= (LinearLayout) findViewById(R.id.lytDetail);
		btnRetry		= (Button) findViewById(R.id.btnRetry);
		imgBtnShare 	= (ImageButton) findViewById(R.id.imgShare);
		imgBtnDirection = (ImageButton) findViewById(R.id.imgDirection);
		imgThumbnail 	= (ImageView) findViewById(R.id.imgLocation);
		fragMap 		= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		adView 			= (AdView) findViewById(R.id.adView);
		webDesc 		= (WebView) findViewById(R.id.webDesc);
		lblNoResult		= (TextView) findViewById(R.id.lblNoResult);
		lblAlert		= (TextView) findViewById(R.id.lblAlert);
		
		lbldate		= (TextView) findViewById(R.id.lbldate);
		lblstime	= (TextView) findViewById(R.id.lblstime);
		lbl_players = (TextView) findViewById(R.id.lbl_players);
		
		btn_join_game = (Button) findViewById(R.id.btn_join_game); 
		btn_leave_game = (Button) findViewById(R.id.btn_leave_game);
		btn_leave_game.setVisibility(View.GONE);
	//	sp_no_players = (Spinner) findViewById(R.id.sp_no_players);
	//	sp_no_players.setSelection(0);
		
		lblWebsite.setOnClickListener(this);
		imgBtnShare.setOnClickListener(this);
		imgBtnDirection.setOnClickListener(this);
     	btnRetry.setOnClickListener(this);
     	
		// Get screen device width and height
        DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int wPix = dm.widthPixels;
		int hPix = wPix / 2 + 50;
		
		// Change cover image width and height
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(wPix, hPix);
        lytMedia.setLayoutParams(lp);
		
        // Call Method setUpMapIfNeeded
        setUpMapIfNeeded();
        
 		/* CHECK_PLAY_SERV = 1 means Google Play services version on the device 
	    supports the version of the client library you are using */
 		if(utils.loadPreferences(utils.CHECK_PLAY_SERV)==1){
 			// Check the connection
 			if(utils.isNetworkAvailable()){
 				
            	// Call asynctask class
             	new getDataAsync().execute();
             	
	            // Condition for admob (0=gone, 1=visible)
	            if(utils.paramAdmob==1){
	             	
	            	adView.setVisibility(View.VISIBLE);
	            	// load ads
		            Ads.loadAds(adView);	            		            
	            }
 			} else {
 				lblNoResult.setVisibility(View.GONE);
 	    		lytRetry.setVisibility(View.VISIBLE);
 	    		lblAlert.setText(R.string.no_connection);
 			}
 		}
     	     
		
 		btn_join_game.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			/*	int item_pos = sp_no_players.getSelectedItemPosition(); 
				String game_type = mgame_type;
				if(item_pos==0){
					Toast.makeText(getApplicationContext(), "Select number of players", Toast.LENGTH_SHORT).show();
				}else{
					
					if(game_type.equalsIgnoreCase("5 vs 5") && item_pos<1)
						Toast.makeText(getApplicationContext(), "Initially 10 players needed", Toast.LENGTH_SHORT).show();
					else if(game_type.equalsIgnoreCase("7 vs 7") && item_pos<2)
						Toast.makeText(getApplicationContext(), "Initially 14 players needed", Toast.LENGTH_SHORT).show();
					else if(game_type.equalsIgnoreCase("11 vs 11") && item_pos<3)
						Toast.makeText(getApplicationContext(), "Initially 22 players needed", Toast.LENGTH_SHORT).show();
					else{
						
						new JoingameAsyncTask().execute("");

					}
				}*/
				new JoingameAsyncTask().execute("");
			}
		});
    }
		
	public class JoingameAsyncTask extends AsyncTask<String,Void,String> {

		ProgressDialog pd;
		String game_type;
		String game_string, location_string;
		
		@Override
		protected void onPreExecute() {

			game_type = lblWebsite.getText().toString();
			pd = new ProgressDialog(ActivityDetailPlace.this);
			pd.setTitle("Joining Game..");
			pd.setMessage("Please wait...");
			pd.setCancelable(false);
			pd.show();
		}


		@Override
		protected String doInBackground(String... params) {
			
			dec_players--;
			String email = gda.loadSavedPreferences_string(gda.TAG_EMAIL, cntxt);
			String players = String.valueOf(dec_players);
			String game_type = lblWebsite.getText().toString().trim();
			
			game_string = gda.loadSavedPreferences_string(gda.TAG_GAME_TYPE, cntxt);
			boolean status = false;
			if(game_string.trim().length()>0){
				List<String> items = Arrays.asList(game_string.split("\\s*,\\s*"));
				for(int i=0;i<items.size();i++){ if(items.get(i).trim().equals(mgame_type)){ status=true; } }
				if(!(status)){ game_string += ","+mgame_type; }
			}else{
				game_string = mgame_type;
			}
			
			location_string = gda.loadSavedPreferences_string(gda.TAG_LOCATION, cntxt);
			status = false;
			if(location_string.trim().length()>0){
				List<String> items = Arrays.asList(location_string.split("\\s*,\\s*"));
				for(int i=0;i<items.size();i++){ if(items.get(i).trim().equals(mLocationName)){ status=true; } }
				if(!(status)){ location_string += ","+mLocationName; }
			}else{
				location_string = mLocationName;
			}
			
			
           	return userFunction.joingame(email, game_string, players, mGetLocationId,location_string);
        }

		@Override
		protected void onPostExecute(String response) {
			
			if(pd!=null){
				pd.dismiss();
			}
			try{
				if(response!=null && Integer.parseInt(response)>0){
					
					gda.savePreferences(gda.TAG_LOCATION, location_string, cntxt);
					gda.savePreferences(gda.TAG_GAME_TYPE, game_string, cntxt);
					Toast.makeText(getBaseContext(), "Game Joined", Toast.LENGTH_SHORT).show();
				//	Toast.makeText(getBaseContext(), dec_players + " Players Needed Now ", Toast.LENGTH_SHORT).show();
					lbl_players.setText(dec_players+" Players Needed");
					btn_join_game.setVisibility(View.GONE);
					btn_leave_game.setVisibility(View.VISIBLE);
					if(dec_players==0){
						dec_players=0;
					//	btn_join_game.setEnabled(false);
						btn_join_game.setVisibility(View.GONE);
					}
				}
				
			}
			catch(Exception e){
				Toast.makeText(getBaseContext(), "something went wrong!", Toast.LENGTH_SHORT).show();
				
			}

			
			
		}
	}
	
	
	
	// AsyncTask to Get Data from Server and put it on View Object
	public class getDataAsync extends AsyncTask<Void, Void, Void>{

		ProgressDialog progress;

    	@Override
		 protected void onPreExecute() {
    		/////////////////
		  // TODO Auto-generated method stub
    		// Show progress dialog when fetching data from database
    		progress= ProgressDialog.show(
    				ActivityDetailPlace.this, 
    				"", 
    				getString(R.string.loading_data), 
    				true);
			
    	}

    	@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
    		
    		// Method to get Data from Server
			getDataFromServer();
			return null;
		}
    	
    	@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			if(json != null) {
				// Display Data
				lytDetail.setVisibility(View.VISIBLE);
				lytRetry.setVisibility(View.GONE);
				lblPlaceName.setText(mLocationName);
				lblAddress.setText(mAddress);
				lblPhone.setText(mPhone);
				lblWebsite.setText(mgame_type);
				
				lbldate.setText(mdate);
				lblstime.setText(mstime);
				lbletime.setText(metime);
				
				if(mcateg_name.equalsIgnoreCase("open")){
					btn_join_game.setVisibility(View.VISIBLE);
					
					if(dec_players==0){
						if(mgame_type.equalsIgnoreCase("5 vs 5"))
							dec_players=10;
						if(mgame_type.equalsIgnoreCase("7 vs 7"))
							dec_players=14;
						if(mgame_type.equalsIgnoreCase("11 vs 11"))
							dec_players=22;
					}
					
				}else{
					btn_join_game.setVisibility(View.GONE);
				}
				
				game_string = gda.loadSavedPreferences_string(gda.TAG_GAME_TYPE, cntxt);
				boolean status = false;
				if(game_string.trim().length()>0){
					List<String> items = Arrays.asList(game_string.split("\\s*,\\s*"));
					for(int z=0;z<items.size();z++){ if(items.get(z).trim().equals(mgame_type)){ status=true; } }
				}
				
				if(status)
					btn_join_game.setVisibility(View.GONE);
				
				lbl_players.setText(dec_players+" Players Needed");
				
				/*if(mgame_type.equalsIgnoreCase("5 vs 5"))
					lbl_players.setText(dec_players+" Players Needed");
				if(mgame_type.equalsIgnoreCase("7 vs 7"))
					lbl_players.setText(dec_players + " Players Needed");
				if(mgame_type.equalsIgnoreCase("11 vs 11"))
					lbl_players.setText(dec_players + " Players Needed");*/
				
				
				
				// Load data from url 
				webDesc.loadUrl(userFunction.varLoadURL+mGetLocationId);
	
				// Set Image thumbnail from Server with picasso
		        Picasso.with(getApplicationContext())
		        .load(userFunction.URLAdmin+mImgLocation)
		        .fit().centerCrop()
		        .tag(getApplicationContext())
		        .into(imgThumbnail);
		        
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mDblLatitude, mDblLongitude), mSelectedMapZoom));
		    	
				// Call AsynTask class
		    	new loadMarkerFromServer().execute();
		    	
			} else {
				lytDetail.setVisibility(View.GONE);
				lytRetry.setVisibility(View.VISIBLE);
            	Toast.makeText(ActivityDetailPlace.this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
			}
			
          if(progress.isShowing()) {
        	  progress.dismiss();
			}

		}
		
	}
	
	// AsyncTask class to load marker from server in UI background
	public class loadMarkerFromServer extends AsyncTask<Void, Void, Void>{
		Bitmap bmImg;
		
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		    Bitmap bmp = Bitmap.createBitmap(80, 80, conf);
		    Canvas canvas = new Canvas(bmp);

		    // Paint defines the text color,
		    // Stroke width, size
		    Paint color = new Paint();
		    color.setTextSize(35);
		    color.setColor(Color.BLACK);
		    
		    URL url;
			try {
				url = new URL(userFunction.URLAdmin+mIcMarkerLocation);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setDoInput(true);
	            conn.connect();
	            InputStream is = conn.getInputStream();
	            bmImg = BitmapFactory.decodeStream(is);

			    //modify canvas
			    canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
			            R.drawable.icon), 0,0, color);
			    canvas.drawText("User Name!", 30, 40, color);

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);			

		    // Add marker to Map
		    map.addMarker(new MarkerOptions().position(new LatLng(mDblLatitude, mDblLongitude))
		    .icon(BitmapDescriptorFactory.fromBitmap(bmImg))
		    .anchor(0.5f, 1)); //Specifies the anchor to be
		               //at a particular point in the marker image.
		}
	
	}

	// Method to check map
	private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
        	map = fragMap.getMap();
        	
             if (map != null) setUpMap();
                 
        }
    }
	
	// Method to Set Map Type Selected
    private void setUpMap() {
    	// Call previous map type setting
    	setMapType(mSelectedMapType);
	}
    
    // Method to set map type based on dialog map type setting
 	void setMapType(int position){
 		switch(position){
 		case 0:
 			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
 			break;
 		case 1:
 			map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
 			break;
 		case 2:
 			map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
 			break;
 		case 3:
 			map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
 			break;
 		}
 	}
 	
 	// Method to get Data from Server
	public void getDataFromServer(){
	       
        try {
            json = userFunction.placeDetailInfo(mGetLocationId);
            
            if(json != null) {
	            JSONArray dataLocationArray = json.getJSONArray(userFunction.array_place_detail);
	            JSONObject locationObject 	= dataLocationArray.getJSONObject(0);
	            System.out.println("dataLocationArray = " + dataLocationArray);
	            
	            // Store Data to Variables
	            mLocationName 	 = locationObject.getString(userFunction.key_location_name);
	            mAddress 		 = locationObject.getString(userFunction.key_location_address);
	            mPhone 			 = locationObject.getString(userFunction.key_location_phone);
	            mWebsite 		 = locationObject.getString(userFunction.key_location_website);
	            mDesc 			 = locationObject.getString(userFunction.key_location_desc);
	            mDblLatitude 	 = locationObject.getDouble(userFunction.key_location_lat);
	            mDblLongitude 	 = locationObject.getDouble(userFunction.key_location_lng);
	            mImgLocation 	 = locationObject.getString(userFunction.key_location_image);
	            mIcMarkerLocation= locationObject.getString(userFunction.key_category_marker);
	            mgame_type		 = locationObject.getString(userFunction.key_game_type);
	            mdate			 = locationObject.getString(userFunction.key_date);
				mstime 			 = locationObject.getString(userFunction.key_s_time);
				metime			 = locationObject.getString(userFunction.key_e_time);
				mcateg_name		 = locationObject.getString(userFunction.key_categ_name);
				dec_players		 = Integer.parseInt(locationObject.getString(userFunction.key_tot_players));
            }      
            
        } catch (JSONException e) {
            // TODO Auto-generated catch block
        	Log.i("ActivityDetailPlace", "getDataFromServer: "+e);
        }      
    }
	
	public void setImageInThread(final ImageView imageView, final String url) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                Drawable result = (Drawable) message.obj;
                imageView.setImageDrawable(result);
            }
        };

        new Thread() {
            @Override
            public void run() {
                Drawable drawable = LoadImageFromWebOperations(url);
                Message message = handler.obtainMessage(1, drawable);
                handler.sendMessage(message);
            }
        }.start();
    }
    
    private Drawable LoadImageFromWebOperations(String url) {
    	try {
    	    InputStream is = (InputStream) new URL(url).getContent();
    	    Drawable d = Drawable.createFromStream(is, "image.png");
    	    return d;
    	} catch (Exception e) {
    	    System.out.println("Exc=" + e);
    	    return null;
    	}
    } 
    
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }
    
	// Listener for option menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case android.R.id.home:
	    		
	    		// Previous page or exit
	    		finish();
	    		
	    		// Show transition when push back button in actionbar
	    		overridePendingTransition (R.anim.open_main, R.anim.close_next);
	    		
	    		return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	// Listener for on click
	@Override
	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.imgShare:
			 // Share google play link of this app to other app such as facebook, twitter etc
			 i = new Intent(this, ActivityShare.class);
			 i.putExtra(utils.EXTRA_LOCATION_ID, mGetLocationId);
			 i.putExtra(utils.EXTRA_LOCATION_NAME, mLocationName);
			 i.putExtra(utils.EXTRA_LOCATION_DESC, mDesc);
			 i.putExtra(utils.EXTRA_LOCATION_IMG, mImgLocation);
			 startActivity(i); 
			 
				// Show transition when button pressed
			 overridePendingTransition (R.anim.open_next, R.anim.close_main);
			 break;
			 
		case R.id.btnRetry:
			// Retry to get Data
			new getDataAsync().execute();
			break;
			
		case R.id.imgDirection:
			// Open ActivityDirection to get Direction
			i = new Intent(this, ActivityDirection.class);
			i.putExtra(utils.EXTRA_DEST_LAT, mDblLatitude);
			i.putExtra(utils.EXTRA_DEST_LNG, mDblLongitude);
			i.putExtra(utils.EXTRA_DEST_MARKER, mIcMarkerLocation);
			startActivity(i);
			
			// Show transition when button pressed
			overridePendingTransition (R.anim.open_next, R.anim.close_main);
			break;	
		
		case R.id.lblWebsite:
			// Open Built-in browser
			i = new Intent(this, ActivityBrowser.class);
			i.putExtra(utils.EXTRA_LOCATION_URL, mWebsite);
			i.putExtra(utils.EXTRA_LOCATION_NAME, mLocationName);
			startActivity(i);
			
			// Show transition when button pressed
			overridePendingTransition (R.anim.open_next, R.anim.close_main);
		default:
			break;
		}
	}

	// Listener when back button pressed
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		// Show transition when back button pressed
		overridePendingTransition (R.anim.open_main, R.anim.close_next);
		
	}
	
}