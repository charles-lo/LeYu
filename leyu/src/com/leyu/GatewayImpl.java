package com.leyu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

public class GatewayImpl implements Gateway{
	
	static final String TAG =  GatewayImpl.class.getSimpleName();
	static GatewayImpl INSTANCE;
	final String baseUrl = "http://leibaoserver.azurewebsites.net/api/Leibao/";
	
	@Override
	public void getMainPageData(final MainPageDataListener listener, String AdminArea) {
		String urlCmd = baseUrl + "getrecmd?";
		if(!TextUtils.isEmpty(AdminArea)){
			urlCmd += "area=" + AdminArea;
		}
		final String url = urlCmd;
		
		new AsyncTask<Void,Void,MainPageData>(){

			@Override
			protected void onPostExecute(MainPageData result) {
				if (result == null){
					listener.onError();
				}else{
					listener.onComplete(result);
				}
				super.onPostExecute(result);
			}

			@Override
			protected MainPageData doInBackground(Void... params) {
				MainPageData data = null;
				try {
					String response = getJSON(url, 10000);
					
					if (response == null){
					}else{
						JSONObject root = new JSONObject(response);
						data = new MainPageData();
						JSONArray tmp = root.getJSONArray("Headline");
						for (int i = 0, size = tmp.length(); i < size; i++)
					    {
					      JSONObject objectInArray = tmp.getJSONObject(i);
					      data.mHeadlines.add(new Headline(objectInArray.getString("ActivityID"), 
					    		  objectInArray.getString("Picture") , objectInArray.getString("Title")
					    		  , objectInArray.getString("Area")));
					     
					    }
						tmp = root.getJSONArray("TopicList");
						for (int i = 0, size = tmp.length(); i < size; i++)
					    {
					      JSONObject objectInArray = tmp.getJSONObject(i);
					      data.mTopList.add(new Topic(objectInArray.getString("TopicID"), 
					    		  objectInArray.getString("Picture") , objectInArray.getString("Title")));
					     
					    }
					}
				} catch (JSONException e) {
					data = null;
					e.printStackTrace();
				}
				return data;
			}}.execute();
	}
	
	@Override
	public void getTopic(final TopicListener listener, String id) {
		final String url = baseUrl + "GetTopic?id=" + id;
		new AsyncTask<Void,Void,TopicData>(){

			@Override
			protected void onPostExecute(TopicData result) {
				if (result == null){
					listener.onError();
				}else{
					listener.onComplete(result);
				}
				super.onPostExecute(result);
			}

			@Override
			protected TopicData doInBackground(Void... params) {
				TopicData data = null;
				try {
					String response = getJSON(url, 10000);
					
					if (response == null){
					}else{
						JSONObject root = new JSONObject(response);
						data = new TopicData();
						data.mTitle = root.getString("Title");
						data.mPicture = root.getString("Picture");
						JSONArray tmpArray = root.getJSONArray("Contents");
						for (int i = 0, size = tmpArray.length(); i < size; i++)
					    {
					      JSONObject objectInArray = tmpArray.getJSONObject(i);
					      data.mContents.add(new Content(objectInArray.getInt("Type"), 
					    		  objectInArray.getString("Text") , objectInArray.getString("Picture")
					    		  , objectInArray.getString("ActivityID")));
					     
					    }
					}
				} catch (JSONException e) {
					data = null;
					e.printStackTrace();
				}
				return data;
			}}.execute();
	}
	
	@Override
	public void getActivity(final ActivityListener listener, String id) {
		final String url = baseUrl + "GetActivity?id=" + id;
		Log.d(TAG, "getActivity " + url);
		new AsyncTask<Void,Void,ActivityData>(){

			@Override
			protected void onPostExecute(ActivityData result) {
				if (result == null){
					listener.onError();
				}else{
					listener.onComplete(result);
				}
				super.onPostExecute(result);
			}

			@Override
			protected ActivityData doInBackground(Void... params) {
				ActivityData data = null;
				try {
					String response = getJSON(url, 10000);
					
					if (response == null){
					}else{
						JSONObject root = new JSONObject(response);
						data = new ActivityData();
						data.mTitle = root.getString("Title");
						data.mPicture = root.getString("Picture");
						data.mBeginDate = root.getString("BeginDate");
						data.mEndDate = root.getString("EndDate");
						data.mPlace = root.getString("Place");
						data.mAddress = root.getString("Address");
						data.mOrganizer = root.getString("Organizer");
						data.mDescription = root.getString("Description");
						data.mPhysical = root.getInt("Physical");
						data.mAesthetic = root.getInt("Aesthetic");
						data.mScience = root.getInt("Science");
						data.mSocially = root.getInt("Socially");
						data.mCulture = root.getInt("Culture");

						JSONObject objectInArray = null;
						JSONArray tmpArray = root.getJSONArray("ActivityAgeLevels");
						for (int i = 0, size = tmpArray.length(); i < size; i++)
					    {
					      objectInArray = tmpArray.getJSONObject(i).getJSONObject("ActivityAgeLevelSetting");
					      data.mActivityAgeLevelSettings.add(
					    		  new ActivityAgeLevelSetting(objectInArray.getString("ID")
					    				  ,objectInArray.getString("Description")));
					    }
					}
				} catch (JSONException e) {
					data = null;
					e.printStackTrace();
				}
				return data;
			}}.execute();
		
	}

	public String getJSON(String url, int timeout) {
	    HttpURLConnection c = null;
	    try {
	        URL u = new URL(url);
	        c = (HttpURLConnection) u.openConnection();
	        c.setRequestMethod("GET");
	        c.setRequestProperty("Content-length", "0");
	        c.setUseCaches(false);
	        c.setAllowUserInteraction(false);
	        c.setConnectTimeout(timeout);
	        c.setReadTimeout(timeout);
	        c.connect();
	        int status = c.getResponseCode();

	        switch (status) {
	            case 200:
	            case 201:
	                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
	                StringBuilder sb = new StringBuilder();
	                String line;
	                while ((line = br.readLine()) != null) {
	                    sb.append(line+"\n");
	                }
	                br.close();
	                return sb.toString();
	        }

	    } catch (MalformedURLException ex) {
	        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
	    } catch (IOException ex) {
	        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
	    } finally {
	       if (c != null) {
	          try {
	              c.disconnect();
	          } catch (Exception ex) {
	             Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
	          }
	       }
	    }
	    return null;
	}	
	
	static Gateway getInstance(){
		if(INSTANCE == null){
			INSTANCE = new GatewayImpl();
		}
		
		return INSTANCE;
		
	}
}