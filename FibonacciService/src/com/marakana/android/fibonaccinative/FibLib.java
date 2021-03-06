
package com.marakana.android.fibonaccinative;

import android.util.Log;

public class FibLib {
    private static final String TAG = "FibLib";

    static { System.loadLibrary("FibLib"); }

    private static long fib(long n) {
        return n <= 0 ? 0 : n == 1 ? 1 : fib(n - 1) + fib(n - 2);
    }

    // Recursive Java implementation of the Fibonacci algorithm
    // (included for comparison only)
    public static long fibJR(long n) {
        Log.d(TAG, "fibJR(" + n + ")");
        return fib(n);
    }

    // Iterative Java implementation of the Fibonacci algorithm
    // (included for comparison only)
    public static long fibJI(long n) {
        Log.d(TAG, "fibJI(" + n + ")");
        long previous = -1;
        long result = 1;
        for (long i = 0; i <= n; i++) {
            long sum = result + previous;
            previous = result;
            result = sum;
        }
        return result;
    }

    public static native long fibNR(long n);

    public static native long fibNI(long n);
}
