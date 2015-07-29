package com.tongle;

import android.app.Fragment;
import android.content.res.Resources;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Page extends Fragment {
	//
	protected final String TAG = this.getClass().getSimpleName();
	//
	protected MainActivity mActivity;
	protected Resources mRes;
	protected View mRootView;
	protected Gateway mGateway;
	protected CacheManager mCacheManager;
	protected TextView mStatus;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = (MainActivity) getActivity();
		mRes = getResources();
		mCacheManager = CacheManager.getInstance();
		mGateway = GatewayImpl.getInstance();
	}
	
	protected Bundle getAccountInfo(){
		return mActivity.getAccountInfo();
	}
	
	public Address getAddress() {
		return mActivity.getAddress();
	}
	
	public int getDeviceWidth() {
		return mActivity.getDeviceWidth();
	}
	
	public Location getLocation() {
		return mActivity.getLocation();
	}

	public int getDeviceHeight() {
		return mActivity.getDeviceHeight();
	}
	
	public void initActionBar(String title){
		mActivity.initActionBar(title);
	}
	
	public void hideActionBar() {
		mActivity.hideActionBar();
	}
	
	public void updateTitlebarLeftImg(int id) {
		mActivity.updateTitlebarLeftImg(id);
	}
	
	public EditText getTitleBarRightEdit(){
		return mActivity.getTitleBarRightEdit();
	}
	
	protected void replaceFragment(String newTag, Fragment newFrg) {
		mActivity.replaceFragment(newTag, newFrg);
	}

	protected void jumpPage(Fragment newFragment, String tag) {
		mActivity.jumpFragment(newFragment, TAG);
	}

	protected void jumpPage(Fragment newFragment, String tag, boolean finishOnBack) {
		mActivity.jumpFragment(newFragment, TAG, finishOnBack);
	}
}