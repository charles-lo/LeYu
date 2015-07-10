package com.tongle;

import java.io.IOException;
import java.util.Locale;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.tongle.accounts.AccountGeneral;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.OnAccountsUpdateListener;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

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
	private AccountManager mAccountManager;
    private Bundle mAccountInfo; 
    // Action Bar
    private ActionBar mActionBar;
    private TextView mTitleTextView;
	
	static private final String TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fresco.initialize(MainActivity.this);
		setContentView(R.layout.activity_main);
		// action bar
		mActionBar = getActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);

		mActionBar.setCustomView(R.layout.title_bar);
		View customView = mActionBar.getCustomView();
		mTitleTextView = (TextView) customView.findViewById(R.id.title);
		final View back = customView.findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();

			}
		});
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.hide();
		//
		mAccountManager = AccountManager.get(this);
		
        logOn(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);

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
					.add(R.id.container, new PageRecommand(), PageRecommand.TAG).commit();
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
	
	public Bundle getAccountInfo(){
		return mAccountInfo;
	}
	
	public void initActionBar(String title){
		mActionBar.show();
		mTitleTextView.setText(title);
	}
	
	private void logOn(String accountType, final String authTokenType) {
		// regenerate token
		invalidateAuthToken();
		// get token
		final AccountManagerFuture<Bundle> future = mAccountManager.getAuthTokenByFeatures(accountType, authTokenType, null, this, null, null,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {

                        try {
                        	mAccountInfo = future.getResult();
                            final String authtoken = mAccountInfo.getString(AccountManager.KEY_AUTHTOKEN);
                            Log.d(TAG, "GetTokenForAccount Bundle is " + mAccountInfo + " authtoken: " + authtoken);
                            
                            GatewayImpl.getInstance().initialize(authtoken, MainActivity.this);
                            //
                            Fragment frg = null;
							frg = getFragmentManager().findFragmentByTag(PageRecommand.TAG);
							final FragmentTransaction ft = getFragmentManager().beginTransaction();
							ft.detach(frg);
							ft.attach(frg);
							ft.commitAllowingStateLoss();
							//
							mAccountManager.addOnAccountsUpdatedListener(new OnAccountsUpdateListener() {

								@Override
								public void onAccountsUpdated(Account[] accounts) {
									if (mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE).length == 0) {
										finish();
									}
								}
							}, null, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                , null);
	}

	public void invalidateAuthToken() {
		// regenerate token
		final Account availableAccounts[] = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

		if (availableAccounts.length == 0) {

		} else {
			invalidateAuthToken(availableAccounts[0], AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
		}
	}
	
	   /**
     * Invalidates the auth token for the account
     * @param account
     * @param authTokenType
     */
    private void invalidateAuthToken(final Account account, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, this, null,null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle bnd = future.getResult();

                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    mAccountManager.invalidateAuthToken(account.type, authtoken);
                    
                    GatewayImpl.getInstance().initialize(authtoken, MainActivity.this);
                    Log.d(TAG, account.name + " invalidated");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, e.getMessage());
                }
            }
        }).start();
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

	@Override
	public void onBackPressed() {
		mActionBar.hide();
		if (mFinishOnBack || !getFragmentManager().popBackStackImmediate()) {
			finish();
		}

	}

	public void replaceFragment(Fragment newFragment, String tag) {
		getFragmentManager().beginTransaction()
				.replace(R.id.container, newFragment, tag).addToBackStack(null)
				.commit();
		mFinishOnBack = false;
	}
	
	public void replaceFragment(Fragment newFragment, String tag, boolean finishOnBack) {
		getFragmentManager().beginTransaction()
				.replace(R.id.container, newFragment, tag).addToBackStack(null)
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
