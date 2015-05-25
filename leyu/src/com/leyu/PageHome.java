package com.leyu;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class PageHome extends Fragment {
	static final String TAG = PageHome.class.getSimpleName();
	private String[] list = { "河童", "殭屍", "吸血鬼", "妖精", "天狗" };
	private ArrayAdapter<String> listAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.page_home, container, false);
		TextView headline = (TextView) rootView.findViewById(R.id.headline);
		headline.setText("冠成知道蝴蝶\n是怎麼生小寶寶的嗎?");
		Button favorable = (Button) rootView.findViewById(R.id.favorable);
		favorable.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG, "");

			}
		});

		TextView listTitle = (TextView) rootView.findViewById(R.id.list_title);
		listTitle.setText("奇妙的大自然");
		ListView lv = (ListView) rootView.findViewById(R.id.listView);
		// lv.addHeaderView(textView, null , false);
		listAdapter = new ArrayAdapter<String>(getActivity(),
				R.layout.listitem_01, R.id.listTextView, list);
		lv.setAdapter(listAdapter);
		return rootView;
	}
}