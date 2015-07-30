package com.tongle;

import java.io.IOException;
import java.util.List;
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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private int m_DeviceWidth;
	private int m_DeviceHeight;
	private boolean mFinishOnBack;
	private Handler mHandler;
	private LocationManager mLocationMgr;
	private LocationListener mLocationListener;
	// Data
	private Geocoder mGeocoder;
	private Location mLocation;
	private Address mAddress;
	private String mIMEI;
	private AccountManager mAccountManager;
    private Bundle mAccountInfo; 
    private List<ResolveInfo> mShareApps;
    // Action Bar
    private ActionBar mActionBar;
    private TextView mTitleTextView;
    private View mTitleBar;
    private View mTitleBarLeft;
    private ImageView mTitleBarLeftImg;
    private EditText mTitleBarRightEdit;
	
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
		mTitleBar = mActionBar.getCustomView();
		
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.hide();
		//
		mAccountManager = AccountManager.get(this);
		
		mIMEI = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
		
        logOn(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);

		m_DeviceWidth = getResources().getDisplayMetrics().widthPixels;
		m_DeviceHeight = getResources().getDisplayMetrics().heightPixels;
		mHandler = new Handler();
		
		new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				PackageManager pm = getPackageManager();
			    Intent sendIntent = new Intent(Intent.ACTION_SEND);     
			    sendIntent.setType("text/plain");
			    mShareApps = pm.queryIntentActivities(sendIntent, 0);
				return null;
			}}.execute();

		// location manager
		mLocationMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
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
		GatewayImpl.getInstance().userActionLeave();
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
	
	public List<ResolveInfo> getShareList(){
		return mShareApps;
	}
	
	public void initActionBar(String title){
		mTitleTextView = (TextView) mTitleBar.findViewById(R.id.title);
		mTitleBarLeftImg =  (ImageView) mTitleBar.findViewById(R.id.left_img);
		mTitleBarLeftImg.setImageResource(R.drawable.back);
		mTitleBarLeft = mTitleBar.findViewById(R.id.back);
		mTitleBarLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();

			}
		});
		mTitleBarRightEdit = (EditText) mTitleBar.findViewById(R.id.right_txt);
		mTitleBarRightEdit.setVisibility(View.GONE);
		mActionBar.show();
		mTitleTextView.setText(title);
	}
	
	public void hideActionBar() {
		mActionBar.hide();
	}
	
	public EditText getTitleBarRightEdit(){
		return mTitleBarRightEdit;
	}
	
	public void updateTitlebarLeftImg(int id) {
		mTitleBarLeftImg.setImageResource(id);
	}
	
	public void logOn() {
		logOn(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
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
                            
                            GatewayImpl.getInstance().initialize(MainActivity.this, authtoken, mIMEI);
                            //
                            Fragment frg = null;
							frg = getFragmentManager().findFragmentByTag(PageRecommand.TAG);
							final FragmentTransaction ft = getFragmentManager().beginTransaction();
							ft.detach(frg);
							ft.attach(frg);
							ft.commitAllowingStateLoss();
//							replaceFragment(PageRecommand.TAG, new PageRecommand());
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
                    
                    GatewayImpl.getInstance().initialize(MainActivity.this, authtoken, mIMEI);
                    Log.d(TAG, account.name + " invalidated");
                } catch (Exception e) {
                    e.printStackTrace();
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
	
	public Location getLocation() {
		return mLocation;
	}

	@Override
	public void onBackPressed() {
		mActionBar.hide();
		if (mFinishOnBack || !getFragmentManager().popBackStackImmediate()) {
			finish();
		}
	}
	
	public void replaceFragment(String newTag, Fragment newFrg) {
		getFragmentManager().popBackStackImmediate();
		jumpFragment(newFrg, newTag);

		mFinishOnBack = false;
	}

	public void jumpFragment(Fragment newFragment, String tag) {
		getFragmentManager().beginTransaction()
				.replace(R.id.container, newFragment, tag).addToBackStack(tag)
				.commit();
		mFinishOnBack = false;
	}
	
	public void jumpFragment(Fragment newFragment, String tag, boolean finishOnBack) {
		getFragmentManager().beginTransaction()
				.replace(R.id.container, newFragment, tag).addToBackStack(tag)
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
