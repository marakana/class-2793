package com.example.test;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;


/**
 * A shell that demonstrates a best-practice framework for using an intent service.
 *
 * Note that PendingIntents are a useful tool but, by a long shot, not the only tools
 * for returning data from a Service.
 */
public class NetworkService extends MyIntentService {

    public static interface PullBodyHandler { void onPullComplete(String body); }

    private static final String TAG = "NET";
    private static final int REQ_ID = 66;

    private static final int OP_PUSH = 0;
    private static final int OP_PULL = 1;

    private static final String PARAM_OP = "com.example.test.OP";
    private static final String PARAM_URI = "com.example.test.URI";
    private static final String PARAM_BODY = "com.example.test.BODY";
    private static final String PARAM_RESP = "com.example.test.RESP";

    public static void push(Activity ctxt, Uri uri, String body) {
        Intent i = new Intent(ctxt, NetworkService.class);
        i.putExtra(PARAM_OP, OP_PUSH);
        i.putExtra(PARAM_URI, uri.toString());
        i.putExtra(PARAM_BODY, body);
        ctxt.startService(i);
    }

    public static void pull(Activity ctxt, Uri uri) {
        Intent i = new Intent(ctxt, NetworkService.class);
        i.putExtra(PARAM_OP, OP_PULL);
        i.putExtra(PARAM_URI, uri.toString());
        i.putExtra(PARAM_RESP, ctxt.createPendingResult(REQ_ID, new Intent(), PendingIntent.FLAG_ONE_SHOT));
        ctxt.startService(i);
    }

    public static boolean onResult(
        int reqCode,
        int resCode,
        Intent resp,
        PullBodyHandler hdlr)
    {
        Log.d(TAG, "post reply: " + reqCode + ", " + resCode);

        // verify that this request belongs to us
        if (REQ_ID != reqCode) { return false; }

        String body = "";
        if (Activity.RESULT_OK == resCode) {
            body = resp.getStringExtra(PARAM_RESP);
        }
        hdlr.onPullComplete(body);

        return true;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int op = intent.getIntExtra(PARAM_OP, -1);
        switch (op) {
            case OP_PUSH:
                push(intent.getStringExtra(PARAM_URI), intent.getStringExtra(PARAM_BODY));
                break;
            case OP_PULL:
                pull(intent.getStringExtra(PARAM_URI), (PendingIntent) intent.getParcelableExtra(PARAM_RESP));
                break;
            default:
                throw new IllegalArgumentException("Unrecognized OP: " + op);
        }
    }

    private void pull(String stringExtra, PendingIntent resp) {

        // run network query...

        /// produce body
        String body = " all done";

        Intent reply = new Intent();
        reply.putExtra(PARAM_RESP, body);

        try { resp.send(this, Activity.RESULT_OK, reply); }
        catch (CanceledException e) { Log.w(TAG, "post cancelled", e); }
    }

    private void push(String stringExtra, String stringExtra2) {
        // TODO Auto-generated method stub

    }
}
