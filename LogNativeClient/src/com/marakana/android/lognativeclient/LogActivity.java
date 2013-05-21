
package com.marakana.android.lognativeclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.marakana.android.lognativecommon.ILogNativeService;
import com.marakana.android.lognativecommon.LogRequest;
import com.marakana.android.lognativecommon.LogRequest.Type;


public class LogActivity extends Activity implements OnClickListener, ServiceConnection {
    public static final String SERVICE = "com.marakana.android.lognativecommon.LogNativeService";

    private static final String TAG = "LogActivity";

    private static final int[] LOG_LEVEL = {
            Log.VERBOSE, Log.DEBUG, Log.INFO, Log.WARN, Log.ERROR
    };

    private Spinner priority;
    private EditText tag;
    private EditText msg;
    private Button button;
    private RadioGroup type;
    private ILogNativeService service;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.main);
        this.priority = (Spinner) super.findViewById(R.id.log_priority);
        this.tag = (EditText) super.findViewById(R.id.log_tag);
        this.msg = (EditText) super.findViewById(R.id.log_msg);
        this.type = (RadioGroup) super.findViewById(R.id.type);
        this.type.check(R.id.type_log_j);
        this.button = (Button) super.findViewById(R.id.log_button);
        this.button.setOnClickListener(this);
        disconnect();
    }

    public void onClick(View v) {
        int priorityPosition = this.priority.getSelectedItemPosition();
        if (priorityPosition != AdapterView.INVALID_POSITION) {
            final int priority = LOG_LEVEL[priorityPosition];
            final String tag = this.tag.getText().toString();
            final String msg = this.msg.getText().toString();
            if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
                new AlertDialog.Builder(this)
                        .setMessage(R.string.log_tag_errors)
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        LogActivity.this.log(priority, tag, msg);
                                    }
                                }).setNegativeButton(android.R.string.no, null).create().show();
            } else {
                log(priority, tag, msg);
            }
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder svc) {
        service = ILogNativeService.Stub.asInterface(svc);
        this.button.setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
        disconnect();
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) { disconnect(); }

    @Override
    protected void onResume() {
        super.onResume();
        if (!bindService(new Intent(SERVICE), this, BIND_AUTO_CREATE)) {
            Log.w(TAG, "Failed to bind service");
        }
    }

    private void log(int priority, String tag, String msg) {
        Exception err;
        try {
            Type typ;
            switch (this.type.getCheckedRadioButtonId()) {
                case R.id.type_log_j:
                    typ = LogRequest.Type.LOG_JAVA;
                    //LogLib.logJ(priority, tag, msg);
                    break;
                case R.id.type_log_n:
                    typ = LogRequest.Type.LOG_NATIVE;
                    //LogLib.logN(priority, tag, msg);
                    break;
                default:
                    return;
            }
            service.log(new LogRequest(typ, priority, tag, msg));

            this.tag.getText().clear();
            this.msg.getText().clear();

            Toast.makeText(this, R.string.log_success, Toast.LENGTH_SHORT).show();
            return;
        }
        catch (RuntimeException e) { err = e; }
        catch (RemoteException e) { err = e; }

        Toast.makeText(this, R.string.log_error, Toast.LENGTH_SHORT).show();
        Log.wtf(TAG, "Failed to log the message", err);
    }

    private void disconnect() {
        this.button.setEnabled(false);
        service = null;
    }
}
