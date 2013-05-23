package com.marakana.android.lognativeservice;


import android.os.RemoteException;
import android.util.Log;

import com.marakana.android.lognative.LogLib;
import com.marakana.android.lognativecommon.ILogNativeService;
import com.marakana.android.lognativecommon.LogRequest;

public class ILogNativeServiceImpl extends ILogNativeService.Stub {
    private static final String TAG = "SVCIMPL";

    @Override
    public void log(LogRequest req) throws RemoteException {
        Log.d(TAG, "logging...");
        switch (req.getType()) {
            case LOG_JAVA:
                logJ(req.getLevel(), req.getTag(), req.getMessage());
                break;
            case LOG_NATIVE:
                logN(req.getLevel(), req.getTag(), req.getMessage());
                break;
        }
    }

    @Override
    public void logJ(int priority, String tag, String msg) throws RemoteException {
        LogLib.logJ(priority, tag, msg);
    }

    @Override
    public void logN(int priority, String tag, String msg) throws RemoteException {
        LogLib.logN(priority, tag, msg);
    }
}
