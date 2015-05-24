package com.leyu;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PageHome extends Fragment {
	static private final int MESSAGE_STATE_LAUNCH = 1000;
	static private final int MESSAGE_STATE_BTN1 = 1001;
	static private final int MESSAGE_STATE_BTN2 = 1002;
	static private final int MESSAGE_STATE_BTN3 = 1003;
	static private final int MESSAGE_STATE_BTN4 = 1004;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.page_home, container, false);
		final View title = rootView.findViewById(R.id.title);
		final View notification = rootView.findViewById(R.id.notification);
		final View hot = rootView.findViewById(R.id.hot);
		final View near = rootView.findViewById(R.id.near);
		final View search = rootView.findViewById(R.id.search);
		final View mail = rootView.findViewById(R.id.mail);

		hot.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				((MainActivity) getActivity())
						.replaceFragment(new PageSignin());

			}
		});

		search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				((MainActivity) getActivity())
						.replaceFragment(new PageSearch());

			}
		});

		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MESSAGE_STATE_LAUNCH: {
					title.setVisibility(View.GONE);
					sendEmptyMessageDelayed(MESSAGE_STATE_BTN1, 500);
					break;
				}
				case MESSAGE_STATE_BTN1: {
					notification.setVisibility(View.VISIBLE);
					hot.setVisibility(View.VISIBLE);
					sendEmptyMessageDelayed(MESSAGE_STATE_BTN2, 500);
					break;
				}
				case MESSAGE_STATE_BTN2: {
					near.setVisibility(View.VISIBLE);
					sendEmptyMessageDelayed(MESSAGE_STATE_BTN3, 500);
					break;
				}
				case MESSAGE_STATE_BTN3: {
					search.setVisibility(View.VISIBLE);
					sendEmptyMessageDelayed(MESSAGE_STATE_BTN4, 500);
					break;
				}
				case MESSAGE_STATE_BTN4: {
					mail.setVisibility(View.VISIBLE);
					break;
				}
				}
				super.handleMessage(msg);
			}
		};
		handler.sendEmptyMessageDelayed(MESSAGE_STATE_LAUNCH, 1000);
		return rootView;
	}
}