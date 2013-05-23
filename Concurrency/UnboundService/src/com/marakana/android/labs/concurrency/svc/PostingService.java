/* $Id: $
   Copyright 2012, G. Blake Meike

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.marakana.android.labs.concurrency.svc;

import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Intent;
import android.util.Log;

import com.marakana.android.labs.concurrency.MainActivity;
import com.marakana.android.labs.concurrency.client.NetworkClient;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public class PostingService extends IntentService {
    public static final String CALLBACK = "CALLBACK";
    public static final String TEXT = "TEXT";
    public static final String RESULT = "RESULT";

    private static final String TAG = "POST_SVC";

    public PostingService() { super(TAG); }

    @Override
    protected void onHandleIntent(Intent req) {
        Log.d(TAG, "post req");

        Integer status = post(req.getStringExtra(TEXT));

        Intent resp = new Intent();
        resp.putExtra(RESULT, status);

        PendingIntent reply = (PendingIntent) req.getParcelableExtra(CALLBACK);

        Log.d(TAG, "post reply");
        try { reply.send(this, Activity.RESULT_OK, resp); }
        catch (CanceledException e) { Log.w(TAG, "post cancelled", e); }
    }

    private Integer post(String text) {
        Log.d(TAG, "posting: " + text);
        return Integer.valueOf((NetworkClient.getNetClient().post(text))
                ? MainActivity.STATUS_SUCCESS
                : MainActivity.STATUS_FAIL);
    }
}
