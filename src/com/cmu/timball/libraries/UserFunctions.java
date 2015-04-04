/**
    * File        : UserFuntions.java
    * App name    : Perkutut
    * Version     : 1.2.0
    * Created     : 24/11/14

    * Created by Taufan Erfiyanto & Cahaya Pangripta Alam on 11/24/13.
    * Copyright (c) 2013 pongodev. All rights reserved.
    */

package com.cmu.timball.libraries;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

public class UserFunctions {
	
	private JSONParser jsonParser;

	// Web Service
	private final String Server = "http://timball.website/";
	
	// Folder
	private final String folderMain = "admin/";
	private final String folderApi  = "api/";
	
	private final String folderWebservice  = "WS/";
	
	// Url
	private final String URLApi  = Server+folderMain+folderApi; 
	public final String URLAdmin = Server+folderMain;
	
	private final String URLWS = Server+folderMain+folderWebservice; 
	
	// Service
	private final String service_gcm			  = "register_id_gcm?";
	private final String service_latest_place  	  = "latest_place?";
	private final String service_around_you 	  = "around_you?";
	private final String service_place_detail 	  = "place_detail?";
	private final String service_category_list 	  = "category_list?";
	private final String service_place_by_category= "place_by_category?";
	private final String service_place_by_search  = "place_by_search_name?";
	public final String service_view_location 	  = "view-location.php?";
	private final String service_desc 	  	  	  = "description.php?";
	
	private final String service_login	  	  	  = "login.php?";
	private final String service_signup  	  	  = "signup.php?";
	private final String service_join_game  	  = "api_join_game.php?";
	private final String service_get_game  	  = "get_user_game.php?";
	private final String service_get_location  	  = "get_user_location.php?";
	
	// Param
	private final String param_start_index   	= "start_index=";
	private final String param_items_per_page	= "items_per_page=";
	private final String param_user_lat  		= "user_lat=";
	private final String param_user_lng  		= "user_lng=";
	private final String param_location_id  	= "location_id=";
	private final String param_category_id  	= "category_id=";
	private final String param_key_name  		= "key_name=";
	private final String param_register_id		= "register_id=";
	private final String param_unique_id		= "unique_device_id=";
	
	private final String param_email		= "email=";
	private final String param_password		= "password=";
	private final String param_username	= "username=";
	
	private final String param_game_type	= "game_type=";
	private final String param_players	= "players=";
	private final String param_loc_name	= "location_name=";
	
	// Key object name to get value
	public final String key_location_id		= "location_id";
	public final String key_location_name	= "location_name";
	public final String key_location_address= "address";
	public final String key_location_phone	= "phone";
	public final String key_location_website= "website";
	public final String key_location_desc	= "description";
	public final String key_location_image	= "location_image";
	public final String key_location_lat	= "latitude";
	public final String key_location_lng	= "longitude";
	public final String key_category_id		= "category_id";
	public final String key_category_name	= "category_name";
	public final String key_category_marker	= "category_marker";
	public final String key_status			= "status";
	public final String key_total_data		= "totalData";
	
	public final String key_game_type		= "typeofgame";
	public final String key_date		= "cdate";
	public final String key_s_time		= "stime";
	public final String key_e_time		= "etime";
	public final String key_categ_name		= "category_name";
	public final String key_tot_players		= "tot_players";
	
	// Array
	public final String array_latest_place 	   = "latestPlace";
	public final String array_around_you 	   = "aroundYou";
	public final String array_place_detail 	   = "placeDetail";
	public final String array_category_list	   = "categoryList";
	public final String array_place_by_category= "placeByCategory";
	public final String array_place_by_search  = "placeBySearchName";
	public final String array_register_id		= "registerID";
	
	// LoadUrl
	public final String varLoadURL = Server+folderMain+service_desc+param_location_id;
	
    // Google project id
    public static final String SENDER_ID = "278717888047"; 

	private String webService;
	
	// constructor
	public UserFunctions(){
		jsonParser = new JSONParser();
	}
	
	/**
	 * function make Login Request
	 * @param email
	 * @param password
	 * */
	
	//http://your-website/your-folder/api/register_id_gcm?register_id=1234567890&unique_device_id=123456789
	public JSONObject registerIdGcm(String register_id, String unique_id){		
		webService = URLApi+service_gcm+param_register_id+register_id+"&"+param_unique_id+unique_id;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}
	
	public JSONObject latestPlace(int valueStartIndex, int valueItemsPerPage){		
		webService = URLApi+service_latest_place+param_start_index+valueStartIndex+"&"+param_items_per_page+valueItemsPerPage;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}
	
	public JSONObject aroundYou(double userLat, double userLng){
		webService = URLApi+service_around_you+param_user_lat+userLat+"&"+param_user_lng+userLng;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}

	public JSONObject placeDetailInfo(String locationId){
		webService = URLApi+service_place_detail+param_location_id+locationId;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}
	
	public JSONObject categoryList(){
		webService = URLApi+service_category_list;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}
	
	public JSONObject placeByCategory(String valueCategoryId, int valueStartIndex, int valueItemsPerPage){		
		webService = URLApi+service_place_by_category+param_category_id+valueCategoryId+"&"+param_start_index+valueStartIndex+"&"+param_items_per_page+valueItemsPerPage;		
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}
	
	public JSONObject searchByName(String valueKeyName, int valueStartIndex, int valueItemsPerPage){
		webService = URLApi+service_place_by_search+param_key_name+valueKeyName+"&"+param_start_index+valueStartIndex+"&"+param_items_per_page+valueItemsPerPage;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}
	
	public String login(String email, String password){
		webService = URLWS+service_login+param_email+email+"&"+param_password+password;
		String res = jsonParser.getStringFromUrl(webService);
		return res;
	}
	
	public String signup(String email,String username, String password){
		webService = URLWS+service_signup+param_email+email+"&"+param_username+username+"&"+param_password+password;
		String res = jsonParser.getStringFromUrl(webService);
		return res;
	}
	
	public String joingame(String email,String game_type, String players, String location_id, String loc_name){
		try {
			game_type = URLEncoder.encode(game_type, "utf-8");
			loc_name = URLEncoder.encode(loc_name, "utf-8");
		} catch (UnsupportedEncodingException e1) { e1.printStackTrace(); }
		webService = URLWS+service_join_game+param_email+email+"&"+param_game_type+game_type+"&"+param_players+players+"&"+param_location_id+location_id+"&"+param_loc_name+loc_name;
		//Toast.makeText(cntxt.getApplicationContext(), webService, Toast.LENGTH_LONG).show();
		String res = jsonParser.getStringFromUrl(webService);
		return res;
	}
	
	public String leavegame(String email,String game_type, String players, String location_id, String loc_name){
		try {
			game_type = URLEncoder.encode(game_type, "utf-8");
			loc_name = URLEncoder.encode(loc_name, "utf-8");
		} catch (UnsupportedEncodingException e1) { e1.printStackTrace(); }
		webService = URLWS+service_join_game+param_email+email+"&"+param_game_type+game_type+"&"+param_players+players+"&"+param_location_id+location_id+"&"+param_loc_name+loc_name;
		//Toast.makeText(cntxt.getApplicationContext(), webService, Toast.LENGTH_LONG).show();
		String res = jsonParser.getStringFromUrl(webService);
		return res;
	}
	
	public String getgame(String email){
		webService = URLWS+service_get_game+param_email+email;
		//Toast.makeText(cntxt.getApplicationContext(), webService, Toast.LENGTH_LONG).show();
		String res = jsonParser.getStringFromUrl(webService);
		return res;
	}
	
	public String getlocation(String email){
		webService = URLWS+service_get_location+param_email+email;
		//Toast.makeText(cntxt.getApplicationContext(), webService, Toast.LENGTH_LONG).show();
		String res = jsonParser.getStringFromUrl(webService);
		return res;
	}
}