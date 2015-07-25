package com.tongle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tongle.Gateway.ActivityAgeLevelSetting;
import com.tongle.Gateway.ActivityData;
import com.tongle.Gateway.ActivityListener;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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

	public static final String ARG = "detail_arg";
	// view
	private BarChart mChart;
	private TextView mBtn01, mBtn02, mBtn03, mBtn04;
	private View mShare;
	private IWXAPI api;
	public static final String APP_ID = "wx6dbf5e76d03452da";
	// Data
	private List<String> category;

	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final DetailArgs args = new Gson().fromJson((String) getArguments().getString(ARG), DetailArgs.class);

		category = Arrays.asList(mRes.getStringArray(R.array.category));
		//
		mActivity.initActionBar(args.mTitle);

		api = WXAPIFactory.createWXAPI(mActivity, APP_ID, false);
		api.registerApp(APP_ID);
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
				WXTextObject textObj = new WXTextObject();
				textObj.text = "樂育 ";// \n title: " + args.mTitle + "\n " +
										// args.mUrl;

				WXMediaMessage msg = new WXMediaMessage();
				msg.mediaObject = textObj;
				msg.description = "樂育";

				SendMessageToWX.Req req = new SendMessageToWX.Req();
				req.transaction = "text" + System.currentTimeMillis();
				req.message = msg;
				req.scene = SendMessageToWX.Req.WXSceneSession;

				api.sendReq(req);

			}
		});

		mRootView.findViewById(R.id.mail).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("message/rfc822");
				intent.putExtra(Intent.EXTRA_TEXT, "樂育 \n title: " + args.mTitle + "\n  " + args.mPicture);

				startActivity(Intent.createChooser(intent, getString(R.string.mail)));
			}
		});

		mRootView.findViewById(R.id.message).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("sms:"));
				intent.putExtra(Intent.EXTRA_TEXT, "樂育 \n title: " + args.mTitle + "\n  " + args.mPicture);

				startActivity(Intent.createChooser(intent, getString(R.string.message)));
			}
		});

		//
		((TextView) mRootView.findViewById(R.id.description)).setText(args.mTitle);
		//
		mStatus = (TextView) mRootView.findViewById(R.id.status);
		
		mGateway.getActivity(new ActivityListener() {

			@Override
			public void onComplete(ActivityData data) {
				mCacheManager.setActivity(args.mID, data);
				updateActivity(data, true);
			}

			@Override
			public void onError() {
				mStatus.setText(R.string.server_error);

			}
		}, args.mID);
		mGateway.userActionActivity(args.mID);
		//
		SimpleDraweeView image = (SimpleDraweeView) mRootView.findViewById(R.id.cover);
		int width, height;
		width = height = (int) (mRes.getDisplayMetrics().density * 115);
		ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(args.mPicture)).setResizeOptions(new ResizeOptions(width, height)).setLocalThumbnailPreviewsEnabled(true)
				.setProgressiveRenderingEnabled(true).build();
		DraweeController controller = Fresco.newDraweeControllerBuilder().setImageRequest(request).setOldController(image.getController()).build();
		image.setController(controller);
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
		

		updateActivity(mCacheManager.getActivity(args.mID), true);

		return mRootView;
	}
	
	private void updateActivity(ActivityData data, boolean hideStatus) {
		if (data == null) {
			return;
		}
		
		if (hideStatus) {
			mStatus.setVisibility(View.GONE);
		}
		// time
		Date beginDate = null, endDate = null;
		SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ");
		SimpleDateFormat writeFormat = new SimpleDateFormat("yyyy/MMdd-HH:mm");
		try {
			beginDate = readFormat.parse(data.mBeginDate);
			endDate = readFormat.parse(data.mEndDate);
		} catch (ParseException e) {
			e.printStackTrace();
			mStatus.setVisibility(View.VISIBLE);
			mStatus.setText(R.string.server_error);
			return;
		}

		((TextView) mRootView.findViewById(R.id.time_value)).setText(writeFormat.format(beginDate) + " ~ " + writeFormat.format(endDate));
		((TextView) mRootView.findViewById(R.id.address_value)).setText(data.mAddress);
		mRootView.findViewById(R.id.holder).setVisibility(View.INVISIBLE);
		((TextView) mRootView.findViewById(R.id.holder_value)).setText("");
		((TextView) mRootView.findViewById(R.id.price_value)).setText(data.mPrice);

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
}