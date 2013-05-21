package com.marakana.android.fibonaccinative;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class FibonacciActivity extends Activity implements OnClickListener {

    static class FibTask extends AsyncTask<Long, Void, String> {

        private final FibonacciActivity ctxt;
        private int method;

        public FibTask(FibonacciActivity ctxt) { this.ctxt = ctxt; }

        @Override
        protected void onPreExecute() {
            method = ctxt.type.getCheckedRadioButtonId();
        }

        @Override
        protected String doInBackground(Long... params) {
            long n = params[0].longValue();
            long result = 0;
            long t = SystemClock.uptimeMillis();
            switch (method) {
                case R.id.type_fib_jr:
                    result = FibLib.fibJR(n);
                    break;
                case R.id.type_fib_ji:
                    result = FibLib.fibJI(n);
                    break;
                case R.id.type_fib_nr:
                    result = FibLib.fibNR(n);
                    break;
                case R.id.type_fib_ni:
                    result = FibLib.fibNI(n);
                    break;
            }
            t = SystemClock.uptimeMillis() - t;
            return String.format("fib(%d)=%d in %d ms", n, result, t);
        }

        @Override
        protected void onPostExecute(String result) {
            ctxt.dialog.dismiss();
            ctxt.dialog = null;
            ctxt.output.setText(result);
        }
    }

    private EditText input;
    private RadioGroup type;
    private TextView output;
    ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.input = (EditText) super.findViewById(R.id.input);
        this.type = (RadioGroup) super.findViewById(R.id.type);
        this.output = (TextView) super.findViewById(R.id.output);
        Button button = (Button) super.findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    public void onClick(View view) {
        String s = this.input.getText().toString();
        if (TextUtils.isEmpty(s)) { return; }

        dialog = ProgressDialog.show(this, "", "Calculating...", true);
        AsyncTask<Long, Void, String> task = new FibTask(this);
        task.execute(Long.valueOf(s));
    }
}
