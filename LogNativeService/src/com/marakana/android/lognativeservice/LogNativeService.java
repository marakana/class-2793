package com.marakana.android.lognativeservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LogNativeService extends Service {

    private ILogNativeServiceImpl service;

    @Override
    public void onCreate() { service = new ILogNativeServiceImpl(); }

    @Override
    public IBinder onBind(Intent arg0) { return service; }
}
