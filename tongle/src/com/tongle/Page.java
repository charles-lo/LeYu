package com.tongle;

import android.app.Fragment;
import android.content.res.Resources;
import android.location.Address;
import android.os.Bundle;
import android.view.View;

public class Page extends Fragment {
	//
	protected final String TAG = this.getClass().getSimpleName();
	//
	protected MainActivity mActivity;
	protected Resources mRes;
	protected View mRootView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = (MainActivity) getActivity();
		mRes = getResources();
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

	public int getDeviceHeight() {
		return mActivity.getDeviceHeight();
	}
	
	protected void jumpPage(Fragment newFragment, String tag){
		mActivity.replaceFragment(newFragment, TAG);;
	}
	
	protected void jumpPage(Fragment newFragment, String tag,  boolean finishOnBack){
		mActivity.replaceFragment(newFragment, TAG, finishOnBack);;
	}
}