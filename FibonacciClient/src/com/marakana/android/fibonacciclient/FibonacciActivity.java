package com.marakana.android.fibonacciclient;

import com.marakana.android.fibonaccicommon.FibonacciRequest;
import com.marakana.android.fibonaccicommon.FibonacciResponse;
import com.marakana.android.fibonaccicommon.IFibonacciService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class FibonacciActivity extends Activity implements OnClickListener, ServiceConnection {
    public static final String SERVICE = "com.marakana.android.fibonaccicommon.IFibonacciService";
    private static final String TAG = "FIBACTIVITY";

    static class FibTask extends AsyncTask<Long, Void, String> {

        private final FibonacciActivity ctxt;
        private int method;

        public FibTask(FibonacciActivity ctxt) { this.ctxt = ctxt; }

        @Override
        protected void onPreExecute() { method = ctxt.type.getCheckedRadioButtonId(); }

        @Override
        protected String doInBackground(Long... params) {
            try {
                long result = 0;
                FibonacciResponse response = null;

                FibonacciRequest.Type type = null;
                long n = params[0].longValue();

                long t = SystemClock.uptimeMillis();
                if (null != ctxt.service) {
                    switch (method) {
                        case R.id.type_fib_jr:
                            type = FibonacciRequest.Type.RECURSIVE_JAVA;
                            //result = ctxt.service.fibJR(n);
                            break;
                        case R.id.type_fib_ji:
                            type = FibonacciRequest.Type.ITERATIVE_JAVA;
                            //result = ctxt.service.fibJI(n);
                            break;
                        case R.id.type_fib_nr:
                            type = FibonacciRequest.Type.RECURSIVE_NATIVE;
                            //result = ctxt.service.fibNR(n);
                            break;
                        case R.id.type_fib_ni:
                            type = FibonacciRequest.Type.ITERATIVE_NATIVE;
                            //result = ctxt.service.fibNI(n);
                            break;
                    }
                    response = ctxt.service.fib(new FibonacciRequest(type, n));
                }
                t = SystemClock.uptimeMillis() - t;

                result = response.getResult();

                long computeTime = response.getTimeInMillis();
                // long computeTime = t;

                return String.format(
                        "fib(%d) = %d\n\t compute time: %d ms\n\t binder overhead: %d ms",
                        n, result,
                        computeTime,
                        t - computeTime);
            }
            catch (RemoteException e) { Log.w(TAG, "Remote exception", e); }
            return "computation failed!";
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
    Button button;
    IFibonacciService service;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.input = (EditText) super.findViewById(R.id.input);
        this.type = (RadioGroup) super.findViewById(R.id.type);
        this.output = (TextView) super.findViewById(R.id.output);

        button = (Button) super.findViewById(R.id.button);
        button.setOnClickListener(this);
        button.setEnabled(false);
    }

    public void onClick(View view) {
        String s = this.input.getText().toString();
        if (TextUtils.isEmpty(s)) { return; }

        dialog = ProgressDialog.show(this, "", "Calculating...", true);
        AsyncTask<Long, Void, String> task = new FibTask(this);
        task.execute(Long.valueOf(s));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!bindService(new Intent(SERVICE), this, BIND_AUTO_CREATE)) {
            Log.w(TAG, "Failed to bind service");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unbindService(this);
        disconnect();
    }

    public void onServiceConnected(ComponentName name, IBinder svc) {
        service = IFibonacciService.Stub.asInterface(svc);
        button.setEnabled(true);
    }

    public void onServiceDisconnected(ComponentName arg0) {
        disconnect();
    }

    private void disconnect() {
        button.setEnabled(false);
        service = null;
    }
}
