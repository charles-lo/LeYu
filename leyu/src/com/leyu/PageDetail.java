package com.leyu;

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
import com.leyu.Gateway.ActivityAgeLevelSetting;
import com.leyu.Gateway.ActivityData;
import com.leyu.Gateway.ActivityListener;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
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

public class PageDetail extends Fragment {
	static final String TAG = PageDetail.class.getSimpleName();
	public static final String ARG = "detail_arg";
	// view
	private Resources mRes;
	private BarChart mChart;
	private TextView mLeft, mRight;
	private View mShare;
	private IWXAPI api;
	public static final String APP_ID = "wx6dbf5e76d03452da";
	// Data
	private	List<String> category = new ArrayList<String>(Arrays.asList("美感", "體能", "社交", "科普", "文化"));
	
	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final DetailArgs args = new Gson().fromJson((String) getArguments().getString(ARG), DetailArgs.class);
		mRes = PageDetail.this.getResources();
		//
		api = WXAPIFactory.createWXAPI(getActivity(), APP_ID, false);
		api.registerApp(APP_ID);
		//
		final View rootView = inflater.inflate(R.layout.page_detail, container, false);

		TextView title = ((TextView) rootView.findViewById(R.id.title));
		title.setText(args.mTitle);
		
		mShare = rootView.findViewById(R.id.share);
		mShare.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}});
		mLeft = (TextView) rootView.findViewById(R.id.left);
		mLeft.setText(R.string.collection);
		mLeft.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				View layout = inflater.inflate(R.layout.toast, (ViewGroup) rootView, false);

				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText(R.string.add);

				Toast toast = new Toast(getActivity());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.setView(layout);
				toast.show();
				
			}});
		mRight = (TextView) rootView.findViewById(R.id.right);//
		mRight.setText(R.string.share);
		mRight.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				mShare.setVisibility(View.VISIBLE);
				
			}});
		rootView.findViewById(R.id.close).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				mShare.setVisibility(View.GONE);
				
			}});
		
		rootView.findViewById(R.id.wechat).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				WXTextObject textObj = new WXTextObject();
				textObj.text = "樂育 ";//\n title: " + args.mTitle + "\n  " + args.mUrl;

				WXMediaMessage msg = new WXMediaMessage();
				msg.mediaObject = textObj;
				msg.description = "樂育";

				SendMessageToWX.Req req = new SendMessageToWX.Req();
				req.transaction = "text" + System.currentTimeMillis();
				req.message = msg;
				req.scene = SendMessageToWX.Req.WXSceneSession;
				
				
				api.sendReq(req);
	    
			}});
		
		rootView.findViewById(R.id.mail).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("message/rfc822");
				intent.putExtra(Intent.EXTRA_TEXT, "樂育 \n title: " + args.mTitle + "\n  " + args.mPicture  );

				startActivity(Intent.createChooser(intent, getString(R.string.mail)));
			}});
		
		rootView.findViewById(R.id.message).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);         
				intent.setData(Uri.parse("sms:"));
				intent.putExtra(Intent.EXTRA_TEXT, "樂育 \n title: " + args.mTitle + "\n  " + args.mPicture  );

				startActivity(Intent.createChooser(intent, getString(R.string.message)));
			}});
		
		final View back = rootView.findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((MainActivity) getActivity()).back();

			}
		});
		//
		((TextView) rootView.findViewById(R.id.description)).setText(args.mTitle);
		//
		final TextView status = (TextView)rootView.findViewById(R.id.status);
		Gateway gateway = GatewayImpl.getInstance();
		gateway.getActivity(new ActivityListener(){

			@Override
			public void onComplete(ActivityData data) {
				status.setVisibility(View.GONE);
				// time
				Date beginDate = null, endDate = null;
				SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
				SimpleDateFormat writeFormat = new SimpleDateFormat("yyyy/MMdd-HH:mm");
				try {  
					beginDate = readFormat.parse(data.mBeginDate);
					endDate = readFormat.parse(data.mEndDate);
				} catch (ParseException e) {  
				    // TODO Auto-generated catch block  
				    e.printStackTrace();  
				}
				
				((TextView) rootView.findViewById(R.id.time_value)).setText(writeFormat.format(beginDate) + " ~ " + writeFormat.format(endDate));
				((TextView) rootView.findViewById(R.id.address_value)).setText(data.mPlace + " : " + data.mAddress);
				((TextView) rootView.findViewById(R.id.holder_value)).setText(data.mOrganizer);
				
//				final String eventDescriptionOrigin = "讓小朋友從花園觀察中，了解蝴蝶的生態以及日常活動，也讓小朋友彼此可以有良好的互動，過程中，老師會帶領高年級的小朋友做和蝴蝶進行生死格鬥，從中學會野外求生技能，並了解生存競爭有多殘酷";
				final String eventDescriptionOrigin = data.mDescription;
				if (eventDescriptionOrigin.length() > 60) {
					String eventDescriptionTxt = eventDescriptionOrigin.substring(0, 60);
					final TextView eventDescription = (TextView) rootView.findViewById(R.id.event_description);
					eventDescription.setText(eventDescriptionTxt + "...");

					TextView extend = (TextView) rootView.findViewById(R.id.extend);
					extend.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							eventDescription.setText(eventDescriptionOrigin);
							eventDescription.setLayoutParams(new TableLayout.LayoutParams(
									LinearLayout.LayoutParams.WRAP_CONTENT,
									LinearLayout.LayoutParams.WRAP_CONTENT));
						}
					});
				}else{
					final TextView eventDescription = (TextView) rootView.findViewById(R.id.event_description);
					eventDescription.setText(eventDescriptionOrigin);
					rootView.findViewById(R.id.extend).setVisibility(View.INVISIBLE);
				}
				//
				String age = "";
				for(ActivityAgeLevelSetting item : data.mActivityAgeLevelSettings){
					age += item.mDescription + "  ";
				}
				((TextView) rootView.findViewById(R.id.fitage)).setText(age);
				//
				setData(5, 5, data);
			}

			@Override
			public void onError() {
				status.setText(R.string.network_error);
				
			}}, args.mID);
		//
		SimpleDraweeView image = (SimpleDraweeView) rootView.findViewById(R.id.cover);
		int width, height;
		width = height = (int) (mRes.getDisplayMetrics().density * 115);
		ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(args.mPicture))
				.setResizeOptions(new ResizeOptions(width, height))
				.setLocalThumbnailPreviewsEnabled(true).setProgressiveRenderingEnabled(true)
				.build();
		DraweeController controller = Fresco.newDraweeControllerBuilder()
				.setImageRequest(request).setOldController(image.getController())
				.build();
		image.setController(controller);
		//data
		
		
		((TextView) rootView.findViewById(R.id.price_value)).setText("免費");
		
		((TextView) rootView.findViewById(R.id.price_onsale)).setVisibility(View.INVISIBLE);//.setText("優惠價倒數1天29分");
			
		mChart = (BarChart) rootView.findViewById(R.id.chart1);
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
        xAxis.setTextSize(mRes.getDimensionPixelSize(R.dimen.font_06)/mRes.getDisplayMetrics().density);
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
		ViewGroup footer = new LinearLayout(PageDetail.this.getActivity());
		LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				(int) (46.67 * mRes.getDisplayMetrics().density));
		footer.setLayoutParams(lp);
		
		return rootView;
	}
	
	static public class DetailArgs{
		private	String	mID;
		private	String	mPicture;
		private	String	mTitle;
		private	String	mArea;
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
        BarEntry  bar;
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
        set1.setValueFormatter(new ValueFormatter(){

			@Override
			public String getFormattedValue(float value) {
				return "" + ((int) value);
			}});
        set1.setColor(mRes.getColor(R.color.blue));
        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);

        mChart.setData(data);
        mChart.requestLayout();
    }
}