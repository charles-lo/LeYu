package com.leyu;

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
import com.leyu.Gateway.MainPageDataListener;
import com.leyu.Gateway.MainPageData;
import com.leyu.Gateway.Topic;
import com.leyu.PageDetail.DetailArgs;
import com.leyu.PageEvent.EventArgs;
import com.udinic.accounts_authenticator_example.authentication.AccountGeneral;

import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.res.Resources;
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

public class PageRecommand extends Fragment {
	static final String TAG = PageRecommand.class.getSimpleName();

	ValueAnimator mDrawerAnimator = null;
	//view
	private Resources mRes;
	private Button mWeekend, mFree, mHot, mNear;
	private int mItemHeight, mMax;
	private ListView mList;
	private int mScrollState;
	private boolean mScroll;
	// Data
	private List<Topic> m_Data = new ArrayList<Topic>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.page_recommand, container, false);
		mRes = getResources();
		mItemHeight = mRes.getDimensionPixelOffset(R.dimen.list_item_height);
		mMax = 3 * mItemHeight;

		//
		((TextView) rootView.findViewById(R.id.left)).setTextColor(mRes.getColor(R.color.red));
		rootView.findViewById(R.id.right).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				((MainActivity) getActivity()).replaceFragment(new PageFind(), true);
				
			}});


		final View header = inflater.inflate(R.layout.header_recommand, null);
		mWeekend = (Button) header.findViewById(R.id.weekend);
		mWeekend.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
//				Fragment event = new PageEvent();
//				Bundle bundle = new Bundle();
//				bundle.putString(PageEvent.ARG, new Gson().toJson(new EventArgs(PageRecommand.this.getActivity().getResources().getString(R.string.weekend))));
//				event.setArguments(bundle);
				((MainActivity) getActivity()).addNewAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);;
				
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
				
			}});
		mList.addHeaderView(header);
		mList.addFooterView(footer);

		final TextView status = (TextView)rootView.findViewById(R.id.status);
		// get server data
		Gateway gateway = GatewayImpl.getInstance();
		
		Address address = ((MainActivity)getActivity()).getAddress();
		String adminArea = null;
		if(address != null){
			adminArea = address.getAdminArea();
		}
		
        gateway.getMainPageData(new MainPageDataListener(){

			@Override
			public void onComplete(final MainPageData data) {
				status.setVisibility(View.GONE);
				// headline
				int width, height;
				width = PageRecommand.this.getActivity().getResources().getDisplayMetrics().widthPixels;
				height = (int) (PageRecommand.this.getActivity().getResources().getDisplayMetrics().density * 140);
				ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(data.mHeadlines.get(0).mPicture))
						.setResizeOptions(new ResizeOptions(width, height))
						.setLocalThumbnailPreviewsEnabled(true).setProgressiveRenderingEnabled(true)
						.build();
				SimpleDraweeView image = ((SimpleDraweeView) header.findViewById(R.id.headline));
				DraweeController controller = Fresco.newDraweeControllerBuilder().setImageRequest(request)
						.setOldController(image.getController()).build();
				image.setController(controller);
				
				image.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						Fragment event = new PageDetail();
						Bundle bundle = new Bundle();
						bundle.putString(PageDetail.ARG, new Gson().toJson(new DetailArgs(data.mHeadlines.get(0).mActivityID, data.mHeadlines.get(0).mPicture, data.mHeadlines.get(0).mTitle, data.mHeadlines.get(0).mArea)));
						event.setArguments(bundle);
						((MainActivity) getActivity()).replaceFragment(event);;
						
					}});
				// top list
				m_Data = data.mTopList;
				mList.setAdapter(new LeyuAdapter());
				
			}

			@Override
			public void onError() {
				status.setText(R.string.network_error);
				
			}}, adminArea);
		return rootView;
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
				convertView = LayoutInflater.from(getActivity()).inflate(
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
			width = height = (int) (PageRecommand.this.getActivity().getResources()
					.getDisplayMetrics().density * 115);
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
					((MainActivity) getActivity()).replaceFragment(event);;
					
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
				mRs = RenderScript.create(getActivity());
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