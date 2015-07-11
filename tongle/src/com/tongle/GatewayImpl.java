package com.tongle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tongle.Gateway.ActivityLiteData;

import android.os.AsyncTask;
import android.text.TextUtils;

public class GatewayImpl implements Gateway{
	
	static final String TAG =  GatewayImpl.class.getSimpleName();
	static final  int ERROR_NETWORK = 1;
	static final  int ERROR_SERVER = 2;
	static GatewayImpl sInstance;
	static String sAuthToken;
	static private MainActivity mMainActivity;
	final String baseUrl = "http://leibaoserver.azurewebsites.net/api/Leibao/";

	@Override
	public void initialize(String token, MainActivity mainActivity) {
		sAuthToken = token;
		mMainActivity = mainActivity;
	}
	
	@Override
	public void getMainPageData(final MainPageDataListener listener, String AdminArea) {
		String urlCmd = baseUrl + "getrecmd?";
		if (!TextUtils.isEmpty(AdminArea)) {
			urlCmd += "area=" + AdminArea;
		}
		final String url = urlCmd;

		new AsyncTask<Void, Void, MainPageData>() {

			@Override
			protected void onPostExecute(MainPageData result) {
				if (result == null) {
					listener.onError();
				} else {
					listener.onComplete(result);
				}
				super.onPostExecute(result);
			}

			@Override
			protected MainPageData doInBackground(Void... params) {
				MainPageData data = null;
				try {
					String response = getResponse(url, 10000);

					if (response == null) {
					} else {
						JSONObject root = new JSONObject(response);
						JSONArray tmp;
						data = new MainPageData();
						if (root.has("Headline")) {
							tmp = root.getJSONArray("Headline");
							for (int i = 0, size = tmp.length(); i < size; i++) {
								JSONObject objectInArray = tmp.getJSONObject(i);
								data.mHeadlines.add(new Headline(objectInArray.getString("ActivityID"), objectInArray.getString("Picture"), objectInArray.getString("Title"), objectInArray
										.getString("Area")));

							}
						}
						if (root.has("TopicList")) {
							tmp = root.getJSONArray("TopicList");
							for (int i = 0, size = tmp.length(); i < size; i++) {
								JSONObject objectInArray = tmp.getJSONObject(i);
								data.mTopList.add(new Topic(objectInArray.getString("TopicID"), objectInArray.getString("Picture"), objectInArray.getString("Title")));

							}
						}
					}
				} catch (JSONException e) {
					data = null;
					e.printStackTrace();
				}
				return data;
			}
		}.execute();
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
					String response = getResponse(url, 10000);

					if (response == null) {
					} else {
						JSONObject root = new JSONObject(response);
						data = new TopicData();
						if (root.has("Title")) {
							data.mTitle = root.getString("Title");
						}
						if (root.has("Picture")) {
							data.mPicture = root.getString("Picture");
						}
						if (root.has("Contents")) {
							JSONArray tmpArray = root.getJSONArray("Contents");
							for (int i = 0, size = tmpArray.length(); i < size; i++) {
								JSONObject objectInArray = tmpArray.getJSONObject(i);
								data.mContents
										.add(new Content(objectInArray.getInt("Type"), objectInArray.getString("Text"), objectInArray.getString("Picture"), objectInArray.getString("ActivityID")));
							}
						}
					}
				} catch (JSONException e) {
					data = null;
					e.printStackTrace();
				}
				return data;
			}
		}.execute();
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
					String response = getResponse(url, 10000);
					
					if (response == null){
					}else{
						JSONObject root = new JSONObject(response);
						data = new ActivityData();
						if (root.has("Title")) {
							data.mTitle = root.getString("Title");
						}
						if (root.has("Picture")) {
							data.mPicture = root.getString("Picture");
						}
						if (root.has("BeginDate")) {
							data.mBeginDate = root.getString("BeginDate");
						}
						if (root.has("EndDate")) {
							data.mEndDate = root.getString("EndDate");
						}
						if (root.has("Place")) {
							data.mPlace = root.getString("Place");
						}
						if (root.has("Address")) {
							data.mAddress = root.getString("Address");
						}
						if (root.has("Organizer")) {
							data.mOrganizer = root.getString("Organizer");
						}
						if (root.has("Description")) {
							data.mDescription = root.getString("Description");
						}
						if (root.has("Physical")) {
							data.mPhysical = root.getInt("Physical");
						}
						if (root.has("Aesthetic")) {
							data.mAesthetic = root.getInt("Aesthetic");
						}
						if (root.has("Science")) {
							data.mScience = root.getInt("Science");
						}
						if (root.has("Socially")) {
							data.mSocially = root.getInt("Socially");
						}
						if (root.has("Culture")) {
							data.mCulture = root.getInt("Culture");
						}
						if (root.has("ActivityAgeLevels")) {
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
					}
				} catch (JSONException e) {
					data = null;
					e.printStackTrace();
				}
				return data;
			}}.execute();
		
	}
	
	@Override
	public void getWeekend(ActivitysListener listener, String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getFree(ActivitysListener listener, String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getHot(ActivitysListener listener, String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getNear(final ActivitysListener listener, String area) {
		final String url = baseUrl + "FilterActivity?area=" + area;
		Log.d(TAG, "getNear " + url);
		new AsyncTask<Void,Void,List<ActivityLiteData>>(){

			@Override
			protected void onPostExecute(List<ActivityLiteData> result) {
				if (result == null){
					listener.onError();
				}else{
					listener.onComplete(result);
				}
				super.onPostExecute(result);
			}

			@Override
			protected List<ActivityLiteData> doInBackground(Void... params) {
				List<ActivityLiteData> data = null;
				try {
					String response = getResponse(url, 10000);
					
					if (response == null){
					}else{
						JSONArray root = new JSONArray(response);
					}
				} catch (JSONException e) {
					data = null;
					e.printStackTrace();
				}
				return data;
			}}.execute();
		
	}

	public String getResponse(String urlString, int timeout) {
		HttpURLConnection connection = null;
		int retry = 0;
		while (retry < 5) {
			try {
				if (retry > 0) {
					Thread.sleep(1000);
				}
				URL url = new URL(urlString);
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Content-length", "0");
				connection.setRequestProperty("Cookie", sAuthToken);
				connection.setUseCaches(false);
				connection.setAllowUserInteraction(false);
				connection.setConnectTimeout(timeout);
				connection.setReadTimeout(timeout);
				connection.connect();
				int status = connection.getResponseCode();

				switch (status) {
				case 200:
				case 201:
				default:
					String ret;
					BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					StringBuilder sb = new StringBuilder();
					String line;
					while ((line = br.readLine()) != null) {
						sb.append(line + "\n");
					}
					br.close();
					ret = sb.toString();
					JSONObject root = new JSONObject(ret);
					if (root.has("Code")) {
						String code = root.getString("Code");
						if (!TextUtils.isEmpty(code) && code.equals("0005")) {
							if (mMainActivity != null) {
								mMainActivity.invalidateAuthToken();
							}
						}
					} else {
						return ret;
					}
				}

			} catch (MalformedURLException ex) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				if (connection != null) {
					try {
						connection.disconnect();
					} catch (Exception ex) {
						Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
			retry++;
			continue;
		}
		return null;
	}
	
	static Gateway getInstance(){
		if(sInstance == null){
			sInstance = new GatewayImpl();
		}
		
		return sInstance;
		
	}
}