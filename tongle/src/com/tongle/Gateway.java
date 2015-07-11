package com.tongle;

import java.util.ArrayList;
import java.util.List;

import android.accounts.AccountManager;

public interface Gateway {
	
	void initialize(String token, MainActivity mainActivity);

	void getMainPageData(MainPageDataListener listener, String AdminArea);
	
	void getTopic(TopicListener listener, String id);
	
	void getActivity(ActivityListener listener, String id);
	
	void getWeekend(ActivitysListener listener, String id);
	
	void getFree(ActivitysListener listener, String id);
	
	void getHot(ActivitysListener listener, String id);
	
	void getNear(ActivitysListener listener, String area);
	
	//
	public interface MainPageDataListener {

		void onComplete(MainPageData data);

		void onError();
	}
	
	public interface TopicListener {

		void onComplete(TopicData data);

		void onError();
	}
	
	public interface ActivityListener {

		void onComplete(ActivityData data);

		void onError();
	}
	
	public interface ActivitysListener {

		void onComplete(List<ActivityLiteData> data);

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
		int mType;
		String mText;
		String mPicture;
		String mActivityID;

		Content(int type, String text, String picture, String activityID) {
			mType = type;
			mText = text;
			mPicture = picture;
			mActivityID = activityID;
		}
	}
	
	class ActivityLiteData {
		String mTitle;
		String mPicture;
		String mBeginDate;
		String mEndDate;
		String mAddress;
		Boolean isHot;
	}
	
	class ActivityData {
		String mID;
		String mPicture;
		String mTitle;
		String mBeginDate;
		String mEndDate;
		String mPlace;
		String mPrice;
		String mAddress;
		String mOrganizer;
		String mDescription;
		int mPhysical;
		int mAesthetic;
		int mScience;
		int mSocially;
		int mCulture;
		Boolean isHot;
		List<ActivityAgeLevelSetting> mActivityAgeLevelSettings = new ArrayList<ActivityAgeLevelSetting>();
	}
	
	class ActivityAgeLevelSetting {
		String mID;
		String mDescription;

		ActivityAgeLevelSetting(String ID, String description) {
			mID = ID;
			mDescription = description;
		}
	}
}