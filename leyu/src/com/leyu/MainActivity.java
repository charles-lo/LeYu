package com.leyu;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class MainActivity extends Activity {
	private int m_DeviceWidth;
	private int m_DeviceHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fresco.initialize(MainActivity.this);
		setContentView(R.layout.activity_main);

		m_DeviceWidth = getResources().getDisplayMetrics().widthPixels;
		m_DeviceHeight = getResources().getDisplayMetrics().heightPixels;

		new PageRecommand();
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PageRecommand()).commit();
		}
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		return super.onOptionsItemSelected(item);
	}
	
	public void back() {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {

		if (!getFragmentManager().popBackStackImmediate()) {
			finish();
		}

	}

	public void replaceFragment(Fragment newFragment) {
		getFragmentManager().beginTransaction()
				.replace(R.id.container, newFragment).addToBackStack(null)
				.commit();
	}

	public int getDeviceWidth() {
		return m_DeviceWidth;
	}

	public int getDeviceHeight() {
		return m_DeviceHeight;
	}
}
