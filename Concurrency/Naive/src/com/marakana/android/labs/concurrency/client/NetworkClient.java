package com.marakana.android.labs.concurrency.client;

import android.util.Log;


/**
 * NetworkClient
 */
public class NetworkClient {
    private static final String TAG ="NET_CLIENT";

    private static final int DELAY = 3;
    private static final int MAX_RETRIES = 5;


    private static volatile NetworkClient client;
    private volatile boolean cancelled;

    public static NetworkClient getNetClient() {
        if (null == client) { client = new NetworkClient(); }
        return client;
    }

    public boolean post(String text) {
        Log.d(TAG, "posting: " + text);
        cancelled = false;
        for (int i = 0; i < MAX_RETRIES; i++) {
            if (cancelled) {
                Log.d(TAG, "post cancelled");
                break;
            }
            try {
                simulateNetwork();
                Log.d(TAG, "post succeeded");
                return true;
            }
            catch (Exception e) {
                Log.w(TAG, "post failed", e);
            }
        }
        Log.d(TAG, "post finished");
        return false;
    }

    @SuppressWarnings("unused")
    private void simulateNetwork() throws InterruptedException {
        if (0 >= DELAY) { return; }
        try { Thread.sleep(DELAY * 1000); }
        catch (InterruptedException e) {
            cancelled = true;
            throw e;
        }
    }
}
