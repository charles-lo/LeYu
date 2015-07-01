package com.leyu;

import static com.udinic.accounts_authenticator_example.authentication.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;

import java.io.IOException;
import java.util.Locale;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.udinic.accounts_authenticator_example.authentication.AccountGeneral;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private static final String STATE_DIALOG = "state_dialog";
	private static final String STATE_INVALIDATE = "state_invalidate";

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
    private AlertDialog mAlertDialog;
    private boolean mInvalidate;
	
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
		//
		mAccountManager = AccountManager.get(this);
		
		if (savedInstanceState != null) {
        	boolean showDialog = savedInstanceState.getBoolean(STATE_DIALOG);
        	boolean invalidate = savedInstanceState.getBoolean(STATE_INVALIDATE);
        	if (showDialog) {
        		showAccountPicker(AUTHTOKEN_TYPE_FULL_ACCESS, invalidate);
        	}
        }
//		addNewAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
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
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	if (mAlertDialog != null && mAlertDialog.isShowing()) {
    		outState.putBoolean(STATE_DIALOG, true);
    		outState.putBoolean(STATE_INVALIDATE, mInvalidate);
    	}
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		return super.onOptionsItemSelected(item);
	}
	
	public void addNewAccount(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.addAccount(accountType, authTokenType, null, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bnd = future.getResult();
                    Log.d("udinic", "AddNewAccount Bundle is " + bnd);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, null);
    }
	
	private void showAccountPicker(final String authTokenType, final boolean invalidate) {
    	mInvalidate = invalidate;
        final Account availableAccounts[] = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        if (availableAccounts.length == 0) {
            Toast.makeText(this, "No accounts", Toast.LENGTH_SHORT).show();
        } else {
            String name[] = new String[availableAccounts.length];
            for (int i = 0; i < availableAccounts.length; i++) {
                name[i] = availableAccounts[i].name;
            }

            // Account picker
            mAlertDialog = new AlertDialog.Builder(this).setTitle("Pick Account").setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, name), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(invalidate)
                        invalidateAuthToken(availableAccounts[which], authTokenType);
                    else
                        getExistingAccountAuthToken(availableAccounts[which], authTokenType);
                }
            }).create();
            mAlertDialog.show();
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
                    showMessage(account.name + " invalidated");
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage());
                }
            }
        }).start();
    }

    /**
     * Get an auth token for the account.
     * If not exist - add it and then return its auth token.
     * If one exist - return its auth token.
     * If more than one exists - show a picker and return the select account's auth token.
     * @param accountType
     * @param authTokenType
     */
    private void getTokenForAccountCreateIfNeeded(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthTokenByFeatures(accountType, authTokenType, null, this, null, null,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        Bundle bnd = null;
                        try {
                            bnd = future.getResult();
                            final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                            showMessage(((authtoken != null) ? "SUCCESS!\ntoken: " + authtoken : "FAIL"));
                            Log.d("udinic", "GetTokenForAccount Bundle is " + bnd);

                        } catch (Exception e) {
                            e.printStackTrace();
                            showMessage(e.getMessage());
                        }
                    }
                }
        , null);
    }
    
    /**
     * Get the auth token for an existing account on the AccountManager
     * @param account
     * @param authTokenType
     */
    private void getExistingAccountAuthToken(Account account, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, this, null, null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle bnd = future.getResult();

                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    showMessage((authtoken != null) ? "SUCCESS!\ntoken: " + authtoken : "FAIL");
                    Log.d("udinic", "GetToken Bundle is " + bnd);
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage());
                }
            }
        }).start();
    }
    
    private void showMessage(final String msg) {
    	if (TextUtils.isEmpty(msg))
            return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
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
