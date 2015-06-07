package com.leyu;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.Gson;
import com.leyu.PageEvent.EventArgs;

import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class PageRecommand extends Fragment {
	static final String TAG = PageRecommand.class.getSimpleName();
	// Cover Drawer
	private int mMin, mMax, mMid, mPreviousY, mDiffY, mPaddingY;
	private boolean isOpened = true, overScroll = true;
	ValueAnimator mDrawerAnimator = null;
	private View mContainer, mButtons;
	//view
	private Resources mRes;
	Button mWeekend, mFree, mHot, mNear;
	// Data
	private String mHeadPic = "http://www.sucaifengbao.com/uploadfile/photo/meinvtupianbizhi/meinvtupianbizhi_813_030.jpg";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.page_recommand, container, false);
		mRes = getResources();

		mContainer = rootView.findViewById(R.id.container);

		mMin = -1 * PageRecommand.this.getResources().getDimensionPixelSize(R.dimen.header_height);
		mContainer.setPadding(0, 0, 0, mMin);
		mMid = mMin / 2;

		mButtons = rootView.findViewById(R.id.buttons);

		//
		((TextView) rootView.findViewById(R.id.recommand)).setTextColor(mRes.getColor(R.color.red));
		rootView.findViewById(R.id.find).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				((MainActivity) getActivity()).replaceFragment(new PageFind());
				
			}});

		int width, height;
		View header = inflater.inflate(R.layout.header_recommand, null);
		mWeekend = (Button) header.findViewById(R.id.weekend);
		mWeekend.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Fragment event = new PageEvent();
				Bundle bundle = new Bundle();
				bundle.putString(PageEvent.ARG, new Gson().toJson(new EventArgs(PageRecommand.this.getActivity().getResources().getString(R.string.weekend))));
				event.setArguments(bundle);
				((MainActivity) getActivity()).replaceFragment(event);
				
			}});
		mFree = (Button) header.findViewById(R.id.free);
		mFree.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Fragment event = new PageEvent();
				Bundle bundle = new Bundle();
				bundle.putString(PageEvent.ARG, new Gson().toJson(new EventArgs(PageRecommand.this.getActivity().getResources().getString(R.string.free))));
				event.setArguments(bundle);
				((MainActivity) getActivity()).replaceFragment(event);;
				
			}});
		mHot = (Button) header.findViewById(R.id.hot);
		mHot.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Fragment event = new PageEvent();
				Bundle bundle = new Bundle();
				bundle.putString(PageEvent.ARG, new Gson().toJson(new EventArgs(PageRecommand.this.getActivity().getResources().getString(R.string.hot))));
				event.setArguments(bundle);
				((MainActivity) getActivity()).replaceFragment(event);
				
			}});
		mNear = (Button) header.findViewById(R.id.near);
		mNear.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Fragment event = new PageEvent();
				Bundle bundle = new Bundle();
				bundle.putString(PageEvent.ARG, new Gson().toJson(new EventArgs(PageRecommand.this.getActivity().getResources().getString(R.string.near))));
				event.setArguments(bundle);
				((MainActivity) getActivity()).replaceFragment(event);
				
			}});

		width = PageRecommand.this.getActivity().getResources().getDisplayMetrics().widthPixels;
		height = (int) (PageRecommand.this.getActivity().getResources().getDisplayMetrics().density * 140);
		ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(mHeadPic))
				.setResizeOptions(new ResizeOptions(width, height))
				.setLocalThumbnailPreviewsEnabled(true).setProgressiveRenderingEnabled(true)
				.build();
		SimpleDraweeView image = ((SimpleDraweeView) header.findViewById(R.id.headline));
		DraweeController controller = Fresco.newDraweeControllerBuilder().setImageRequest(request)
				.setOldController(image.getController()).build();
		image.setController(controller);

		ListView list = ((ListView) rootView.findViewById(R.id.list));
		ViewGroup footer = new LinearLayout(PageRecommand.this.getActivity());
		LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				(int) (PageRecommand.this.getResources().getDisplayMetrics().heightPixels)/3);
		footer.setLayoutParams(lp);
		list.addHeaderView(header);
		list.addFooterView(footer);
		list.setAdapter(new LeyuAdapter());


		return rootView;
	}

	public boolean isDrawerOpened() {
		return isOpened;
	}

	public boolean drawerTouchHandler(MotionEvent event) {
		if (mDrawerAnimator != null && mDrawerAnimator.isStarted()) {
			return true;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mPreviousY = 0;
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (mPreviousY == 0) {
				mDiffY = 0;
				mPreviousY = (int) event.getRawY();
				return true;
			}
			mDiffY = (int) event.getRawY() - mPreviousY;
			mPaddingY += mDiffY;
			if (mPaddingY > mMax) {
				mPaddingY = mMax;
			} else if (mPaddingY < mMin) {
				mPaddingY = mMin;
			}
			if (mDiffY >= 0 && mPaddingY == mMax) {
				overScroll = true;
			} else {
				overScroll = false;
			}
			mPreviousY = (int) event.getRawY();
			mContainer.scrollTo(0, -1 * mPaddingY);
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			finishDrawer();
			return true;
		} else {
			return false;
		}
	}

	private void finishDrawer() {
		if (mPaddingY == mMin) {
			isOpened = false;
		} else if (mPaddingY == mMax) {
			isOpened = true;
		} else {
			if (mPaddingY > mMid) {
				mDrawerAnimator = ValueAnimator.ofInt(mPaddingY, mMax);
			} else if (mPaddingY <= mMid) {
				mDrawerAnimator = ValueAnimator.ofInt(mPaddingY, mMin);
			}
			mDrawerAnimator.setDuration(180);
			mDrawerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				public void onAnimationUpdate(ValueAnimator animation) {
					Integer value = (Integer) animation.getAnimatedValue();
					mPaddingY = value.intValue();
					mContainer.scrollTo(0, -1 * mPaddingY);
					if (mPaddingY == mMin) {
						isOpened = false;
					} else if (mPaddingY == mMax) {
						isOpened = true;
					} else {
					}
				}
			});
			mDrawerAnimator.start();
		}
		mPreviousY = 0;
	}

	class LeyuAdapter extends BaseAdapter {

		private List<Item> m_Data = new ArrayList<Item>();

		class Item {
			String mTitle;
			String mUrl;

			Item(String title, String url) {
				mTitle = title;
				mUrl = url;
			}
		}

		LeyuAdapter() {

			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("手作趣味多", ""));
			m_Data.add(new Item("創意小天才", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("手作趣味多", ""));
			m_Data.add(new Item("創意小天才", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("手作趣味多", ""));
			m_Data.add(new Item("創意小天才", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("手作趣味多", ""));
			m_Data.add(new Item("創意小天才", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("手作趣味多", ""));
			m_Data.add(new Item("創意小天才", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("手作趣味多", ""));
			m_Data.add(new Item("創意小天才", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("手作趣味多", ""));
			m_Data.add(new Item("創意小天才", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("手作趣味多", ""));
			m_Data.add(new Item("創意小天才", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("手作趣味多", ""));
			m_Data.add(new Item("創意小天才", ""));
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
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.listitem_recommand, parent, false);
				holder.mTitle = (TextView) convertView.findViewById(R.id.headline);
				holder.image = (SimpleDraweeView) convertView.findViewById(R.id.headline_value);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.mTitle.setText(m_Data.get(position).mTitle);
			String uriBase = "http://www.sucaifengbao.com/uploadfile/photo/meinvtupianbizhi/meinvtupianbizhi_813_";
			DecimalFormat df = new DecimalFormat("'0'.jpg");
			final Uri uri = Uri.parse(uriBase + df.format(position + 20));
			//
			int width, height;
			width = height = (int) (PageRecommand.this.getActivity().getResources()
					.getDisplayMetrics().density * 115);
			ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
					.setResizeOptions(new ResizeOptions(width, height))
					.setLocalThumbnailPreviewsEnabled(true).setProgressiveRenderingEnabled(true)
					.build();
			DraweeController controller = Fresco.newDraweeControllerBuilder()
					.setImageRequest(request).setOldController(holder.image.getController())
					.build();
			holder.image.setController(controller);

			return convertView;
		}

		class ViewHolder {
			TextView mTitle;
			SimpleDraweeView image;
		}
	}
}