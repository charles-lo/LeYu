package com.tongle;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PageMain extends Page {

	static final String TAG = PageMain.class.getSimpleName();
	private ViewPager mViewPager;
	private MyFragmentPageAdapter mAdapter;
	// page
	private PageRecommand mPageRecommand = new PageRecommand();
	private PageFind mPageFind = new PageFind();
	private PageMine mPageMine = new PageMine();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.page_main, container, false);
		mViewPager = (ViewPager) mRootView.findViewById(R.id.viewpager);
		mViewPager.setOffscreenPageLimit(3);
		FragmentManager fm = getFragmentManager();
		mAdapter = new MyFragmentPageAdapter(fm);
		mViewPager.setAdapter(mAdapter);

		return mRootView;
	}
	
	@Override
	public void onResume() {
		Log.d(TAG, "PageMain onResume");
		mRootView.requestLayout();
		mRootView.invalidate();
		super.onResume();
	}

	public class MyFragmentPageAdapter extends FragmentPagerAdapter {
		public MyFragmentPageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return mPageRecommand;
			case 1:
				return mPageFind;
			case 2:
				return mPageMine;
			default:
				return null;
			}
		}
	}
}