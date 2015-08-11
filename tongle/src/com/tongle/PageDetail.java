package com.tongle;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.google.gson.Gson;
import com.tongle.Gateway.ActivityAgeLevelSetting;
import com.tongle.Gateway.ActivityData;
import com.tongle.Gateway.ActivityListener;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PageDetail extends Page {
	
	static final String TAG = PageRecommand.class.getSimpleName();
	static public final String ARG = "detail_arg";
	// view
	private BarChart mChart;
	private TextView mBtn01, mBtn02, mBtn03, mBtn04;
	private View mShare;
	private SimpleDraweeView mImage;
	private int coverWidth, coverHeight;
	// Data
	private DetailArgs mArgs;
	private List<String> category;
	private List<ResolveInfo> mShareApps;
	private Intent mFriendsIntent;
	private String mShareLink;
	private ConcurrentLinkedQueue<AsyncTask> mTaskQueue = new ConcurrentLinkedQueue<AsyncTask>();
	// share
	private ResolveInfo mWechat, mWeibo, mFacebook;

	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mArgs = new Gson().fromJson((String) getArguments().getString(ARG), DetailArgs.class);

		mActivity.initActionBar(mArgs.mTitle);
		category = Arrays.asList(mRes.getStringArray(R.array.category));
		//
		initShares();
		//
		mRootView = inflater.inflate(R.layout.page_detail, container, false);

		mShare = mRootView.findViewById(R.id.share);
		mShare.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		mBtn01 = (TextView) mRootView.findViewById(R.id.btn01);
		mBtn01.setText(R.string.collection);
		mBtn01.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				View layout = inflater.inflate(R.layout.toast, (ViewGroup) mRootView, false);

				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText(R.string.add);

				Toast toast = new Toast(mActivity);
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.setView(layout);
				toast.show();

			}
		});
		mBtn02 = (TextView) mRootView.findViewById(R.id.btn02);
		mBtn02.setText(R.string.recommand);
		
		mBtn03 = (TextView) mRootView.findViewById(R.id.btn03);//
		mBtn03.setText(R.string.share);
		mBtn03.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				mShare.setVisibility(View.VISIBLE);

			}
		});
		
		mBtn04 = (TextView) mRootView.findViewById(R.id.btn04);
		mBtn04.setText(R.string.footprint);
		mRootView.findViewById(R.id.close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mShare.setVisibility(View.GONE);

			}
		});

		mRootView.findViewById(R.id.wechat).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initShares();
				if (mWechat == null) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.wechat.com"));
					startActivity(browserIntent);
				} else {
					Intent intent = new Intent();
					intent.setComponent(new ComponentName(mWechat.activityInfo.packageName, mWechat.activityInfo.name));
					intent.setAction(Intent.ACTION_SEND);
					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + " \n title: " + mArgs.mTitle + "\n  " + mShareLink);
					intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
					intent.setPackage(mWechat.activityInfo.packageName);
					startActivity(intent);
				}
			}
		});

		mRootView.findViewById(R.id.weibo).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initShares();
				if (mWeibo == null) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.weibo.cn"));
					startActivity(browserIntent);
				} else {
					Intent intent = new Intent();
					intent.setComponent(new ComponentName(mWeibo.activityInfo.packageName, mWeibo.activityInfo.name));
					intent.setAction(Intent.ACTION_SEND);
					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + " \n title: " + mArgs.mTitle + "\n  " + mShareLink);
					intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
					intent.setPackage(mWeibo.activityInfo.packageName);
					startActivity(intent);
				}
			}
		});

		mRootView.findViewById(R.id.facebook).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initShares();
				if (mFacebook == null) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/mobile"));
					startActivity(browserIntent);
				} else {
					Intent intent = new Intent();
					intent.setComponent(new ComponentName(mFacebook.activityInfo.packageName, mFacebook.activityInfo.name));
					intent.setAction(Intent.ACTION_SEND);
					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + " \n title: " + mArgs.mTitle + "\n  " + mShareLink);
					intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
					intent.setPackage(mFacebook.activityInfo.packageName);
					startActivity(intent);
				}
			}
		});
		
		mRootView.findViewById(R.id.wechat_friends).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mFriendsIntent != null) {
					startActivity(mFriendsIntent);
				} else {
					new AsyncTask<Void, Void, Intent>() {

						@Override
						protected void onPostExecute(Intent result) {
							startActivity(result);
						}

						@Override
						protected Intent doInBackground(Void... params) {
							mFriendsIntent = getFriendsIntent();
							return mFriendsIntent;
						}
					}.execute();
				}
			}
		});
		
		mRootView.findViewById(R.id.mail).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("message/rfc822");
				intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + " \n title: " + mArgs.mTitle + "\n  " + mShareLink);

				startActivity(Intent.createChooser(intent, getString(R.string.mail)));
			}
		});

		mRootView.findViewById(R.id.message).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("sms:"));
				intent.putExtra(Intent.EXTRA_TEXT, "樂育 \n title: " + mArgs.mTitle + "\n  " + mShareLink);

				startActivity(Intent.createChooser(intent, getString(R.string.message)));
			}
		});

		//
		((TextView) mRootView.findViewById(R.id.description)).setText(mArgs.mTitle);
		//
		mStatus = (TextView) mRootView.findViewById(R.id.status);
		
		mGateway.getActivityDetail(new ActivityListener() {

			@Override
			public void onComplete(ActivityData data) {
				if (!isAdded()) {
					return;
				}
				mCacheManager.setActivity(mArgs.mID, data);
				updateActivity(data, true);
			}

			@Override
			public void onError() {
				mStatus.setText(R.string.server_error);

			}
		}, mArgs.mID);
		mGateway.userActionActivity(mArgs.mID);
		//
		mImage = (SimpleDraweeView) mRootView.findViewById(R.id.cover);
		coverWidth = coverHeight = (int) (mRes.getDisplayMetrics().density * 115);
		if (mArgs.mPicture != null) {
			ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(mArgs.mPicture)).setResizeOptions(new ResizeOptions(coverWidth, coverHeight)).setLocalThumbnailPreviewsEnabled(true)
					.setProgressiveRenderingEnabled(true).build();
			DraweeController controller = Fresco.newDraweeControllerBuilder().setImageRequest(request).setOldController(mImage.getController()).build();
			mImage.setController(controller);
		}
		//
		mTaskQueue.add(new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				mFriendsIntent = getFriendsIntent();
				return null;
			}
		}.execute());
		// data

		((TextView) mRootView.findViewById(R.id.price_onsale)).setVisibility(View.INVISIBLE);

		mChart = (BarChart) mRootView.findViewById(R.id.chart1);
		mChart.setDrawBarShadow(false);
		mChart.setDrawValueAboveBar(true);
		mChart.setDrawValuesForWholeStack(false);
		mChart.setClickable(false);
		mChart.setLongClickable(false);
		mChart.setDoubleTapToZoomEnabled(false);
		mChart.setTouchEnabled(false);

		mChart.setDescription("");
		mChart.setMaxVisibleValueCount(5);
		mChart.setPinchZoom(false);
		mChart.setDrawGridBackground(false);

		XAxis xAxis = mChart.getXAxis();
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setDrawGridLines(false);
		xAxis.setSpaceBetweenLabels(2);
		xAxis.setTextSize(mRes.getDimensionPixelSize(R.dimen.font_06) / mRes.getDisplayMetrics().density);
		xAxis.setTextColor(mRes.getColor(R.color.grey));

		YAxis leftAxis = mChart.getAxisLeft();
		leftAxis.setLabelCount(4);
		leftAxis.setPosition(YAxisLabelPosition.INSIDE_CHART);
		leftAxis.setSpaceTop(15f);
		leftAxis.setTextColor(Color.TRANSPARENT);

		YAxis rightAxis = mChart.getAxisRight();
		rightAxis.setEnabled(false);

		Legend l = mChart.getLegend();
		l.setEnabled(false);
		// footer
		ViewGroup footer = new LinearLayout(mActivity);
		LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (46.67 * mRes.getDisplayMetrics().density));
		footer.setLayoutParams(lp);
		

		updateActivity(mCacheManager.getActivity(mArgs.mID), true);

		return mRootView;
	}
	
	private void initShares() {
		if (mShareApps != null) {
			return;
		}
		mShareApps = mActivity.getShareList();

		if (mShareApps != null) {
			for (ResolveInfo info : mShareApps) {
				if (info.activityInfo.packageName.contains("com.tencent.mm") && info.activityInfo.name.contains("com.tencent.mm.ui.tools.ShareImgUI")) {
					mWechat = info;
				}
				if (info.activityInfo.packageName.contains("com.sina.weibo")) {
					mWeibo = info;
				}
				if (info.activityInfo.packageName.contains("com.facebook.katana")) {
					mFacebook = info;
				}
			}
		}
	}
	
	private Intent getFriendsIntent(){
		Intent ret = null;
		if (!isAdded()) {
			return ret;
		}
		try {
			URL url = new URL(mArgs.mPicture);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = calculateInSampleSize(options, coverWidth, coverHeight);
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			
			Bitmap immutableBpm = BitmapFactory.decodeStream(input, null, options);
			if(immutableBpm == null){
				return null;
			}
			Bitmap mutableBitmap = immutableBpm.copy(Bitmap.Config.RGB_565, true);

			String path = Images.Media.insertImage(mActivity.getContentResolver(), mutableBitmap, "", null);
			Uri uri = Uri.parse(path);
			initShares();
			if (mWechat == null) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.wechat.com"));
				ret = browserIntent;
			} else {
				Intent intent = new Intent();
				intent.setComponent(new ComponentName(mWechat.activityInfo.packageName, "com.tencent.mm.ui.tools.ShareToTimeLineUI"));
				intent.setAction(Intent.ACTION_SEND);
				intent.setType("image/*");
				intent.putExtra(Intent.EXTRA_STREAM, uri);
				intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + " \n title: " + mArgs.mTitle + "\n  " + mArgs.mPicture);
				intent.setPackage(mWechat.activityInfo.packageName);
				ret = intent;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
    }

    return inSampleSize;
}
	
	private void updateActivity(ActivityData data, boolean hideStatus) {
		if (data == null) {
			return;
		}
		
		if (hideStatus) {
			mStatus.setVisibility(View.GONE);
		}
		
		mActivity.initActionBar(data.mTitle);
		if (data.mPicture != null) {
			ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(data.mPicture)).setResizeOptions(new ResizeOptions(coverWidth, coverHeight)).setLocalThumbnailPreviewsEnabled(true)
					.setProgressiveRenderingEnabled(true).build();
			DraweeController controller = Fresco.newDraweeControllerBuilder().setImageRequest(request).setOldController(mImage.getController()).build();
			mImage.setController(controller);
		}
		// time
		Date beginDate = null, endDate = null;
		SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ");
		SimpleDateFormat writeFormat = new SimpleDateFormat("yyyy/MMdd-HH:mm");
		if (data.mBeginDate != null && data.mEndDate != null) {
			try {
				beginDate = readFormat.parse(data.mBeginDate);
				endDate = readFormat.parse(data.mEndDate);
			} catch (ParseException e) {
				e.printStackTrace();
				mStatus.setVisibility(View.VISIBLE);
				mStatus.setText(R.string.server_error);
				return;
			}
		}
		
		mShareLink = data.mShareUri;

		((TextView) mRootView.findViewById(R.id.time_value)).setText(writeFormat.format(beginDate) + " ~ " + writeFormat.format(endDate));
		((TextView) mRootView.findViewById(R.id.address_value)).setText(data.mAddress);
		mRootView.findViewById(R.id.holder).setVisibility(View.INVISIBLE);
		((TextView) mRootView.findViewById(R.id.holder_value)).setText("");
		((TextView) mRootView.findViewById(R.id.price_value)).setText(data.mPrice);
		((TextView) mRootView.findViewById(R.id.tel_value)).setText(data.mTel);
		((TextView) mRootView.findViewById(R.id.website_value)).setText(data.mWebSite);

		final String eventDescriptionOrigin = data.mDescription;
		if (eventDescriptionOrigin.length() > 60) {
			String eventDescriptionTxt = eventDescriptionOrigin.substring(0, 60);
			final TextView eventDescription = (TextView) mRootView.findViewById(R.id.event_description);
			eventDescription.setText(eventDescriptionTxt + "...");

			TextView extend = (TextView) mRootView.findViewById(R.id.extend);
			extend.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					eventDescription.setText(eventDescriptionOrigin);
					eventDescription.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				}
			});
		} else {
			final TextView eventDescription = (TextView) mRootView.findViewById(R.id.event_description);
			eventDescription.setText(eventDescriptionOrigin);
			mRootView.findViewById(R.id.extend).setVisibility(View.INVISIBLE);
		}
		//
		String age = "";
		for (ActivityAgeLevelSetting item : data.mActivityAgeLevelSettings) {
			age += item.mDescription + "  ";
		}
		((TextView) mRootView.findViewById(R.id.fitage)).setText(age);
		//
		setData(5, 5, data);
	}

	static public class DetailArgs {
		private String mID;
		private String mPicture;
		private String mTitle;
		private String mArea;

		DetailArgs(String id, String picture, String title, String area) {
			mID = id;
			mTitle = title;
			mPicture = picture;
			mArea = area;
		}
	}

	private void setData(int count, float range, ActivityData serverData) {

		ArrayList<String> xVals = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			xVals.add(category.get(i));
		}

		ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
		BarEntry bar;
		float val = (float) serverData.mAesthetic;
		bar = new BarEntry(val, 0);
		bar.setXIndex(1);
		yVals1.add(new BarEntry(val, 0));

		val = (float) serverData.mPhysical;
		bar = new BarEntry(val, 1);
		bar.setXIndex(1);
		yVals1.add(new BarEntry(val, 1));

		val = (float) serverData.mSocially;
		bar = new BarEntry(val, 2);
		bar.setXIndex(1);
		yVals1.add(new BarEntry(val, 2));

		val = (float) serverData.mScience;
		bar = new BarEntry(val, 3);
		bar.setXIndex(1);
		yVals1.add(new BarEntry(val, 3));

		val = (float) serverData.mCulture;
		bar = new BarEntry(val, 4);
		bar.setXIndex(1);
		yVals1.add(new BarEntry(val, 4));

		BarDataSet set1 = new BarDataSet(yVals1, "");
		set1.setBarSpacePercent(50);
		set1.setValueFormatter(new ValueFormatter() {

			@Override
			public String getFormattedValue(float value) {
				return "" + ((int) value);
			}
		});
		set1.setColor(mRes.getColor(R.color.blue));
		ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
		dataSets.add(set1);

		BarData data = new BarData(xVals, dataSets);
		data.setValueTextSize(10f);

		mChart.setData(data);
		mChart.requestLayout();
	}
	
	@Override
	public void onDetach() {
		if (mTaskQueue.size() > 0) {
			mTaskQueue.poll().cancel(true);
		}
		super.onDetach();
	}
}