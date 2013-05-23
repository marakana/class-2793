/* $Id: $
   Copyright 2013, G. Blake Meike

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

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public abstract class MyIntentService extends Service {

    static class ServiceTask extends AsyncTask<Intent, Void, Void> {
        private MyIntentService svc;

        ServiceTask(MyIntentService svc) { this.svc = svc; }

        @Override
        protected Void doInBackground(Intent... params) {
            svc.onHandleIntent(params[0]);
            return null;
        }
    }

    protected abstract void onHandleIntent(Intent intent);

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new ServiceTask(this).execute(intent);
        return 0;
    }
}
