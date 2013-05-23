package com.marakana.android.yamba;


import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class StatusFragment extends Fragment {
    public static final String TAG = "STATUS";

    private static final int MIN_CHARS = 0;
    private static final int WARN_CHARS = 10;
    private static final int MAX_CHARS = 140;

    private TextView textCount;
    private EditText editText;

    static class Poster extends AsyncTask<String, Void, Void> {
        private final ContentResolver cr;

        public Poster(ContentResolver cr) { this.cr = cr; }

        @Override
        protected Void doInBackground(String... args) {
            ContentValues values = new ContentValues();
            values.put(YambaContract.Posts.Columns.STATUS, args[0]);
            cr.insert(YambaContract.Posts.URI, values);
            return null;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
        super.onCreateView(inflater, parent, state);

        View root = inflater.inflate(R.layout.status, parent, false);

        textCount = (TextView) root.findViewById(R.id.count_text);

        editText = (EditText) root.findViewById(R.id.status_text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) { updateStatusLen(); }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });

        root.findViewById(R.id.status_button).setOnClickListener(
                new Button.OnClickListener() {
                    @Override public void onClick(View v) { update(); }
                } );

        return root;
    }

    void update() {
        String msg = editText.getText().toString();
        if (BuildConfig.DEBUG) { Log.d(TAG, "update: " + msg); }

        if (TextUtils.isEmpty(msg)) { return; }

        editText.setText("");

        new Poster(getActivity().getApplicationContext().getContentResolver()).execute(msg);
    }

    void updateStatusLen() {
        int remaining = MAX_CHARS - editText.getText().length();

        int color;
        if (remaining <= MIN_CHARS) { color = Color.RED; }
        else if (remaining <= WARN_CHARS) { color = Color.YELLOW; }
        else { color = Color.GREEN; }

        textCount.setText(String.valueOf(remaining));
        textCount.setTextColor(color);
    }
}
