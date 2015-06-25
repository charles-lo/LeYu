package com.leyu;

import java.io.IOException;
import java.util.Locale;

import com.facebook.drawee.backends.pipeline.Fresco;

import android.app.Activity;
import android.app.Fragment;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	private int m_DeviceWidth;
	private int m_DeviceHeight;
	private boolean mFinishOnBack;
	private Handler mHandler;
	private LocationManager mLocationMgr;
	private LocationListener mLocationListener;
	private Geocoder mGeocoder;
	private Location mLocation;
	private Address mAddress;
	
	static private final String TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fresco.initialize(MainActivity.this);
		setContentView(R.layout.activity_main);

		m_DeviceWidth = getResources().getDisplayMetrics().widthPixels;
		m_DeviceHeight = getResources().getDisplayMetrics().heightPixels;
		mHandler = new Handler();
		
		// location manager
		mLocationMgr = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		mGeocoder = new Geocoder(this, Locale.TAIWAN);
		mLocation = mLocationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		updateAddress();
		//
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PageRecommand()).commit();
		}
	}
	
	@Override
	protected void onResume() {
		// location manager
		if(mLocationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			mLocationListener = new LocationListener(){

				@Override
				public void onLocationChanged(Location location) {
					mLocation = location;
					updateAddress();
				}

				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) {
					Log.d(TAG, "[onStatusChanged] provider: " + provider + " status: " + status);
					
				}

				@Override
				public void onProviderEnabled(String provider) {
					Log.d(TAG, "[onProviderEnabled] provider: " + provider);
					
				}

				@Override
				public void onProviderDisabled(String provider) {
					Log.d(TAG, "[onProviderDisabled] provider: " + provider);
					
				}};
				
				mLocationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 0, mLocationListener);
		}
        
        
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		mLocationMgr.removeUpdates(mLocationListener);
		super.onPause();
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
	
	private void updateAddress() {
		try {
			if (mLocation != null) {
				mAddress = mGeocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1).get(0);
				Log.d(TAG, mAddress.getAdminArea());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Address getAddress() {
		return mAddress;
	}
	
	public void back() {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {

		if (mFinishOnBack || !getFragmentManager().popBackStackImmediate()) {
			finish();
		}

	}

	public void replaceFragment(Fragment newFragment) {
		getFragmentManager().beginTransaction()
				.replace(R.id.container, newFragment).addToBackStack(null)
				.commit();
		mFinishOnBack = false;
	}
	
	public void replaceFragment(Fragment newFragment, boolean finishOnBack) {
		getFragmentManager().beginTransaction()
				.replace(R.id.container, newFragment).addToBackStack(null)
				.commit();
		mFinishOnBack = finishOnBack;
	}

	Handler getHandler(){
		return mHandler;
	}
	
	public int getDeviceWidth() {
		return m_DeviceWidth;
	}

	public int getDeviceHeight() {
		return m_DeviceHeight;
	}
}
