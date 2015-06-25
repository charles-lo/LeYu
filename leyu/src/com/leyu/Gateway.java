package com.leyu;

import java.util.ArrayList;
import java.util.List;

public interface Gateway {

	void getTopic(TopicListener listener, String id);

	void getMainPageData(MainPageDataListener listener, String AdminArea);

	
	//
	public interface TopicListener {

		void onComplete(TopicData data);

		void onError();
	}

	public interface MainPageDataListener {

		void onComplete(MainPageData data);

		void onError();
	}

	class MainPageData {
		List<Headline> mHeadlines = new ArrayList<Headline>();
		List<Topic> mTopList = new ArrayList<Topic>();
	}

	class Headline {
		String mActivityID;
		String mPicture;
		String mTitle;
		String mArea;

		Headline(String id, String picture, String Title, String Area) {
			mActivityID = id;
			mPicture = picture;
			mTitle = Title;
			mArea = Area;
		}
	}

	class Topic {
		String mID;
		String mPicture;
		String mTitle;

		Topic(String id, String picture, String Title) {
			mID = id;
			mPicture = picture;
			mTitle = Title;
		}
	}

	class TopicData {
		String mID;
		String mPicture;
		String mTitle;
		List<Content> mContents = new ArrayList<Content>();
	}

	class Content {
		String mType;
		String mText;
		String mPicture;
		String mActivityID;

		Content(String type, String text, String picture, String activityID) {
			mType = type;
			mText = text;
			mPicture = picture;
			mActivityID = activityID;
		}
	}
}