package com.tongle.accounts;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created with IntelliJ IDEA.
 * User: Udini
 * Date: 19/03/13
 * Time: 19:10
 */
public class TongleAuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {

        TongleAuthenticator authenticator = new TongleAuthenticator(this);
        return authenticator.getIBinder();
    }
}
