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

import com.leyu.Gateway.Listener;

import android.os.AsyncTask;
import android.util.Log;

public class GatewayImpl implements Gateway{
	
	static final String TAG =  GatewayImpl.class.getSimpleName();
	static GatewayImpl INSTANCE;
	final String baseUrl = "http://leibaoserver.azurewebsites.net/api/Leibao/";
	
	@Override
	public void getMainPageData(final Listener listener) {
		final String url = baseUrl + "GetMainPageData";
		
		new AsyncTask<Void,Void,Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				try {
					JSONObject response = new JSONObject(getJSON(url, 10000));
					MainPageData data = new MainPageData();
					JSONObject tmp = response.getJSONObject("Headline");
					data.mTitle = tmp.getString("Title");
					data.mUrl = tmp.getString("Picture");
					JSONArray tmpArray = response.getJSONArray("TopicList");
					for (int i = 0, size = tmpArray.length(); i < size; i++)
				    {
				      JSONObject objectInArray = tmpArray.getJSONObject(i);
				      data.mTopList.add(new Topic(objectInArray.getString("TopicID"), 
				    		  objectInArray.getString("Picture") , objectInArray.getString("Title")));
				     
				    }
					listener.onComplete(data);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
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