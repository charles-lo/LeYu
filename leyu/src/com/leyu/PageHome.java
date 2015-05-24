package com.leyu;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class PageHome extends Fragment {
	static final String TAG = PageHome.class.getSimpleName();

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
		return rootView;
	}
}