package com.example.test;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;


/**
 * This is a pedagogical tool intended to explain the behavior
 * of an IntentService.  It is neither the actual implementation
 * of an IntentService, nor a replacement for it.
 */
public abstract class MyIntentService extends Service {

    private class IntentTask extends AsyncTask<Intent, Void, Void> {
        @Override
        protected Void doInBackground(Intent... params) {
            onHandleIntent(params[0]);
            return null;
        }
    }

    protected abstract void onHandleIntent(Intent intent);

    @Override
    public IBinder onBind(Intent arg0) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new IntentTask().execute(intent);
        return super.onStartCommand(intent, flags, startId);
    }
}
