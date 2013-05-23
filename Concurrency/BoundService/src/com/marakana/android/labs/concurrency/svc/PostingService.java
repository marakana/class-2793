package com.marakana.android.labs.concurrency.svc;

import java.util.concurrent.CountDownLatch;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.marakana.android.labs.concurrency.svc.Poster;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public class PostingService extends Service {
    private static final String TAG = "POST_SVC";

    volatile Poster.PostHandler postHandler;
    volatile CountDownLatch latch;
    private Poster poster;


    private Thread looper;

    @Override
    public void onCreate() {
        Log.d(TAG, "create");
        super.onCreate();
        latch = new CountDownLatch(1);

        looper = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                postHandler = new Poster.PostHandler();
                latch.countDown();
                Looper.loop();
            }
        };
        looper.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (null == poster) {
            try { latch.await(); }
            catch (InterruptedException e) { }
            poster = new Poster(postHandler);
        }

        Log.d(TAG, "bound: " + postHandler);
        return poster;
    }
}
