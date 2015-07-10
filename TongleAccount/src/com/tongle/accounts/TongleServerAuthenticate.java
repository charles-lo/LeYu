package com.tongle.accounts;

import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the comminication with Parse.com
 *
 */
public class TongleServerAuthenticate implements ServerAuthenticate{
	static final String TAG = TongleServerAuthenticate.class.getSimpleName();
	final String baseUrl = "http://leibaoserver.azurewebsites.net/api/Leibao/";
	static final String COOKIES_HEADER = "Set-Cookie";
	static java.net.CookieManager msCookieManager = new java.net.CookieManager();

	
    @Override
    public String userSignUp(String name, String email, String pass, String authType) throws Exception {
    	
    	final String url = baseUrl + "RegisterUserByEmail?email=" + email + "&password=" + md5(pass);
    	String authtoken = null;
    	String response = getResponse(url, 10000);
    	if (response == null){
		}else{
			JSONObject root = new JSONObject(response);
			Log.d(TAG, "Code: " + root.getString("Code") + "Message: " + root.getString("Message"));
		}

        return authtoken;
    }

    @Override
    public String userSignIn(String email, String pass, String authType) throws Exception {

    	final String url = baseUrl + "LoginByEmail?email=" + email + "&password=" + md5(pass);
    	String authtoken = null;
    	String response = getResponse(url, 10000);
    	if (response == null){
    		throw new Exception("Error signing-in");
		}else{
			JSONObject root = new JSONObject(response);
			Log.d(TAG, "Code: " + root.getString("Code") + " Message: " + root.getString("Message"));
			if (root.getString("Code").equals("0000")) {
				Log.d(TAG, "success: ");
				authtoken = TextUtils.join(",",  msCookieManager.getCookieStore().getCookies());
			} else {
				Log.d(TAG, "error: ");
				throw new Exception("Error signing-in error code: " + root.getString("Code")
						+ " Message: " + root.getString("Message"));
			}
			root.getString("Message");
		}
        return authtoken;
    }

	private String md5(String s) {
	    try {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        // Create Hex String
	        StringBuffer hexString = new StringBuffer();
	        for (int i=0; i<messageDigest.length; i++)
	            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
	        return hexString.toString();

	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return "";
	}
	
	private String getResponse(String urlString, int timeout) {
	    HttpURLConnection connection = null;
	    try {
	        URL url = new URL(urlString);
	        connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("GET");
	        connection.setRequestProperty("Content-length", "0");
	        connection.setUseCaches(false);
	        connection.setAllowUserInteraction(false);
	        connection.setConnectTimeout(timeout);
	        connection.setReadTimeout(timeout);
	        connection.connect();
	        int status = connection.getResponseCode();

	        Log.d(TAG, "" + status);
	        
	        switch (status) {
	            case 200:
	            case 201:{
	                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	                StringBuilder sb = new StringBuilder();
	                String line;
	                while ((line = br.readLine()) != null) {
	                    sb.append(line+"\n");
	                }
	                br.close();
	                Log.d(TAG, sb.toString());
	                Map<String, List<String>> headerFields = connection.getHeaderFields();
	                List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
	                if(cookiesHeader != null)
	                {
	                    for (String cookie : cookiesHeader) 
	                    {
	                    	if(cookie.contains("leibao-id")){
	                    		msCookieManager.getCookieStore().add(null,HttpCookie.parse(cookie).get(0));
	                    	}
	                    }               
	                }
	                return sb.toString();
	            }
	            case 400:
	            	BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
	                StringBuilder sb = new StringBuilder();
	                String line;
	                while ((line = br.readLine()) != null) {
	                    sb.append(line+"\n");
	                }
	                br.close();
	                Log.d(TAG, sb.toString());
	                return sb.toString();
	        }

	    } catch (MalformedURLException ex) {
	        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
	    } catch (IOException ex) {
	        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
	    } finally {
	       if (connection != null) {
	          try {
	              connection.disconnect();
	          } catch (Exception ex) {
	             Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
	          }
	       }
	    }
	    return null;
	}

    private class ParseComError implements Serializable {
        int code;
        String error;
    }
    private class User implements Serializable {

        private String firstName;
        private String lastName;
        private String username;
        private String phone;
        private String objectId;
        public String sessionToken;
        private String gravatarId;
        private String avatarUrl;


        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getObjectId() {
            return objectId;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public String getSessionToken() {
            return sessionToken;
        }

        public void setSessionToken(String sessionToken) {
            this.sessionToken = sessionToken;
        }

        public String getGravatarId() {
            return gravatarId;
        }

        public void setGravatarId(String gravatarId) {
            this.gravatarId = gravatarId;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }
    }
}
