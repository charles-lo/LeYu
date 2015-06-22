package com.leyu;

import java.util.ArrayList;
import java.util.List;

public interface Gateway{
	
	void getMainPageData(Listener listener);
	
	public interface Listener{
		
		void onComplete(MainPageData data);
	}
	
	class MainPageData{
		String mTitle;
		String mUrl;
		List<Topic> mTopList = new ArrayList<Topic>();
	}
	
	class Topic{
		String mID;
		String mPicture;
		String mTitle;
		
		Topic(String id, String picture, String Title){
			mID = id;
			mPicture = picture;
			mTitle = Title;
		}
	}
}