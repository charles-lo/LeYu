package com.tongle;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.Gson;
import com.tongle.PageDetail.DetailArgs;

import android.R.color;
import android.app.Fragment;
import android.app.LocalActivityManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class PageEvent extends Fragment {
	static final String TAG = PageEvent.class.getSimpleName();
	public static final String ARG = "event_arg";
	// view
	private LinearLayout mTabBar;
	private int mPreious;
	private Resources mRes;
	private	List<View> tabs = new ArrayList<View>();
	private TabHost tabHost;
	private ListView list;
	private LeyuAdapter mAdapter;
	// Data
	private	List<String> tabName = new ArrayList<String>(Arrays.asList("海邊", "牧場", "果園", "展覽", "展覽", "", ""));
	private List<Integer> tabInt = new ArrayList<Integer>(Arrays.asList(20, 30, 40, 50, 60, 70));
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		EventArgs args = new Gson().fromJson((String) getArguments().getString(ARG), EventArgs.class);
		mRes = PageEvent.this.getResources();
		mAdapter = new LeyuAdapter();
		
		View rootView = inflater.inflate(R.layout.page_event, container, false);

		TextView title = ((TextView) rootView.findViewById(R.id.title));
		title.setText(args.mTitle);
		
		final View back = rootView.findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((MainActivity) getActivity()).back();

			}
		});
		
		//
		list = ((ListView) rootView.findViewById(R.id.list));
		list.setDivider(null);
		ViewGroup footer = new LinearLayout(PageEvent.this.getActivity());
		LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				(int) (46.67 * mRes.getDisplayMetrics().density));
		footer.setLayoutParams(lp);
		list.addFooterView(footer);
		list.setAdapter(new LeyuAdapter());
		//


		tabHost = (TabHost) rootView.findViewById(R.id.tabhost);
		tabHost.setup();
		tabHost.setup(new LocalActivityManager(this.getActivity(), true));
		for (int i = 0; i < tabName.size(); i++) {
			View tabIndicator = inflater.inflate(R.layout.tabwidget, null);
			tabs.add(tabIndicator);
			TextView tvTab1 = (TextView) tabIndicator.findViewById(R.id.tab_title);
			tvTab1.setText(tabName.get(i));
			tabHost.addTab(tabHost.newTabSpec("" + i).setIndicator(tabIndicator)
					.setContent(R.id.temp));
		}

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				tabHost.setOnTabChangedListener(new OnTabChangeListener() {
					@Override
					public void onTabChanged(String tabId) {
						if(tabHost.getCurrentTabView().findViewById(R.id.tab_title) !=null && TextUtils.isEmpty(((TextView) tabHost.getCurrentTabView().findViewById(R.id.tab_title)).getText())){
							return;
						}
						for (View tab : tabs) {
							if (tabHost.getCurrentTabView().findViewById(R.id.tab_image) != null) {
								tab.findViewById(R.id.tab_image).setVisibility(View.INVISIBLE);
								((TextView) tab.findViewById(R.id.tab_title)).setTextColor(mRes
										.getColor(R.color.footer));
							}
						}
						if (tabHost.getCurrentTabView().findViewById(R.id.tab_image) != null) {
							tabHost.getCurrentTabView().findViewById(R.id.tab_image)
									.setVisibility(View.VISIBLE);
							((TextView) tabHost.getCurrentTabView()
									.findViewById(R.id.tab_title)).setTextColor(Color.WHITE);
							list.setAdapter(null);
							mAdapter.notifyDataSetChanged();
							list.setAdapter(new LeyuAdapter());
						}

					}
				});

			}
		});
		for(View tab : tabs){
			tab.findViewById(R.id.tab_image).setVisibility(View.INVISIBLE);
			tab.performClick();
		}
		for (View tab : tabs) {
			if (tabHost.getCurrentTabView().findViewById(R.id.tab_image) != null) {
				tab.findViewById(R.id.tab_image).setVisibility(View.INVISIBLE);
				((TextView) tab.findViewById(R.id.tab_title)).setTextColor(mRes
						.getColor(R.color.footer));
			}
		}
		tabHost.setCurrentTab(0);
		tabs.get(0).findViewById(R.id.tab_image).setVisibility(View.VISIBLE);
		((TextView)tabs.get(0).findViewById(R.id.tab_title)).setTextColor(Color.WHITE);

		return rootView;
	}
	
	static public class EventArgs{
		private	String	mTitle;
		EventArgs(String title) {
			mTitle = title;
		}
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
			m_Data.add(new Item("只有天知道", ""));
			m_Data.add(new Item("八仙爆炸超酷", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("只有天知道", ""));
			m_Data.add(new Item("八仙爆炸超酷", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("只有天知道", ""));
			m_Data.add(new Item("八仙爆炸超酷", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("只有天知道", ""));
			m_Data.add(new Item("八仙爆炸超酷", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("只有天知道", ""));
			m_Data.add(new Item("八仙爆炸超酷", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("只有天知道", ""));
			m_Data.add(new Item("八仙爆炸超酷", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("只有天知道", ""));
			m_Data.add(new Item("八仙爆炸超酷", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("只有天知道", ""));
			m_Data.add(new Item("八仙爆炸超酷", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("只有天知道", ""));
			m_Data.add(new Item("八仙爆炸超酷", ""));
			m_Data.add(new Item("其妙大自然", ""));
			m_Data.add(new Item("只有天知道", ""));
			m_Data.add(new Item("八仙爆炸超酷", ""));
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
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_event,
						parent, false);
				holder.mTitle = (TextView) convertView.findViewById(R.id.event_title);
				holder.image = (SimpleDraweeView) convertView.findViewById(R.id.event_image);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.mTitle.setText(m_Data.get(position).mTitle);
			String uriBase = "http://www.sucaifengbao.com/uploadfile/photo/meinvtupianbizhi/meinvtupianbizhi_813_";
			DecimalFormat df = new DecimalFormat("'0'.jpg");
			final Uri uri = Uri.parse(uriBase + df.format(position + tabInt.get(tabHost.getCurrentTab())));
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
			
			convertView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Fragment event = new PageDetail();
					Bundle bundle = new Bundle();
					bundle.putString(PageDetail.ARG, new Gson().toJson(new DetailArgs(null, uri.toString(), m_Data.get(position).mTitle, null)));
					event.setArguments(bundle);
					((MainActivity) getActivity()).replaceFragment(event, PageDetail.TAG);;
					
				}});

			return convertView;
		}

		class ViewHolder {
			TextView mTitle;
			SimpleDraweeView image;
		}
	}
}