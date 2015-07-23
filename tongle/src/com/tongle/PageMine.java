package com.tongle;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

public class PageMine extends Page {
	static final String TAG = PageMine.class.getSimpleName();

	//

	// Data

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.page_mine, container, false);
		mRes = getResources();
		//
		mActivity = (MainActivity) getActivity();
		mActivity.initActionBar(getString(R.string.my_baby));
		//
		final TextView name = (TextView) mRootView.findViewById(R.id.name);
		name.setText("冠成");

		final TabHost tabHost = (TabHost) mRootView.findViewById(R.id.tabhost);
		tabHost.setup();

		final Context context = mActivity;
		((TextView) mRootView.findViewById(R.id.right)).setTextColor(mRes.getColor(R.color.red));
		mRootView.findViewById(R.id.left).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				replaceFragment(TAG, new PageRecommand());
			}
		});
		mRootView.findViewById(R.id.center).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				replaceFragment(TAG, new PageFind());
			}
		});
		//
		final ViewPager pager = (ViewPager) mRootView.findViewById(R.id.viewpager);// ViewPager

		List<View> listViews = new ArrayList<View>();
		listViews.add(new View(context));
		listViews.add(new View(context));
		listViews.add(new View(context));

		RelativeLayout tabIndicator1 = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.tab_widget, null);

		TextView tvTab1 = (TextView) tabIndicator1.findViewById(R.id.tv_title);
		tvTab1.setText(R.string.friend);

		RelativeLayout tabIndicator2 = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.tab_widget, null);
		TextView tvTab2 = (TextView) tabIndicator2.findViewById(R.id.tv_title);
		tvTab2.setText(R.string.footprint);

		RelativeLayout tabIndicator3 = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.tab_widget, null);
		TextView tvTab3 = (TextView) tabIndicator3.findViewById(R.id.tv_title);
		tvTab3.setText(R.string.collection);

		tabHost.addTab(tabHost.newTabSpec("A").setIndicator(tabIndicator1).setContent(new TabContentFactory() {

			@Override
			public View createTabContent(String tag) {
				TextView tv = new TextView(context);
				tv.setText("The Text of " + tag);
				return tv;
			}
		}));
		tabHost.addTab(tabHost.newTabSpec("B").setIndicator(tabIndicator2).setContent(new TabContentFactory() {

			@Override
			public View createTabContent(String tag) {
				TextView tv = new TextView(context);
				tv.setText("The Text of " + tag);
				return tv;
			}
		}));
		tabHost.addTab(tabHost.newTabSpec("C").setIndicator(tabIndicator3).setContent(new TabContentFactory() {

			@Override
			public View createTabContent(String tag) {
				TextView tv = new TextView(context);
				tv.setText("The Text of " + tag);
				return tv;
			}
		}));

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				tabHost.setOnTabChangedListener(new OnTabChangeListener() {
					@Override
					public void onTabChanged(String tabId) {
						if ("A".equals(tabId)) {
							pager.setCurrentItem(0);// 在tabhost的监听改变Viewpager
						}
						if ("B".equals(tabId)) {
							pager.setCurrentItem(1);
						}
						if ("C".equals(tabId)) {
							pager.setCurrentItem(2);
						}
					}
				});

			}
		});

		// 为ViewPager适配和设置监听
		pager.setAdapter(new MyPageAdapter(listViews));
		pager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				tabHost.setCurrentTab(position);// 在Viewpager改变tabhost
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		//
		final TextView status = (TextView) mRootView.findViewById(R.id.status);

		Gateway gateway = GatewayImpl.getInstance();

		return mRootView;
	}

	private class MyPageAdapter extends PagerAdapter {

		private List<View> list;

		private MyPageAdapter(List<View> list) {
			this.list = list;
		}

		@Override
		public void destroyItem(ViewGroup view, int position, Object arg2) {
			ViewPager pViewPager = ((ViewPager) view);
			pViewPager.removeView(list.get(position));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			ViewPager pViewPager = ((ViewPager) view);
			pViewPager.addView(list.get(position));
			return list.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}
}