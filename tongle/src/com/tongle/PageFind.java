package com.tongle;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import android.app.AlertDialog;

import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PageFind extends Page {
	//
	private List<ActivityLiteData> m_Data = new ArrayList<ActivityLiteData>();
	// search args
	private String mArea, mDate, mCategory;
	// Calendar
	public GregorianCalendar mMonth, mItemMonth;// calendar instances.
	public CalendarAdapter mAdapter;// adapter instance
	public Handler mHandler;// for grabbing some event values for showing the dot  marker.
	public ArrayList<String> mItems; // container to store calendar items which needs showing the event marker
	private ArrayList<String> mEvent;
	private LinearLayout mLayout;
	private ArrayList<String> mDesc;
	private TextView mCalendarText;
	// Data

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.page_find, container, false);
		mActivity.hideActionBar();
		
		updateTypeList(mCacheManager.getTypeList());
		mGateway.getTypeList(new ListListener() {

			@Override
			public void onComplete(List<String> data) {
				if(!isAdded()){
					return;
				}
				mCacheManager.setTypeList(data);
				updateTypeList(data);
			}

			@Override
			public void onError() {
				final TextView categoryText = (TextView) mRootView.findViewById(R.id.category_text);
				categoryText.setText(R.string.network_error);

			}
		});
		updateTypeList(mCacheManager.getAreaList());
		mGateway.getAreaList(new ListListener() {

			@Override
			public void onComplete(List<String> data) {
				//
				if(!isAdded()){
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
		((TextView) mRootView.findViewById(R.id.center)).setTextColor(mRes.getColor(R.color.red));
		mRootView.findViewById(R.id.left).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				replaceFragment(TAG, new PageRecommand());
			}
		});
		mRootView.findViewById(R.id.right).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				replaceFragment(TAG, new PageMine());
			}
		});
		//
		ListView list = ((ListView) mRootView.findViewById(R.id.list));
		list.setDivider(null);
		ViewGroup footer = new LinearLayout(mActivity);
		LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				(int) (46.67 * PageFind.this.getResources().getDisplayMetrics().density));
		footer.setLayoutParams(lp);
		list.addFooterView(footer);
		list.setAdapter(new LeyuAdapter());
		
		//calendar
		final View calendar = mRootView.findViewById(R.id.calendar);
		final View calendarView = mRootView.findViewById(R.id.calendar_view);
		mCalendarText = (TextView) mRootView.findViewById(R.id.calendar_text);
		
		OnClickListener clickCalendar = new OnClickListener() {

			@Override
			public void onClick(View v) {
				mCalendarText.setText(convertDate(mAdapter.curentDateString));
				calendarView.setVisibility(View.VISIBLE);
			}
		};
		calendar.setOnClickListener(clickCalendar);
		mCalendarText.setOnClickListener(clickCalendar);
		View calendarOK = (TextView) mRootView.findViewById(R.id.btn_ok);
		calendarOK.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				calendarView.setVisibility(View.GONE);
				mDate = mCalendarText.getText().toString();
				update();
			}});
		View calendarCancel = (TextView) mRootView.findViewById(R.id.btn_cancel);
		calendarCancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				calendarView.setVisibility(View.GONE);
				mCalendarText.setText(R.string.alltime);
				mDate = null;
				update();
			}});


		mLayout = (LinearLayout) mRootView.findViewById(R.id.text);
		mMonth = (GregorianCalendar) GregorianCalendar.getInstance();
		mItemMonth = (GregorianCalendar) mMonth.clone();

		mItems = new ArrayList<String>();

		mAdapter = new CalendarAdapter(mActivity, mMonth);

		GridView gridview = (GridView) mRootView.findViewById(R.id.gridview);
		gridview.setAdapter(mAdapter);
		
		mCalendarText.setText(convertDate(mAdapter.curentDateString));
		mDate = mCalendarText.getText().toString();
		update();
		
		mHandler = new Handler();
		mHandler.post(calendarUpdater);

		TextView title = (TextView) mRootView.findViewById(R.id.title);
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
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// removing the previous view if added
				if (((LinearLayout) mLayout).getChildCount() > 0) {
					((LinearLayout) mLayout).removeAllViews();
				}
				mDesc = new ArrayList<String>();
				((CalendarAdapter) parent.getAdapter()).setSelected(v);
				((CalendarAdapter) parent.getAdapter()).setToday();
				String selectedGridDate = CalendarAdapter.dayString.get(position);
				
				mCalendarText.setText(convertDate(selectedGridDate));
				String[] separatedTime = selectedGridDate.split("-");
				String gridvalueString = separatedTime[2].replaceFirst("^0*",
						"");// taking last part of date. ie; 2 from 2012-12-02.
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
			}
		});

		initActionBar("");
		updateTitlebarLeftImg(R.drawable.logo_s);
		return mRootView;
	}
	
	private void updateTypeList(List<String> data) {
		data.add(0, getString(R.string.all_category));
		final View category = mRootView.findViewById(R.id.category);
		final TextView categoryText = (TextView) mRootView.findViewById(R.id.category_text);
		final ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_dropdown_item, data);
		OnClickListener clickCategory = new OnClickListener() {

			@Override
			public void onClick(View v) {
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
						update();
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
	
	private void updateAreaList(final List<String> data) {
		final View regions = mRootView.findViewById(R.id.region);
		final TextView regionsText = (TextView) mRootView.findViewById(R.id.region_text);

		data.add(0, getString(R.string.near));
		data.add(getString(R.string.allplace));
		final ArrayAdapter<String> regionsAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_dropdown_item, data);
		OnClickListener clickRegion = new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(mActivity).setTitle(mRes.getString(R.string.region)).setAdapter(regionsAdapter, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String region = regionsAdapter.getItem(which);
						regionsText.setText(region);
						if (which == 0) {
							mArea = null;
						} else if (which == data.size() - 1) {
							mArea = null;
						} else {
							mArea = region;
						}

						dialog.dismiss();
						update();
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
	
	private void update() {
		// get server data
		Gateway gateway = GatewayImpl.getInstance();
		gateway.searchActivityMoreData(new searchListener() {

			@Override
			public void onComplete(SearchData searchData) {
				if(!isAdded()){
					return;
				}
				List<ActivityLiteData> data = searchData.mActivitys;
				if (data == null || data.size() == 0) {
					m_Data.clear();
					showList(data);
				} else {
					showList(data);
				}
			}

			@Override
			public void onError() {

			}
		},mArea, getLocation(), mDate, mCategory);
	}
	
	private String convertDate(String dateString) {
		String ret = null;
		Date date = null;
		SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat writeFormat = new SimpleDateFormat("yyyy/MM/dd");
		try {
			date = readFormat.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
			return ret;
		}
		return writeFormat.format(date);
	}
	
	private void showList(List<ActivityLiteData> data){
		m_Data = data;
		
		ListView list = ((ListView) mRootView.findViewById(R.id.list));
		list.setDivider(null);
		ViewGroup footer = new LinearLayout(mActivity);
		LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				(int) (46.67 * PageFind.this.getResources().getDisplayMetrics().density));
		footer.setLayoutParams(lp);
		list.addFooterView(footer);
		list.setAdapter(new LeyuAdapter());
	}
	
	protected void setNextMonth() {
		if (mMonth.get(GregorianCalendar.MONTH) == mMonth
				.getActualMaximum(GregorianCalendar.MONTH)) {
			mMonth.set((mMonth.get(GregorianCalendar.YEAR) + 1),
					mMonth.getActualMinimum(GregorianCalendar.MONTH), 1);
		} else {
			mMonth.set(GregorianCalendar.MONTH,
					mMonth.get(GregorianCalendar.MONTH) + 1);
		}

	}
	
	protected void setPreviousMonth() {
		if (mMonth.get(GregorianCalendar.MONTH) == mMonth
				.getActualMinimum(GregorianCalendar.MONTH)) {
			mMonth.set((mMonth.get(GregorianCalendar.YEAR) - 1),
					mMonth.getActualMaximum(GregorianCalendar.MONTH), 1);
		} else {
			mMonth.set(GregorianCalendar.MONTH,
					mMonth.get(GregorianCalendar.MONTH) - 1);
		}

	}
	
	public void refreshCalendar() {
		TextView title = (TextView) mRootView.findViewById(R.id.title);

		mAdapter.refreshDays();
		mAdapter.notifyDataSetChanged();
		mHandler.post(calendarUpdater); // generate some calendar items

		title.setText(android.text.format.DateFormat.format("MMMM yyyy", mMonth));
	}
	
	public Runnable calendarUpdater = new Runnable() {

		@Override
		public void run() {
			mItems.clear();

			// Print dates of the current week
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
			String itemvalue;
			mEvent = CalendarUtility.readCalendarEvent(mActivity);
			Log.d("=====Event====", mEvent.toString());
			Log.d("=====Date ARRAY====", CalendarUtility.startDates.toString());

			for (int i = 0; i < CalendarUtility.startDates.size(); i++) {
				itemvalue = df.format(mItemMonth.getTime());
				mItemMonth.add(GregorianCalendar.DATE, 1);
				mItems.add(CalendarUtility.startDates.get(i).toString());
			}
			mAdapter.setItems(mItems);
			mAdapter.notifyDataSetChanged();
		}
	};
	
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
			// TODO Auto-generated method stub
			return m_Data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (m_Data.get(position)==null){//m_Data.get(position).mType == 1) {
				Holder02 holder = new Holder02();
				if (convertView == null || convertView.getTag() == null
						|| ((Holder) convertView.getTag()).mType != 1) {
					convertView = LayoutInflater.from(mActivity).inflate(
							R.layout.listitem_event_02, parent, false);
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
					title.setText(m_Data.get(position).mTitle);
				}
				String uriBase = "http://www.sucaifengbao.com/uploadfile/photo/meinvtupianbizhi/meinvtupianbizhi_813_";
				DecimalFormat df = new DecimalFormat("'0'.jpg");
				final Uri uri = Uri.parse(uriBase + df.format(position + 20));
				//
				int width, height;
				for (SimpleDraweeView image : holder.images) {
					width = height = (int) (mRes.getDisplayMetrics().density * 115);
					ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
							.setResizeOptions(new ResizeOptions(width, height))
							.setLocalThumbnailPreviewsEnabled(true)
							.setProgressiveRenderingEnabled(true).build();
					DraweeController controller = Fresco.newDraweeControllerBuilder()
							.setImageRequest(request)
							.setOldController(image.getController()).build();
					image.setController(controller);
				}
				convertView.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						Fragment event = new PageDetail();
						Bundle bundle = new Bundle();
						bundle.putString(PageDetail.ARG, new Gson().toJson(new DetailArgs(m_Data.get(position).mID, uri.toString(), m_Data.get(position).mTitle, null)));
						event.setArguments(bundle);
						jumpPage(event, TAG);;
						
					}});
			}
			else{
				Holder01 holder = new Holder01();
				if (convertView == null 
						|| convertView.getTag() == null 
						|| ((Holder) convertView.getTag()).mType !=0) {
					convertView = LayoutInflater.from(mActivity).inflate(R.layout.listitem_event,
							parent, false);
					holder.mTitle = (TextView) convertView.findViewById(R.id.event_title);
					holder.image = (SimpleDraweeView) convertView.findViewById(R.id.event_image);
					holder.mType = 0;

					convertView.setTag(holder);
				} else {
					holder = (Holder01) convertView.getTag();
				}

				holder.mTitle.setText(m_Data.get(position).mTitle);
				final Uri uri = Uri.parse(m_Data.get(position).mPicture);
				//
				int width, height;
				width = height = (int) (mRes.getDisplayMetrics().density * 115);
				ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
						.setResizeOptions(new ResizeOptions(width, height))
						.setLocalThumbnailPreviewsEnabled(true).setProgressiveRenderingEnabled(true)
						.build();
				DraweeController controller = Fresco.newDraweeControllerBuilder()
						.setImageRequest(request).setOldController(holder.image.getController())
						.build();
				holder.image.setController(controller);
				//
				convertView.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						Fragment event = new PageDetail();
						Bundle bundle = new Bundle();
						bundle.putString(PageDetail.ARG, new Gson().toJson(new DetailArgs(m_Data.get(position).mID, uri.toString(), m_Data.get(position).mTitle, null)));
						event.setArguments(bundle);
						jumpPage(event, TAG);;
						
					}});
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