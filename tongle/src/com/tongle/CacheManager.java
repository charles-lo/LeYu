package com.tongle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tongle.Gateway.ActivityData;
import com.tongle.Gateway.ActivityLiteData;
import com.tongle.Gateway.MainPageData;

class CacheManager {

	//
	private MainPageData mMainPageData;
	private Map<String, ActivityData> mActivitys = new HashMap<String, ActivityData>();
	private List<ActivityLiteData> mWeekEnd = new ArrayList<ActivityLiteData>();
	private List<ActivityLiteData> mFree = new ArrayList<ActivityLiteData>();
	private List<ActivityLiteData> mHot = new ArrayList<ActivityLiteData>();
	private List<ActivityLiteData> mNear = new ArrayList<ActivityLiteData>();
	private List<String> mTypeList = new ArrayList<String>();
	private List<String> mCategoryList = new ArrayList<String>();
	private List<String> mAreaList = new ArrayList<String>();
	//
	static CacheManager sInstance;

	public MainPageData getMainPageData() {
		return mMainPageData;
	}

	public void setMainPageData(MainPageData data) {
		if (data != null) {
			mMainPageData = data;
		}
	}
	
	public ActivityData getActivity(String id){
		return mActivitys.get(id);
	}
	
	public void setActivity(String id, ActivityData data) {
		if (data != null) {
			mActivitys.put(id, data);
		}
	}
	
	public List<ActivityLiteData> getWeekend() {
		return mWeekEnd;
	}

	public void setWeekend(List<ActivityLiteData> data) {
		if (data != null) {
			mWeekEnd = data;
		}
	}
	
	public List<ActivityLiteData> getFree() {
		return mFree;
	}

	public void setFree(List<ActivityLiteData> data) {
		if (data != null) {
			mFree = data;
		}
	}
	
	public List<ActivityLiteData> getHot() {
		return mHot;
	}

	public void setHot(List<ActivityLiteData> data) {
		if (data != null) {
			mHot = data;
		}
	}
	
	public List<ActivityLiteData> getNear() {
		return mNear;
	}

	public void setNear(List<ActivityLiteData> data) {
		if (data != null) {
			mNear = data;
		}
	}
	
	public List<String> getTypeList() {
		return mTypeList;
	}
	
	public void setTypeList(List<String> data) {
		if (data != null) {
			mTypeList.clear();
			mTypeList.addAll(data);
		}
	}
	
	public List<String> getCategoryList() {
		return mCategoryList;
	}
	
	public void setCategoryList(List<String> data) {
		if (data != null) {
			mCategoryList = data;
		}
	}
	
	public List<String> getAreaList() {
		return mAreaList;
	}
	
	public void setAreaList(List<String> data) {
		if (data != null) {
			mAreaList = data;
		}
	}

	static CacheManager getInstance() {
		if (sInstance == null) {
			sInstance = new CacheManager();
		}

		return sInstance;

	}
}