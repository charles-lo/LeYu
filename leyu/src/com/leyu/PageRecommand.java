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

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.Gson;
import com.leyu.PageEvent.EventArgs;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AbsListView.OnScrollListener;
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
	// test
	private	double latitude, longitude;
	
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
        	new AsyncTask(){

				@Override
				protected Object doInBackground(Object... params) {
					try {
						JSONArray array = new JSONArray(getJSON(httpUrl, 10000));
						Log.d(TAG, "");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}}.execute();
		}
        
        LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
//        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            if(location != null){
//                latitude = location.getLatitude();
//                longitude = location.getLongitude();
//                Log.d(TAG, "charles case 01 latitude: " + latitude + " longitude: " +longitude );
//                }
//        }else
        {
            LocationListener locationListener = new LocationListener() {
                 
                // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                     
                }
                 
                // Provider被enable时触发此函数，比如GPS被打开
                @Override
                public void onProviderEnabled(String provider) {
                     
                }
                 
                // Provider被disable时触发此函数，比如GPS被关闭 
                @Override
                public void onProviderDisabled(String provider) {
                     
                }
                 
                //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发 
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {   
                        Log.e("Map", "Location changed : Lat: " 
                        + location.getLatitude() + " Lng: " 
                        + location.getLongitude());   
                    }
                }
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000, 0,locationListener);   
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);   
            if(location != null){   
                latitude = location.getLatitude(); //经度   
                longitude = location.getLongitude(); //纬度
                Log.d(TAG, "charles case 02 latitude: " + latitude + " longitude: " +longitude );
            }
            
            location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);   
            if(location != null){   
                latitude = location.getLatitude(); //经度   
                longitude = location.getLongitude(); //纬度
                Log.d(TAG, "charles case 03 latitude: " + latitude + " longitude: " +longitude );
            }   
            
            TelephonyManager telephonyManager = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            GsmCellLocation cellLocation = (GsmCellLocation)telephonyManager.getCellLocation();

            int cid = cellLocation.getCid();
            int lac = cellLocation.getLac();
            Log.d(TAG, "charles case 04 cid: " + cid + " lac: " +lac );
            
        }

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

		final ListView list = ((ListView) rootView.findViewById(R.id.list));
		ViewGroup footer = new LinearLayout(PageRecommand.this.getActivity());
		LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				(int) (PageRecommand.this.getResources().getDisplayMetrics().heightPixels)/3);
		footer.setLayoutParams(lp);
		list.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int [] loc = new int [2];
				view.getLocationOnScreen(loc);
//				Log.d(TAG, loc[1]  + "  " + loc[0] + "    charles");
				
			}});
		list.addHeaderView(header);
		list.addFooterView(footer);
		list.setAdapter(new LeyuAdapter());


		return rootView;
	}
	
	private int getRelativeTop(View myView) {
	    if (myView.getParent() == myView.getRootView())
	        return myView.getTop();
	    else
	        return myView.getTop() + getRelativeTop((View) myView.getParent());
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
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.listitem_recommand, parent, false);
				holder.mTitle = (TextView) convertView.findViewById(R.id.headline);
				holder.image = (SimpleDraweeView) convertView.findViewById(R.id.headline_value);

				final View root = convertView;
				final View img = holder.image;
				convertView.getViewTreeObserver().addOnScrollChangedListener(new OnScrollChangedListener(){

					@Override
					public void onScrollChanged() {
						int [] loc = new int [2];
						root.getLocationOnScreen(loc);
						Log.d(TAG, "charles  A " + loc[0] +"   " +  loc[1]);
						android.view.ViewGroup.LayoutParams layoutParams = img.getLayoutParams();
						layoutParams.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
						if(loc[1] < mMax &&  loc[1] > 0){
							layoutParams.height = Math.min(mMax, mItemHeight + (mMax - loc[1]));
							
//							img.setPadding(100, 0, 0, 0);
						}else if(loc[1] <= 0){
							layoutParams.height = Math.max(0, mMax + loc[1]);
							
						}else if(loc[1] <= -400){
							layoutParams.height = 0;
							
						}else {
							layoutParams.height = mItemHeight;
						}
						img.setLayoutParams(layoutParams);
					}});
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