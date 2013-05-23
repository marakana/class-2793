package com.marakana.android.labs.concurrency;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;

import com.marakana.android.labs.concurrency.svc.PostingServiceHelper;
import com.marakana.android.labs.concurrency.svc.PostingServiceHelper.PostCompletionHandler;


public class MainActivity extends Activity implements PostCompletionHandler {
    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_FAIL = -1;

    private static final String TAG = "MAIN";

    private View status;
    private EditText postText;
    private boolean posting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        postText = (EditText) findViewById(R.id.mainText);
        status = findViewById(R.id.mainStatus);

        findViewById(R.id.mainButton).setOnClickListener(
                new Button.OnClickListener() {
                    @Override public void onClick(View v) { post(); }
                } );
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent resp) {
        Log.d(TAG, "received: " + reqCode + ", " + resCode);
        if (PostingServiceHelper.getInstance().onResult(reqCode, resCode, resp, this))
        {
            return;
        }
        Log.w("ACTIVITY", "unhandled request" + reqCode);
    }

    void post() {
        if (posting) { return; }
        posting = true;

        String text = postText.getText().toString();
        if (TextUtils.isEmpty(text)) { return; }

        postText.setText("");
        status.setBackgroundColor(Color.YELLOW);

        Log.d(TAG, "posting: " + text);
        PostingServiceHelper.getInstance().post(this, text);
    }

    @Override
    public void postComplete(int ret) {
        Log.d(TAG, "post complete");
        status.setBackgroundColor((0 <= ret) ? Color.GREEN : Color.RED);
        posting = false;
    }
}
