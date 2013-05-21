package com.marakana.android.fibonacciservice;

import android.os.RemoteException;
import android.os.SystemClock;

import com.marakana.android.fibonaccicommon.FibonacciRequest;
import com.marakana.android.fibonaccicommon.FibonacciResponse;
import com.marakana.android.fibonaccicommon.IFibonacciService;
import com.marakana.android.fibonaccinative.FibLib;

public class IFibonacciServiceImpl extends IFibonacciService.Stub {

    public FibonacciResponse fib(FibonacciRequest req) throws RemoteException {
        long result = 0;
        long t = SystemClock.uptimeMillis();
        switch (req.getType()) {
            case ITERATIVE_JAVA:
                 result = fibJI(req.getN());
                 break;
            case ITERATIVE_NATIVE:
                result = fibNI(req.getN());
                break;
            case RECURSIVE_JAVA:
                result = fibJR(req.getN());
                break;
            case RECURSIVE_NATIVE:
                result = fibNR(req.getN());
                break;
        }
        return new FibonacciResponse(result, SystemClock.uptimeMillis() - t);
    }

    public long fibJI(long n) throws RemoteException {
        return FibLib.fibJI(n);
    }

    public long fibJR(long n) throws RemoteException {
        return FibLib.fibJR(n);
    }

    public long fibNI(long n) throws RemoteException {
        return FibLib.fibNI(n);
    }

    public long fibNR(long n) throws RemoteException {
        return FibLib.fibNR(n);
    }
}
