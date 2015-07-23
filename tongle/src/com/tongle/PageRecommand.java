package com.tongle;

import java.util.ArrayList;
import java.util.List;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.BaseRepeatedPostProcessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.Gson;
import com.tongle.Gateway.MainPageData;
import com.tongle.Gateway.MainPageDataListener;
import com.tongle.Gateway.Topic;
import com.tongle.PageDetail.DetailArgs;
import com.tongle.PageEvent.EventArgs;
import com.tongle.PageEvent.EventArgs.Type;

import android.animation.ValueAnimator;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
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

public class PageRecommand extends Page {
	static final String TAG = PageRecommand.class.getSimpleName();
	
	ValueAnimator mDrawerAnimator = null;
	//view
	private Button mWeekend, mFree, mHot, mNear;
	private int mItemHeight, mMax;
	private ListView mList;
	private int mScrollState;
	private boolean mScroll;
	// Data
	private List<Topic> m_Data = new ArrayList<Topic>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.page_recommand, container, false);
		mItemHeight = mRes.getDimensionPixelOffset(R.dimen.list_item_height);
		mMax = 3 * mItemHeight;
		mActivity.hideActionBar();
		//
		((TextView) mRootView.findViewById(R.id.left)).setTextColor(mRes.getColor(R.color.red));
		mRootView.findViewById(R.id.center).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				replaceFragment(TAG, new PageFind());
				
			}});
		mRootView.findViewById(R.id.right).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				replaceFragment(TAG, new PageMine());
			}
		});

		final View header = inflater.inflate(R.layout.header_recommand, null);
		Bundle accountInfo = getAccountInfo();
		Address address = getAddress();
		String adminArea = null;
		if(address != null){
			adminArea = address.getAdminArea();
		}
		
		if (accountInfo != null) {
			((TextView) header.findViewById(R.id.name)).setText(accountInfo.getString("authAccount") + " 現在在 " + adminArea);
		} else {
			((TextView) header.findViewById(R.id.name)).setText(R.string.not_logon);
			header.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					mActivity.logOn();				
				}});
		}
		mWeekend = (Button) header.findViewById(R.id.weekend);
		mWeekend.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Fragment event = new PageEvent();
				Bundle bundle = new Bundle();
				bundle.putString(PageEvent.ARG, new Gson().toJson(new EventArgs(Type.weekend)));
				event.setArguments(bundle);
				jumpPage(event, TAG);
			}});
		mFree = (Button) header.findViewById(R.id.free);
		mFree.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Fragment event = new PageEvent();
				Bundle bundle = new Bundle();
				bundle.putString(PageEvent.ARG, new Gson().toJson(new EventArgs(Type.free)));
				event.setArguments(bundle);
				jumpPage(event, TAG);
				
			}});
		mHot = (Button) header.findViewById(R.id.hot);
		mHot.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Fragment event = new PageEvent();
				Bundle bundle = new Bundle();
				bundle.putString(PageEvent.ARG, new Gson().toJson(new EventArgs(Type.hot)));
				event.setArguments(bundle);
				jumpPage(event, TAG);
				
			}});
		mNear = (Button) header.findViewById(R.id.near);
		mNear.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Fragment event = new PageEvent();
				Bundle bundle = new Bundle();
				bundle.putString(PageEvent.ARG, new Gson().toJson(new EventArgs(Type.near)));
				event.setArguments(bundle);
				jumpPage(event, TAG);
				
			}});

		

		mList = ((ListView) mRootView.findViewById(R.id.list));
		ViewGroup footer = new LinearLayout(mActivity);
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
				
			}});
		mList.addHeaderView(header);
		mList.addFooterView(footer);

		final TextView status = (TextView)mRootView.findViewById(R.id.status);
		// get server data
		Gateway gateway = GatewayImpl.getInstance();
		
		
		gateway.getMainPageData(new MainPageDataListener() {

			@Override
			public void onComplete(final MainPageData data) {

				// headline
				status.setVisibility(View.GONE);
				if (data.mHeadlines.size() > 0) {
					int width, height;
					width = getDeviceWidth();
					height = (int) (mRes.getDisplayMetrics().density * 140);
					ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(data.mHeadlines.get(0).mPicture)).setResizeOptions(new ResizeOptions(width, height))
							.setLocalThumbnailPreviewsEnabled(true).setProgressiveRenderingEnabled(true).build();
					SimpleDraweeView image = ((SimpleDraweeView) header.findViewById(R.id.headline));
					DraweeController controller = Fresco.newDraweeControllerBuilder().setImageRequest(request).setOldController(image.getController()).build();
					image.setController(controller);

					image.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Fragment event = new PageDetail();
							Bundle bundle = new Bundle();
							bundle.putString(PageDetail.ARG,
									new Gson().toJson(new DetailArgs(data.mHeadlines.get(0).mActivityID, data.mHeadlines.get(0).mPicture, data.mHeadlines.get(0).mTitle, data.mHeadlines.get(0).mArea)));
							event.setArguments(bundle);
							jumpPage(event, TAG);
							;

						}
					});
				}

				// top list
				m_Data = data.mTopList;
				mList.setAdapter(new LeyuAdapter());

			}

			@Override
			public void onError() {
				status.setText(R.string.network_error);

			}
		}, adminArea);
		return mRootView;
	}
	


	class LeyuAdapter extends BaseAdapter {

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
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				convertView = LayoutInflater.from(mActivity).inflate(
						R.layout.listitem_recommand, parent, false);
				holder.mTitle = (TextView) convertView.findViewById(R.id.headline);
				holder.mImage = (SimpleDraweeView) convertView.findViewById(R.id.headline_value);
				holder.mProcessor = new MeshPostprocessor();

				final View root = convertView;
				final View img = holder.mImage;
				final MeshPostprocessor processor = holder.mProcessor;
				final int factor = 10000;
				convertView.getViewTreeObserver().addOnScrollChangedListener(new OnScrollChangedListener(){

					@Override
					public void onScrollChanged() {
						int [] loc = new int [2];
						root.getLocationOnScreen(loc);
						android.view.ViewGroup.LayoutParams layoutParams = img.getLayoutParams();
						layoutParams.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
						if(loc[1] < mMax &&  loc[1] > mMax/factor){
							processor.setBlurRadius(false, 0);
							layoutParams.height = Math.min(mMax, mItemHeight + (mMax - loc[1])*factor/(factor-1));
						}else if(loc[1] <=  mMax/factor){
							layoutParams.height = mMax;
							processor.setBlurRadius(true, Math.min(25, Math.max(5f, ((float)Math.abs(mMax/factor - loc[1])*2)/mMax * 25)));
						}else {
							processor.setBlurRadius(false, 0);
							layoutParams.height = mItemHeight;
						}
						if(!mScroll && mScrollState == OnScrollListener.SCROLL_STATE_IDLE){
							if(loc[1] > -mMax && loc[1] <0){
								mScroll = true;
//								if(loc[1]< -mMax /3){
//									mList.smoothScrollToPositionFromTop(mList.getFirstVisiblePosition()+1,0,100);
//								}else{
//									mList.smoothScrollToPositionFromTop(mList.getFirstVisiblePosition(),0,100);
//								}

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
			android.view.ViewGroup.LayoutParams layoutParams = holder.mImage.getLayoutParams();
			layoutParams.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
			layoutParams.height = mMax;
			holder.mImage.setLayoutParams(layoutParams);

			holder.mTitle.setText(m_Data.get(position).mTitle);
			final Uri uri = Uri.parse(m_Data.get(position).mPicture);
			//
			int width, height;
			width = height = (int) (mRes.getDisplayMetrics().density * 115);
			ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
					.setResizeOptions(new ResizeOptions(width, height))
					.setPostprocessor(holder.mProcessor)
					.setLocalThumbnailPreviewsEnabled(true).setProgressiveRenderingEnabled(true)
					.build();
			DraweeController controller = Fresco.newDraweeControllerBuilder()
					.setImageRequest(request).setOldController(holder.mImage.getController())
					.build();
			holder.mImage.setController(controller);
			
			convertView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Fragment event = new PageTopic();
					Bundle bundle = new Bundle();
					bundle.putString(PageTopic.ARG, new Gson().toJson(m_Data.get(position)));
					event.setArguments(bundle);
					jumpPage(event, PageTopic.TAG);
					
				}});

			return convertView;
		}

		public class MeshPostprocessor extends BaseRepeatedPostProcessor {
			private float mBlurRadius = 5f;
			private boolean mBlur, mLock;
			private RenderScript mRs;
			private Allocation mInput, mOutput;
			private ScriptIntrinsicBlur mScript;

			MeshPostprocessor() {
				super();
				mRs = RenderScript.create(mActivity);
				mScript = ScriptIntrinsicBlur.create(mRs, Element.U8_4(mRs));
			}

			public void setBlurRadius(boolean blur, float blurRadius) {
				if(mLock){
					return;
				}
				if (mBlur == blur && mBlurRadius == blurRadius) {
					return;
				}
				if (blur && (blurRadius > 25 || blurRadius < 5)) {
					return;
				}
				if(blur){
					mLock = true;
				}
				mBlurRadius = blurRadius;
				mBlur = blur;
				update();
			}

			@Override
			public String getName() {
				return "meshPostprocessor";
			}

			@Override
			public void process(Bitmap bitmap) {
				if (mBlur) {
					if (mBlurRadius <= 25 && mBlurRadius >= 5) {
						blur(bitmap);
					}
					mLock = false;
				}
			}

			private void blur(Bitmap src) {
				mInput = Allocation.createFromBitmap(mRs, src,
						Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
				mOutput = Allocation.createTyped(mRs, mInput.getType());
				mScript.setRadius(mBlurRadius);
				mScript.setInput(mInput);
				mScript.forEach(mOutput);
				mOutput.copyTo(src);
			}
		}

		class ViewHolder {
			TextView mTitle;
			SimpleDraweeView mImage;
			MeshPostprocessor mProcessor;
		}
	}
}