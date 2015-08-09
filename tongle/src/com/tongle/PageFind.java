package com.tongle;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.Gson;
import com.tongle.Gateway.ActivityLiteData;
import com.tongle.Gateway.ListListener;
import com.tongle.Gateway.SearchData;
import com.tongle.Gateway.searchListener;
import com.tongle.PageDetail.DetailArgs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;

import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnHoverListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HorizontalScrollView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

public class PageFind extends Page {
	//
	private LeyuAdapter mLeyuAdapter;
	// search args
	private String mArea, mDate, mCategory, mType;
	private boolean mNear = true;
	// tab
	private List<View> mTabs = new ArrayList<View>();
	private HorizontalScrollView mTabView;
	private TabHost mTabHost;
	private List<String> mTypes = new ArrayList<String>();
	private LayoutInflater mInflater;
	private boolean mTabInitilized;
	private Runnable mHideTabsRunnable;
	// Calendar
	private View mCalendarView;
	private GregorianCalendar mMonth, mItemMonth;// calendar instances.
	private CalendarAdapter mAdapter;// adapter instance
	private Handler mHandler;// for grabbing some event values for showing the
								// dot marker.
	private ArrayList<String> mItems; // container to store calendar items which
										// needs showing the event marker
	private ArrayList<String> mEvent;
	private LinearLayout mLayout;
	private ArrayList<String> mDesc;
	private TextView mCalendarText;
	private String mDateSelected, mToday, mKeyword;
	private int duration = 300, mOffset;
	//
	private EditText mRightEdit;
	private List<ActivityLiteData> m_Data = new ArrayList<ActivityLiteData>();
	private List<ActivityLiteData> m_DataShow = new ArrayList<ActivityLiteData>();
	// title bar
    private TextView mTitleTextView;
    private View mTitleBar;
    private View mTitleBarLeft;
    private ImageView mTitleBarLeftImg;
    private EditText mTitleBarRightEdit;
	// Data

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = inflater;
		mRootView = inflater.inflate(R.layout.page_find, container, false);		
		
		mActivity.hideActionBar();
		mTabView = (HorizontalScrollView) mRootView.findViewById(R.id.tab);
		mTabView.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				showType();
				return false;
			}});
		mTabHost = (TabHost) mRootView.findViewById(R.id.tabhost);
		mTabHost.setup();

		updateCategoryList(mCacheManager.getCategoryList());
		mGateway.getCategoryList(new ListListener() {

			@Override
			public void onComplete(List<String> data) {
				if (!isAdded()) {
					return;
				}
				mCacheManager.setCategoryList(data);
				updateCategoryList(data);
			}

			@Override
			public void onError() {
				final TextView categoryText = (TextView) mRootView.findViewById(R.id.category_text);
				categoryText.setText(R.string.network_error);

			}
		});

		mGateway.getAreaList(new ListListener() {

			@Override
			public void onComplete(List<String> data) {
				//
				if (!isAdded()) {
					return;
				}
				mCacheManager.setAreaList(data);
				updateAreaList(data);
			}

			@Override
			public void onError() {
				final TextView regionsText = (TextView) mRootView.findViewById(R.id.region_text);
				regionsText.setText(R.string.network_error);

			}
		});
		//
		((TextView) mRootView.findViewById(R.id.footer_center)).setTextColor(mRes.getColor(R.color.red));
		mRootView.findViewById(R.id.footer_left).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switchContent(mActivity.mPageFind, mActivity.mPageRecommand);
			}
		});
		mRootView.findViewById(R.id.footer_right).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switchContent(mActivity.mPageFind, mActivity.mPageMine);
			}
		});
		//
		ListView list = ((ListView) mRootView.findViewById(R.id.list));
		list.setDivider(null);
		ViewGroup footer = new LinearLayout(mActivity);
		LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (46.67 * PageFind.this.getResources().getDisplayMetrics().density));
		footer.setLayoutParams(lp);
		list.addFooterView(footer);
		list.setAdapter(new LeyuAdapter());
		
		
		list.setOnTouchListener(new OnSwipeTouchListener(mActivity) {

			public void onSwipeTop() {
				Log.d(TAG, " onSwipeTop");
			}

			public void onSwipeRight() {
				Log.d(TAG, " onSwipeRight");
			}

			public void onSwipeLeft() {
				Log.d(TAG, " onSwipeLeft");
			}

			public void onSwipeBottom() {
				Log.d(TAG, " onSwipeBottom");
				showType();
			}
		});

		// calendar
		final View calendar = mRootView.findViewById(R.id.calendar);
		mCalendarView = mRootView.findViewById(R.id.calendar_view);
		final GridView gridview = (GridView) mRootView.findViewById(R.id.gridview);

		gridview.setOnTouchListener(new OnSwipeTouchListener(mActivity) {

			public void onSwipeTop() {
				closeCaleandar();
			}

			public void onSwipeRight() {
				setPreviousMonth();
				refreshCalendar();
			}

			public void onSwipeLeft() {
				setNextMonth();
				refreshCalendar();
			}

			public void onSwipeBottom() {
			}
		});
		mCalendarText = (TextView) mRootView.findViewById(R.id.calendar_text);

		OnClickListener clickCalendar = new OnClickListener() {

			@Override
			public void onClick(View v) {
				mOffset = -getDeviceHeight() / 3;
				if (mCalendarView.getVisibility() == View.VISIBLE) {
					closeCaleandar();
				} else {
					openCaleandar();
				}
			}
		};
		calendar.setOnClickListener(clickCalendar);
		mCalendarText.setOnClickListener(clickCalendar);

		mLayout = (LinearLayout) mRootView.findViewById(R.id.text);
		mMonth = (GregorianCalendar) GregorianCalendar.getInstance();
		mItemMonth = (GregorianCalendar) mMonth.clone();

		mItems = new ArrayList<String>();

		mAdapter = new CalendarAdapter(mActivity, mMonth);
		gridview.setAdapter(mAdapter);

		mCalendarText.setText(getString(R.string.today));
		mDateSelected = mToday = mDate = mAdapter.curentDateString;
		update(true);
		TextView title = (TextView) mRootView.findViewById(R.id.calendar_title);
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", mMonth));

		RelativeLayout previous = (RelativeLayout) mRootView.findViewById(R.id.previous);

		previous.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setPreviousMonth();
				refreshCalendar();
			}
		});

		RelativeLayout next = (RelativeLayout) mRootView.findViewById(R.id.next);
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setNextMonth();
				refreshCalendar();

			}
		});

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				// removing the previous view if added
				if (((LinearLayout) mLayout).getChildCount() > 0) {
					((LinearLayout) mLayout).removeAllViews();
				}
				mDesc = new ArrayList<String>();
				((CalendarAdapter) parent.getAdapter()).setSelected(v);
				((CalendarAdapter) parent.getAdapter()).setToday();
				String selectedGridDate = CalendarAdapter.dayString.get(position);
				mDateSelected = selectedGridDate;
				Log.d(TAG, "mToday: " + mToday + " mDateSelected: " + mDateSelected);
				if (mDateSelected.equals(mToday)) {
					mCalendarText.setText(getString(R.string.today));
				} else {
					mCalendarText.setText(selectedGridDate);
				}
				String[] separatedTime = selectedGridDate.split("/");
				String gridvalueString = separatedTime[2].replaceFirst("^0*", "");
				int gridvalue = Integer.parseInt(gridvalueString);
				// navigate to next or previous month on clicking offdays.
				if ((gridvalue > 10) && (position < 8)) {
					setPreviousMonth();
					refreshCalendar();
				} else if ((gridvalue < 7) && (position > 28)) {
					setNextMonth();
					refreshCalendar();
				}
				((CalendarAdapter) parent.getAdapter()).setSelected(v);

				for (int i = 0; i < CalendarUtility.startDates.size(); i++) {
					if (CalendarUtility.startDates.get(i).equals(selectedGridDate)) {
						mDesc.add(CalendarUtility.nameOfEvent.get(i));
					}
				}

				if (mDesc.size() > 0) {
					for (int i = 0; i < mDesc.size(); i++) {
						TextView rowTextView = new TextView(mActivity);

						// set some properties of rowTextView or something
						rowTextView.setText("Event:" + mDesc.get(i));
						rowTextView.setTextColor(Color.BLACK);

						// add the textview to the linearlayout
						mLayout.addView(rowTextView);
					}
				}
				mDesc = null;

				mDate = mDateSelected;
				update(true);
			}
		});
		initTitleBar();
		
		return mRootView;
	}

	private void initTitleBar() {
		mTitleTextView = (TextView) mRootView.findViewById(R.id.title);
		mTitleBarLeftImg =  (ImageView) mRootView.findViewById(R.id.left_img);
		mTitleBarRightEdit = (EditText) mRootView.findViewById(R.id.right_txt);
		mRootView.findViewById(R.id.title).setVisibility(View.GONE);
		
		mTitleBarLeftImg.setImageResource(R.drawable.logo_s);
		
		mRightEdit = (EditText) mRootView.findViewById(R.id.right_txt);
		mRightEdit.setVisibility(View.VISIBLE);
		mRightEdit.setHint(R.string.keyword_find);
		mRightEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mKeyword = s.toString();
				m_DataShow.clear();
				for (ActivityLiteData item : m_Data) {
					if (item.mTitle.contains(mKeyword)) {
						m_DataShow.add(item);
					}
				}
				if (mLeyuAdapter != null) {
					mLeyuAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void updateCategoryList(List<String> data) {
		data.add(0, getString(R.string.all_category));
		final View category = mRootView.findViewById(R.id.category);
		final TextView categoryText = (TextView) mRootView.findViewById(R.id.category_text);
		final ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_dropdown_item, data);
		OnClickListener clickCategory = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCalendarView.getVisibility() == View.VISIBLE) {
					closeCaleandar();
				} else {
				}
				new AlertDialog.Builder(mActivity).setTitle(mRes.getString(R.string.category)).setAdapter(categoryAdapter, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String data = categoryAdapter.getItem(which);
						categoryText.setText(data);
						if (which == 0) {
							mCategory = null;
						} else {
							mCategory = data;
						}
						dialog.dismiss();
						update(true);
					}
				}).create().show();

			}
		};
		category.setOnClickListener(clickCategory);
		categoryText.setOnClickListener(clickCategory);
		String text = categoryAdapter.getItem(0);
		categoryText.setText(text);
		mCategory = null;
	}

	private void showType() {
		if (mTypes.size() == 0) {
			return;
		}

		if (mHideTabsRunnable != null) {
			getHandler().removeCallbacks(mHideTabsRunnable);
		}
		mTabView.setVisibility(View.VISIBLE);
		mHideTabsRunnable = new Runnable() {

			@Override
			public void run() {
				mTabView.setVisibility(View.GONE);
			}
		};
		getHandler().postDelayed(mHideTabsRunnable, 5000);
	}

	private void updateTabs() {
		if (mTypes == null || mTypes.size() == 0) {
			return;
		}
		showType();
		mTabInitilized = false;
		mTabs.clear();
		mTabHost.clearAllTabs();
		int len = mTypes.size();
		View tabIndicator = mInflater.inflate(R.layout.tabwidget, null);
		mTabs.add(tabIndicator);
		final TextView tvTab = (TextView) tabIndicator.findViewById(R.id.tab_title);
		tvTab.setText(getString(R.string.all));
		mTabHost.addTab(mTabHost.newTabSpec("").setIndicator(tabIndicator).setContent(new TabContentFactory() {

			@Override
			public View createTabContent(String tag) {
				TextView tv = new TextView(mActivity);
				tv.setText("The Text of " + tag);
				return tv;
			}
		}));
		for (int i = 0; i < len; i++) {
			tabIndicator = mInflater.inflate(R.layout.tabwidget, null);
			mTabs.add(tabIndicator);
			TextView tvTab1 = (TextView) tabIndicator.findViewById(R.id.tab_title);
			tvTab1.setText(mTypes.get(i));
			mTabHost.addTab(mTabHost.newTabSpec(mTypes.get(i)).setIndicator(tabIndicator).setContent(new TabContentFactory() {

				@Override
				public View createTabContent(String tag) {
					TextView tv = new TextView(mActivity);
					tv.setText("The Text of " + tag);
					return tv;
				}
			}));
		}

		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
					@Override
					public void onTabChanged(String tabId) {

						if (mTabInitilized) {
							mType = tabId;
							showType();
							update(false);
						}
						if (mTabHost.getCurrentTabView().findViewById(R.id.tab_title) != null && TextUtils.isEmpty(((TextView) mTabHost.getCurrentTabView().findViewById(R.id.tab_title)).getText())) {
							return;
						}
						for (View tab : mTabs) {
							if (mTabHost.getCurrentTabView().findViewById(R.id.tab_image) != null) {
								tab.findViewById(R.id.tab_image).setVisibility(View.INVISIBLE);
								((TextView) tab.findViewById(R.id.tab_title)).setTextColor(mRes.getColor(R.color.footer));
							}
						}
						if (mTabHost.getCurrentTabView().findViewById(R.id.tab_image) != null) {
							mTabHost.getCurrentTabView().findViewById(R.id.tab_image).setVisibility(View.VISIBLE);
							((TextView) mTabHost.getCurrentTabView().findViewById(R.id.tab_title)).setTextColor(Color.WHITE);
						}
					}
				});

			}
		});

		for (View tab : mTabs) {
			tab.findViewById(R.id.tab_image).setVisibility(View.INVISIBLE);
			tab.performClick();
		}
		for (View tab : mTabs) {
			if (mTabHost.getCurrentTabView().findViewById(R.id.tab_image) != null) {
				tab.findViewById(R.id.tab_image).setVisibility(View.INVISIBLE);
				((TextView) tab.findViewById(R.id.tab_title)).setTextColor(mRes.getColor(R.color.footer));
			}
		}
		mTabHost.setCurrentTab(0);
		mTabs.get(0).findViewById(R.id.tab_image).setVisibility(View.VISIBLE);
		((TextView) mTabs.get(0).findViewById(R.id.tab_title)).setTextColor(Color.WHITE);
		mTabInitilized = true;
	}

	private void updateAreaList(final List<String> data) {
		final View regions = mRootView.findViewById(R.id.region);
		final TextView regionsText = (TextView) mRootView.findViewById(R.id.region_text);

		data.add(0, getString(R.string.near));
		data.add(getString(R.string.allplace));
		final ArrayAdapter<String> regionsAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_dropdown_item, data);
		OnClickListener clickRegion = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCalendarView.getVisibility() == View.VISIBLE) {
					closeCaleandar();
				} else {
				}
				new AlertDialog.Builder(mActivity).setTitle(mRes.getString(R.string.region)).setAdapter(regionsAdapter, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String region = regionsAdapter.getItem(which);
						regionsText.setText(region);
						if (which == 0) {
							mArea = null;
							mNear = true;
						} else if (which == data.size() - 1) {
							mArea = null;
							mNear = false;
						} else {
							mArea = region;
							mNear = false;
						}

						dialog.dismiss();
						update(true);
					}
				}).create().show();
			}
		};
		regions.setOnClickListener(clickRegion);
		regionsText.setOnClickListener(clickRegion);
		String region = regionsAdapter.getItem(0);
		regionsText.setText(region);
		mArea = null;
	}

	private void closeCaleandar() {
		mCalendarView.animate().translationY(mOffset).alpha(0.0f).setDuration(duration).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mCalendarView.setTranslationY(0);
				mCalendarView.setVisibility(View.GONE);
			}
		});
	}

	private void openCaleandar() {
		mCalendarView.setVisibility(View.VISIBLE);
		mCalendarView.setAlpha(0.0f);
		mCalendarView.setTranslationY(mOffset);
		mCalendarView.animate().translationY(0).alpha(1.0f).setDuration(duration).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mCalendarView.setVisibility(View.VISIBLE);
			}
		});
	}

	private void update(final boolean updateType) {
		if (mRightEdit != null) {
			mRightEdit.setText(null);
		}
		// get server data
		Gateway gateway = GatewayImpl.getInstance();
		gateway.searchActivityMoreData(new searchListener() {

			@Override
			public void onComplete(SearchData searchData) {
				if (!isAdded()) {
					return;
				}
				if (updateType) {
					mTypes.clear();
					mCacheManager.setTypeList(searchData.mTypeList);
					mTypes.addAll(searchData.mTypeList);
					updateTabs();
				}
				
				List<ActivityLiteData> data = searchData.mActivitys;
				if (data == null || data.size() == 0) {
					m_Data.clear();
					m_DataShow.clear();
					showList(data);
				} else {
					showList(data);
				}
			}

			@Override
			public void onError() {

			}
		}, mArea, mNear ? getLocation() : null, mDate, mCategory, mType);
	}

	private void showList(List<ActivityLiteData> data) {
		m_Data = data;
		m_DataShow = new ArrayList<ActivityLiteData>(m_Data);

		ListView list = ((ListView) mRootView.findViewById(R.id.list));
		list.setDivider(null);
		ViewGroup footer = new LinearLayout(mActivity);
		LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (46.67 * PageFind.this.getResources().getDisplayMetrics().density));
		footer.setLayoutParams(lp);
		list.addFooterView(footer);
		mLeyuAdapter = new LeyuAdapter();
		list.setAdapter(mLeyuAdapter);
	}

	protected void setNextMonth() {
		if (mMonth.get(GregorianCalendar.MONTH) == mMonth.getActualMaximum(GregorianCalendar.MONTH)) {
			mMonth.set((mMonth.get(GregorianCalendar.YEAR) + 1), mMonth.getActualMinimum(GregorianCalendar.MONTH), 1);
		} else {
			mMonth.set(GregorianCalendar.MONTH, mMonth.get(GregorianCalendar.MONTH) + 1);
		}

	}

	protected void setPreviousMonth() {
		if (mMonth.get(GregorianCalendar.MONTH) == mMonth.getActualMinimum(GregorianCalendar.MONTH)) {
			mMonth.set((mMonth.get(GregorianCalendar.YEAR) - 1), mMonth.getActualMaximum(GregorianCalendar.MONTH), 1);
		} else {
			mMonth.set(GregorianCalendar.MONTH, mMonth.get(GregorianCalendar.MONTH) - 1);
		}

	}

	public void refreshCalendar() {
		TextView title = (TextView) mRootView.findViewById(R.id.calendar_title);

		mAdapter.refreshDays();
		mAdapter.notifyDataSetChanged();

		title.setText(android.text.format.DateFormat.format("MMMM yyyy", mMonth));
	}

	//

	class LeyuAdapter extends BaseAdapter {

		class Item {
			int mType;
			String mTitle;
			String mUrl;

			Item(int type, String title, String url) {
				mType = type;
				mTitle = title;
				mUrl = url;
			}
		}

		LeyuAdapter() {

		}

		@Override
		public int getCount() {
			return m_DataShow.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (m_DataShow.get(position) == null) {// m_Data.get(position).mType
													// == 1) {
				Holder02 holder = new Holder02();
				if (convertView == null || convertView.getTag() == null || ((Holder) convertView.getTag()).mType != 1) {
					convertView = LayoutInflater.from(mActivity).inflate(R.layout.listitem_event_02, parent, false);
					holder.mTitles.add((TextView) convertView.findViewById(R.id.event_title_01));
					holder.mTitles.add((TextView) convertView.findViewById(R.id.event_title_02));
					holder.images.add((SimpleDraweeView) convertView.findViewById(R.id.event_image_01));
					holder.images.add((SimpleDraweeView) convertView.findViewById(R.id.event_image_02));
					holder.mType = 1;
					convertView.setTag(holder);
				} else {
					holder = (Holder02) convertView.getTag();
				}
				for (TextView title : holder.mTitles) {
					title.setText(m_DataShow.get(position).mTitle);
				}
				String uriBase = "http://www.sucaifengbao.com/uploadfile/photo/meinvtupianbizhi/meinvtupianbizhi_813_";
				DecimalFormat df = new DecimalFormat("'0'.jpg");
				final Uri uri = Uri.parse(uriBase + df.format(position + 20));
				//
				int width, height;
				for (SimpleDraweeView image : holder.images) {
					width = height = (int) (mRes.getDisplayMetrics().density * 115);
					ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri).setResizeOptions(new ResizeOptions(width, height)).setLocalThumbnailPreviewsEnabled(true)
							.setProgressiveRenderingEnabled(true).build();
					DraweeController controller = Fresco.newDraweeControllerBuilder().setImageRequest(request).setOldController(image.getController()).build();
					image.setController(controller);
				}
				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Fragment event = new PageDetail();
						Bundle bundle = new Bundle();
						bundle.putString(PageDetail.ARG, new Gson().toJson(new DetailArgs(m_DataShow.get(position).mID, uri.toString(), m_DataShow.get(position).mTitle, null)));
						event.setArguments(bundle);
						jumpPage(event, TAG);
					}
				});
			} else {
				Holder01 holder = new Holder01();
				if (convertView == null || convertView.getTag() == null || ((Holder) convertView.getTag()).mType != 0) {
					convertView = LayoutInflater.from(mActivity).inflate(R.layout.listitem_event, parent, false);
					holder.mTitle = (TextView) convertView.findViewById(R.id.event_title);
					holder.image = (SimpleDraweeView) convertView.findViewById(R.id.event_image);
					holder.mType = 0;

					convertView.setTag(holder);
				} else {
					holder = (Holder01) convertView.getTag();
				}
				String title = m_DataShow.get(position).mTitle;

				holder.mTitle.setText(title);
				final Uri uri = Uri.parse(m_DataShow.get(position).mPicture);
				//
				int width, height;
				width = height = (int) (mRes.getDisplayMetrics().density * 115);
				ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri).setResizeOptions(new ResizeOptions(width, height)).setLocalThumbnailPreviewsEnabled(true)
						.setProgressiveRenderingEnabled(true).build();
				DraweeController controller = Fresco.newDraweeControllerBuilder().setImageRequest(request).setOldController(holder.image.getController()).build();
				holder.image.setController(controller);
				//
				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Fragment event = new PageDetail();
						Bundle bundle = new Bundle();
						bundle.putString(PageDetail.ARG, new Gson().toJson(new DetailArgs(m_DataShow.get(position).mID, uri.toString(), m_DataShow.get(position).mTitle, null)));
						event.setArguments(bundle);
						jumpPage(event, TAG);
						;

					}
				});
			}

			return convertView;
		}

		class Holder {
			int mType;
		}

		class Holder01 extends Holder {
			TextView mTitle;
			SimpleDraweeView image;
			View root;
		}

		class Holder02 extends Holder {
			List<TextView> mTitles = new ArrayList<TextView>();
			List<SimpleDraweeView> images = new ArrayList<SimpleDraweeView>();
			View root;
		}

	}
}