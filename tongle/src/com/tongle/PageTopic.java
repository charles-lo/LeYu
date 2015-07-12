package com.tongle;

import java.util.ArrayList;
import java.util.List;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.Gson;
import com.tongle.Gateway.Content;
import com.tongle.Gateway.Topic;
import com.tongle.Gateway.TopicData;
import com.tongle.Gateway.TopicListener;
import com.tongle.PageDetail.DetailArgs;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class PageTopic extends Page {
	static final String TAG = PageTopic.class.getSimpleName();
	public static final String ARG = "topic_arg";
	// Data
	private List<Content> m_Data = new ArrayList<Content>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final Topic args = new Gson().fromJson((String) getArguments().getString(ARG), Topic.class);
		mRootView = inflater.inflate(R.layout.page_topic, container, false);
		mRes = getResources();
		//
		mActivity = (MainActivity) getActivity();
		mActivity.initActionBar(args.mTitle);

		final TextView status = (TextView) mRootView.findViewById(R.id.status);

		final ListView list = ((ListView) mRootView.findViewById(R.id.list));
		list.setDivider(null);
		ViewGroup footer = new LinearLayout(PageTopic.this.getActivity());
		LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				(int) (46.67 * PageTopic.this.getResources().getDisplayMetrics().density));
		footer.setLayoutParams(lp);
		
		final View header = inflater.inflate(R.layout.listitem_image, null);
		list.addHeaderView(header);
		list.addFooterView(footer);

		Gateway gateway = GatewayImpl.getInstance();
		gateway.getTopic(new TopicListener() {

			@Override
			public void onComplete(TopicData data) {
				status.setVisibility(View.GONE);
				//
				// headline
				int width, height;
				width = getActivity().getResources().getDisplayMetrics().widthPixels;
				height = (int) (mRes.getDisplayMetrics().density * 140);
				ImageRequest request = ImageRequestBuilder
						.newBuilderWithSource(Uri.parse(data.mPicture))
						.setResizeOptions(new ResizeOptions(width, height))
						.setLocalThumbnailPreviewsEnabled(true)
						.setProgressiveRenderingEnabled(true).build();
				SimpleDraweeView image = ((SimpleDraweeView) header
						.findViewById(R.id.event_image));
				DraweeController controller = Fresco.newDraweeControllerBuilder()
						.setImageRequest(request).setOldController(image.getController()).build();
				image.setController(controller);
				//
				m_Data = data.mContents;
				list.setAdapter(new LeyuAdapter());
			}

			@Override
			public void onError() {
				status.setText(R.string.network_error);

			}
		}, args.mID);

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
			final Content data = m_Data.get(position);
			if (data.mType == 0) {
				Holder01 holder = new Holder01();
				if (convertView == null || convertView.getTag() == null
						|| ((Holder) convertView.getTag()).mType != 0) {
					convertView = LayoutInflater.from(getActivity()).inflate(
							R.layout.listitem_text, parent, false);
					holder.mTitle = (TextView) convertView.findViewById(R.id.event_title);
					holder.mType = 0;
					holder.mTitle.setText(data.mText);
					convertView.setTag(holder);
				} else {
					holder = (Holder01) convertView.getTag();
				}

				holder.mTitle.setText(data.mText);
			} else if (data.mType == 1) {
				Holder01 holder = new Holder01();
				if (convertView == null || convertView.getTag() == null
						|| ((Holder) convertView.getTag()).mType != 1) {
					convertView = LayoutInflater.from(getActivity()).inflate(
							R.layout.listitem_image, parent, false);
					holder.image = (SimpleDraweeView) convertView.findViewById(R.id.event_image);
					holder.mType = 1;

					//
					final Uri uri = Uri.parse(data.mPicture);
					int width, height;
					width = height = (int) (mRes.getDisplayMetrics().density * 115);
					ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
							.setResizeOptions(new ResizeOptions(width, height))
							.setLocalThumbnailPreviewsEnabled(true)
							.setProgressiveRenderingEnabled(true).build();
					DraweeController controller = Fresco.newDraweeControllerBuilder()
							.setImageRequest(request)
							.setOldController(holder.image.getController()).build();
					holder.image.setController(controller);
					//
					convertView.setTag(holder);
				} else {
					holder = (Holder01) convertView.getTag();
				}
			} else if (data.mType == 2) {
				Holder01 holder = new Holder01();
				if (convertView == null || convertView.getTag() == null
						|| ((Holder) convertView.getTag()).mType != 2) {
					convertView = LayoutInflater.from(getActivity()).inflate(
							R.layout.listitem_event, parent, false);
					holder.mTitle = (TextView) convertView.findViewById(R.id.event_title);
					holder.image = (SimpleDraweeView) convertView.findViewById(R.id.event_image);
					holder.mType = 2;

					convertView.setTag(holder);
				} else {
					holder = (Holder01) convertView.getTag();
				}

				holder.mTitle.setText(data.mText);

				final Uri uri = Uri.parse(data.mPicture);
				//
				int width, height;
				width = height = (int) (mRes.getDisplayMetrics().density * 115);
				ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
						.setResizeOptions(new ResizeOptions(width, height))
						.setLocalThumbnailPreviewsEnabled(true)
						.setProgressiveRenderingEnabled(true).build();
				DraweeController controller = Fresco.newDraweeControllerBuilder()
						.setImageRequest(request).setOldController(holder.image.getController())
						.build();
				holder.image.setController(controller);
				//

				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Fragment event = new PageDetail();
						Bundle bundle = new Bundle();
						bundle.putString(PageDetail.ARG, new Gson().toJson(new DetailArgs(data.mActivityID, data.mPicture, data.mText, null)));
						event.setArguments(bundle);
						((MainActivity) getActivity()).replaceFragment(event, TAG);
					}
				});
			} else if (data.mType == 3) {
				Holder02 holder = new Holder02();
				if (convertView == null || convertView.getTag() == null
						|| ((Holder) convertView.getTag()).mType != 1) {
					convertView = LayoutInflater.from(getActivity()).inflate(
							R.layout.listitem_event_02, parent, false);
					holder.mTitles.add((TextView) convertView.findViewById(R.id.event_title_01));
					holder.mTitles.add((TextView) convertView.findViewById(R.id.event_title_02));
					holder.images.add((SimpleDraweeView) convertView
							.findViewById(R.id.event_image_01));
					holder.images.add((SimpleDraweeView) convertView
							.findViewById(R.id.event_image_02));
					holder.mType = 1;
					convertView.setTag(holder);
				} else {
					holder = (Holder02) convertView.getTag();
				}
				for (TextView title : holder.mTitles) {
					title.setText(data.mText);
				}
				//
				final Uri uri = Uri.parse(data.mPicture);
				int width, height;
				for (SimpleDraweeView image : holder.images) {
					width = height = (int) (mRes.getDisplayMetrics().density * 115);
					ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
							.setResizeOptions(new ResizeOptions(width, height))
							.setLocalThumbnailPreviewsEnabled(true)
							.setProgressiveRenderingEnabled(true).build();
					DraweeController controller = Fresco.newDraweeControllerBuilder()
							.setImageRequest(request).setOldController(image.getController())
							.build();
					image.setController(controller);
				}
				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Fragment event = new PageDetail();
						Bundle bundle = new Bundle();
						bundle.putString(PageDetail.ARG, new Gson().toJson(new DetailArgs(data.mActivityID, data.mPicture, data.mText, null)));
						event.setArguments(bundle);
						((MainActivity) getActivity()).replaceFragment(event, TAG);
					}
				});

			} else {
				convertView = new View(getActivity());
			}

			return convertView;
		}

		class Holder {
			int mType;
		}

		class Holder01 extends Holder {
			TextView mTitle;
			View mBG;
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