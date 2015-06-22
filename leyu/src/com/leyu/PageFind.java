package com.leyu;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.Gson;
import com.leyu.PageDetail.DetailArgs;

import android.app.AlertDialog;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

public class PageFind extends Fragment {
	static final String TAG = PageFind.class.getSimpleName();
	//
	private Resources mRes;
	View mRootView;
	// Calendar
	public GregorianCalendar mMonth, mItemMonth;// calendar instances.
	public CalendarAdapter mAdapter;// adapter instance
	public Handler mHandler;// for grabbing some event values for showing the dot  marker.
	public ArrayList<String> mItems; // container to store calendar items which needs showing the event marker
	private ArrayList<String> mEvent;
	private LinearLayout mLayout;
	private ArrayList<String> desc;
	private TextView calendarText;
	// Data

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.page_find, container, false);
		mRes = getResources();
		//
		final View regions = mRootView.findViewById(R.id.region);
		final TextView regionsText = (TextView) mRootView.findViewById(R.id.region_text);
		final ArrayAdapter<String> regionsAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item, mRes.getStringArray(R.array.regions));
		OnClickListener clickRegion = new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(getActivity()).setTitle(mRes.getString(R.string.region))
						.setAdapter(regionsAdapter, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

								regionsText.setText(regionsAdapter.getItem(which));
								dialog.dismiss();
							}
						}).create().show();
				}
		};
		regions.setOnClickListener(clickRegion);
		regionsText.setOnClickListener(clickRegion);



		final View category = mRootView.findViewById(R.id.category);
		final TextView categoryText = (TextView) mRootView.findViewById(R.id.category_text);
		final ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item, Arrays.asList("類別01", "類別02",
						"類別03", "類別04", "類別05", "類別06"));
		OnClickListener clickCategory = new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(getActivity()).setTitle(mRes.getString(R.string.category))
						.setAdapter(categoryAdapter, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

								categoryText.setText(categoryAdapter.getItem(which));
								dialog.dismiss();
							}
						}).create().show();

			}
		};
		category.setOnClickListener(clickCategory);
		categoryText.setOnClickListener(clickCategory);
		//
		((TextView) mRootView.findViewById(R.id.right)).setTextColor(mRes.getColor(R.color.red));
		mRootView.findViewById(R.id.left).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((MainActivity) getActivity()).replaceFragment(new PageRecommand(), true);

			}
		});

		//
		ListView list = ((ListView) mRootView.findViewById(R.id.list));
		list.setDivider(null);
		ViewGroup footer = new LinearLayout(PageFind.this.getActivity());
		LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				(int) (46.67 * PageFind.this.getResources().getDisplayMetrics().density));
		footer.setLayoutParams(lp);
		list.addFooterView(footer);
		list.setAdapter(new LeyuAdapter());
		
		//calendar
		final View calendar = mRootView.findViewById(R.id.calendar);
		final View calendarView = mRootView.findViewById(R.id.calendar_view);
		calendarText = (TextView) mRootView.findViewById(R.id.calendar_text);
		
	    OnClickListener clickCalendar = new OnClickListener() {

			@Override
			public void onClick(View v) {
				calendarText.setText(mAdapter.curentDateString);
				calendarView.setVisibility(View.VISIBLE);
			}
		};
		calendar.setOnClickListener(clickCalendar);
		calendarText.setOnClickListener(clickCalendar);
		View calendarOK = (TextView) mRootView.findViewById(R.id.btn_ok);
		calendarOK.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				calendarView.setVisibility(View.GONE);
				
			}});
		

		mLayout = (LinearLayout) mRootView.findViewById(R.id.text);
		mMonth = (GregorianCalendar) GregorianCalendar.getInstance();
		mItemMonth = (GregorianCalendar) mMonth.clone();

		mItems = new ArrayList<String>();

		mAdapter = new CalendarAdapter(getActivity(), mMonth);

		GridView gridview = (GridView) mRootView.findViewById(R.id.gridview);
		gridview.setAdapter(mAdapter);

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
				desc = new ArrayList<String>();
				((CalendarAdapter) parent.getAdapter()).setSelected(v);
				String selectedGridDate = CalendarAdapter.dayString.get(position);
				
				calendarText.setText(selectedGridDate);
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

				for (int i = 0; i < Utility.startDates.size(); i++) {
					if (Utility.startDates.get(i).equals(selectedGridDate)) {
						desc.add(Utility.nameOfEvent.get(i));
					}
				}

				if (desc.size() > 0) {
					for (int i = 0; i < desc.size(); i++) {
						TextView rowTextView = new TextView(getActivity());

						// set some properties of rowTextView or something
						rowTextView.setText("Event:" + desc.get(i));
						rowTextView.setTextColor(Color.BLACK);

						// add the textview to the linearlayout
						mLayout.addView(rowTextView);

					}

				}

				desc = null;

			}

		});

		return mRootView;
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
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String itemvalue;
			mEvent = Utility.readCalendarEvent(getActivity());
			Log.d("=====Event====", mEvent.toString());
			Log.d("=====Date ARRAY====", Utility.startDates.toString());

			for (int i = 0; i < Utility.startDates.size(); i++) {
				itemvalue = df.format(mItemMonth.getTime());
				mItemMonth.add(GregorianCalendar.DATE, 1);
				mItems.add(Utility.startDates.get(i).toString());
			}
			mAdapter.setItems(mItems);
			mAdapter.notifyDataSetChanged();
		}
	};
	
	//

	class LeyuAdapter extends BaseAdapter {
		
		private List<Item> m_Data = new ArrayList<Item>();

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

			m_Data.add(new Item(1, "其妙大自然", ""));
			m_Data.add(new Item(0, "手作趣味多", ""));
			m_Data.add(new Item(0, "創意小天才", ""));
			m_Data.add(new Item(0, "其妙大自然", ""));
			m_Data.add(new Item(0, "手作趣味多", ""));
			m_Data.add(new Item(0, "創意小天才", ""));
			m_Data.add(new Item(0, "其妙大自然", ""));
			m_Data.add(new Item(0, "手作趣味多", ""));
			m_Data.add(new Item(0, "創意小天才", ""));
			m_Data.add(new Item(0, "其妙大自然", ""));
			m_Data.add(new Item(1, "手作趣味多", ""));
			m_Data.add(new Item(0, "創意小天才", ""));
			m_Data.add(new Item(0, "其妙大自然", ""));
			m_Data.add(new Item(0, "手作趣味多", ""));
			m_Data.add(new Item(0, "創意小天才", ""));
			m_Data.add(new Item(0, "其妙大自然", ""));
			m_Data.add(new Item(0, "手作趣味多", ""));
			m_Data.add(new Item(0, "創意小天才", ""));
			m_Data.add(new Item(0, "其妙大自然", ""));
			m_Data.add(new Item(0, "手作趣味多", ""));
			m_Data.add(new Item(0, "創意小天才", ""));
			m_Data.add(new Item(0, "其妙大自然", ""));
			m_Data.add(new Item(0, "手作趣味多", ""));
			m_Data.add(new Item(0, "創意小天才", ""));
			m_Data.add(new Item(0, "其妙大自然", ""));
			m_Data.add(new Item(0, "手作趣味多", ""));
			m_Data.add(new Item(1, "創意小天才", ""));
			
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
			if (m_Data.get(position).mType == 1) {
				Holder02 holder = new Holder02();
				if (convertView == null || convertView.getTag() == null
						|| ((Holder) convertView.getTag()).mType != 1) {
					convertView = LayoutInflater.from(getActivity()).inflate(
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
						bundle.putString(PageDetail.ARG, new Gson().toJson(new DetailArgs(m_Data.get(position).mTitle, uri.toString())));
						event.setArguments(bundle);
						((MainActivity) getActivity()).replaceFragment(event);;
						
					}});
			}
			else{
				Holder01 holder = new Holder01();
				if (convertView == null 
						|| convertView.getTag() == null 
						|| ((Holder) convertView.getTag()).mType !=0) {
					convertView = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_event,
							parent, false);
					holder.mTitle = (TextView) convertView.findViewById(R.id.event_title);
					holder.image = (SimpleDraweeView) convertView.findViewById(R.id.event_image);
					holder.mType = 0;

					convertView.setTag(holder);
				} else {
					holder = (Holder01) convertView.getTag();
				}

				holder.mTitle.setText(m_Data.get(position).mTitle);
				String uriBase = "http://www.sucaifengbao.com/uploadfile/photo/meinvtupianbizhi/meinvtupianbizhi_813_";
				DecimalFormat df = new DecimalFormat("'0'.jpg");
				final Uri uri = Uri.parse(uriBase + df.format(position + 20));
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
						bundle.putString(PageDetail.ARG, new Gson().toJson(new DetailArgs(m_Data.get(position).mTitle, uri.toString())));
						event.setArguments(bundle);
						((MainActivity) getActivity()).replaceFragment(event);;
						
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