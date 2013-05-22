package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;


public class MainActivity extends Activity implements NetworkService.PullBodyHandler {
    private static final String TAG = "MAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void onClick() {
        NetworkService.push(this, Uri.parse("http://foo.bar"), "some message");
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent resp) {
        Log.d(TAG, "received: " + reqCode + ", " + resCode);
        if (NetworkService.onResult(reqCode, resCode, resp, this)) { return; }
        Log.w("ACTIVITY", "unhandled request" + reqCode);
    }

    @Override
    public void onPullComplete(String body) {
        // handle body...
    }
}
