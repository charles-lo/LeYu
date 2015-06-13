package com.leyu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.Gson;
import com.leyu.PageDetail.DetailArgs;
import com.leyu.PageEvent.EventArgs;

import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class PageRecommand extends Fragment {
	static final String TAG = PageRecommand.class.getSimpleName();

	ValueAnimator mDrawerAnimator = null;
	//view
	private Resources mRes;
	private Button mWeekend, mFree, mHot, mNear;
	private ListView mList;
	private int mScrollState;
	private boolean mScroll;
	// Data
	private String mHeadPic = "http://www.sucaifengbao.com/uploadfile/photo/meinvtupianbizhi/meinvtupianbizhi_813_030.jpg";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.page_recommand, container, false);
		mRes = getResources();
		// internet
		final String httpUrl = "http://leibaoserver.azurewebsites.net/api/activity";
		URL url = null;  
        try  
        {  
            //构造一个URL对象  
            url = new URL(httpUrl);   
        }  
        catch (MalformedURLException e)  
        {  
            Log.e(TAG, "MalformedURLException");  
        }
        if (url != null) {
//        	new AsyncTask(){
//
//				@Override
//				protected Object doInBackground(Object... params) {
//					try {
//						JSONArray array = new JSONArray(getJSON(httpUrl, 10000));
//						Log.d(TAG, "");
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					return null;
//				}}.execute();
		}

		//
		((TextView) rootView.findViewById(R.id.left)).setTextColor(mRes.getColor(R.color.red));
		rootView.findViewById(R.id.right).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				((MainActivity) getActivity()).replaceFragment(new PageFind(), true);
				
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

		mList = ((ListView) rootView.findViewById(R.id.list));
		ViewGroup footer = new LinearLayout(PageRecommand.this.getActivity());
		LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				(int) (PageRecommand.this.getResources().getDisplayMetrics().heightPixels)/2);
		footer.setLayoutParams(lp);
		mList.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				mScrollState = scrollState;
				
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int [] loc = new int [2];
				view.getLocationOnScreen(loc);
//				Log.d(TAG, loc[1]  + "  " + loc[0] + "    charles");
				
			}});
		mList.addHeaderView(header);
		mList.addFooterView(footer);
		mList.setAdapter(new LeyuAdapter());


		return rootView;
	}
	
	public String getJSON(String url, int timeout) {
	    HttpURLConnection c = null;
	    try {
	        URL u = new URL(url);
	        c = (HttpURLConnection) u.openConnection();
	        c.setRequestMethod("GET");
	        c.setRequestProperty("Content-length", "0");
	        c.setUseCaches(false);
	        c.setAllowUserInteraction(false);
	        c.setConnectTimeout(timeout);
	        c.setReadTimeout(timeout);
	        c.connect();
	        int status = c.getResponseCode();

	        switch (status) {
	            case 200:
	            case 201:
	                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
	                StringBuilder sb = new StringBuilder();
	                String line;
	                while ((line = br.readLine()) != null) {
	                    sb.append(line+"\n");
	                }
	                br.close();
	                return sb.toString();
	        }

	    } catch (MalformedURLException ex) {
	        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
	    } catch (IOException ex) {
	        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
	    } finally {
	       if (c != null) {
	          try {
	              c.disconnect();
	          } catch (Exception ex) {
	             Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
	          }
	       }
	    }
	    return null;
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
		private int mItemHeight, mMax;

		LeyuAdapter() {
			
			mItemHeight = PageRecommand.this.getActivity().getResources().getDimensionPixelOffset(R.dimen.list_item_height);
			mMax = 3 * mItemHeight;
			
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.listitem_recommand, parent, false);
				holder.mTitle = (TextView) convertView.findViewById(R.id.headline);
				holder.image = (SimpleDraweeView) convertView.findViewById(R.id.headline_value);

				final View root = convertView;
				final View img = holder.image;
				final int factor = 10000;
				convertView.getViewTreeObserver().addOnScrollChangedListener(new OnScrollChangedListener(){

					@Override
					public void onScrollChanged() {
						int [] loc = new int [2];
						root.getLocationOnScreen(loc);
						android.view.ViewGroup.LayoutParams layoutParams = img.getLayoutParams();
						layoutParams.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
						if(loc[1] < mMax &&  loc[1] > mMax/factor){
							layoutParams.height = Math.min(mMax, mItemHeight + (mMax - loc[1])*factor/(factor-1));
						}else if(loc[1] <=  mMax/factor){
							layoutParams.height = mMax;
						}else {
							layoutParams.height = mItemHeight;
						}
						if(!mScroll && mScrollState == OnScrollListener.SCROLL_STATE_IDLE){
							if(loc[1] > -mMax && loc[1] <=0){
								mScroll = true;
								if(loc[1]< -mMax /3){
									mList.smoothScrollToPositionFromTop(mList.getFirstVisiblePosition()+1,0,100);
								}else{
									mList.smoothScrollToPositionFromTop(mList.getFirstVisiblePosition(),0,100);
								}
								new Handler().postDelayed(new Runnable(){
	
									@Override
									public void run() {
										mScroll = false;
										
									}}, 500);
							}
						}
						img.setLayoutParams(layoutParams);
					}});
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			android.view.ViewGroup.LayoutParams layoutParams = holder.image.getLayoutParams();
			layoutParams.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
			layoutParams.height = mMax;
			holder.image.setLayoutParams(layoutParams);

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
			
			convertView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Fragment event = new PageDetail();
					Bundle bundle = new Bundle();
					bundle.putString(PageDetail.ARG, new Gson().toJson(new DetailArgs(m_Data.get(position).mTitle, uri.toString())));
					event.setArguments(bundle);
					((MainActivity) getActivity()).replaceFragment(event);;
					
				}});

			return convertView;
		}

		class ViewHolder {
			TextView mTitle;
			SimpleDraweeView image;
		}
	}
}