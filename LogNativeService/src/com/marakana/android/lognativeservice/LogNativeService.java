package com.marakana.android.lognativeservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class LogNativeService extends Service {
    private static final String TAG = "SVC";

    private ILogNativeServiceImpl service;

    @Override
    public void onCreate() {
        Log.d(TAG, "creating...");
        service = new ILogNativeServiceImpl();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.d(TAG, "binding...");
        return service;
    }
}
