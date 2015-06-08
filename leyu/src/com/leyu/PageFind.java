package com.leyu;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.leyu.PageEvent.LeyuAdapter.Item;
import com.leyu.PageEvent.LeyuAdapter.ViewHolder;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PageFind extends Fragment {
	static final String TAG = PageFind.class.getSimpleName();
	//
	private Resources mRes;
	private int myYear, myMonth, myDay;
	View mRootView;
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

		final View calendar = mRootView.findViewById(R.id.calendar);
		final TextView calendarText = (TextView) mRootView.findViewById(R.id.calendar_text);
		final Calendar c = Calendar.getInstance();
	    myYear = c.get(Calendar.YEAR);
	    myMonth = c.get(Calendar.MONTH);
	    myDay = c.get(Calendar.DAY_OF_MONTH);
	    final DatePickerDialog dialog = new DatePickerDialog(getActivity(),
	    	      new OnDateSetListener (){

					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						calendarText.setText((monthOfYear+1) + "/" + dayOfMonth + "/" + year);
						
					}},
	    	      myYear, myMonth, myDay);
	    OnClickListener clickCalendar = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.show();
			}
		};
		calendar.setOnClickListener(clickCalendar);
		calendarText.setOnClickListener(clickCalendar);

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
		((TextView) mRootView.findViewById(R.id.find)).setTextColor(mRes.getColor(R.color.red));
		mRootView.findViewById(R.id.recommand).setOnClickListener(new OnClickListener() {

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

		return mRootView;
	}

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
		public View getView(int position, View convertView, ViewGroup parent) {
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