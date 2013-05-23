package com.marakana.android.labs.concurrency;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.Activity;
import android.graphics.Color;

import com.marakana.android.labs.concurrency.client.NetworkClient;


public class MainActivity extends Activity {
    private static final String TAG = "MAIN";


    private View status;
    private EditText postText;


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

    void post() {
        String text = postText.getText().toString();
        if (TextUtils.isEmpty(text)) { return; }

        postText.setText("");
        status.setBackgroundColor(Color.YELLOW);

        Log.d(TAG, "posting: " + text);
        boolean ok = NetworkClient.getNetClient().post(text);
        status.setBackgroundColor(ok ? Color.GREEN : Color.RED);
        Log.d(TAG, "posting completed");
    }
}
