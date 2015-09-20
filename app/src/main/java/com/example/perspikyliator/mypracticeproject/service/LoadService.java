package com.example.perspikyliator.mypracticeproject.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.perspikyliator.mypracticeproject.MainActivity;
import com.example.perspikyliator.mypracticeproject.interfaces.CallbackBankInfoLoader;
import com.example.perspikyliator.mypracticeproject.loader.InternetAsyncTask;
import com.example.perspikyliator.mypracticeproject.model.BankArray;

public class LoadService extends Service implements CallbackBankInfoLoader {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (checkNetworkState()) {
            new InternetAsyncTask(this, getApplicationContext()).execute(MainActivity.SERVICE);
        } else {
            stopSelf();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onBankSuccess(BankArray bankArray) {
        stopSelf();
    }

    @Override
    public void onBankFailure(String errorMessage) {
        stopSelf();
    }

    public boolean checkNetworkState() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
