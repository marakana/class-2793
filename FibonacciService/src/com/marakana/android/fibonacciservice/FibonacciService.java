package com.marakana.android.fibonacciservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class FibonacciService extends Service {

    private IFibonacciServiceImpl service;

    @Override
    public void onCreate() {
        super.onCreate();
        service = new IFibonacciServiceImpl();
    }

    @Override
    public IBinder onBind(Intent arg0) { return service; }
}
