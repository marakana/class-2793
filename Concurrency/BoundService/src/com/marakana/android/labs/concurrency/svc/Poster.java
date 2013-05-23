package com.marakana.android.labs.concurrency.svc;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.marakana.android.labs.concurrency.MainActivity;
import com.marakana.android.labs.concurrency.client.NetworkClient;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
class Poster extends IPostingService.Stub {
    private static final String TAG = "POSTER";

    private static final int POST_MESSAGE_ID = 42;

    static class PostHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case POST_MESSAGE_ID:
                    Log.d(TAG, "post message");
                    PostArgs args = (PostArgs) message.obj;
                    int status = (NetworkClient.getNetClient().post(args.text))
                            ? MainActivity.STATUS_SUCCESS
                            : MainActivity.STATUS_FAIL;
                    Log.d(TAG, "post status: " + status);
                    try { args.hdlr.postCompleted(status); }
                    catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private static class PostArgs {
        public final String text;
        public final IPostCompletionHandler hdlr;
        public PostArgs(String text, IPostCompletionHandler hdlr) {
            this.text = text;
            this.hdlr = hdlr;
        }
    }

    private PostHandler postHandler;

    public Poster(PostHandler postHandler) { this.postHandler = postHandler; }

    @Override
    public void post(final String text, final IPostCompletionHandler hdlr) {
        Log.d(TAG, "posting: " + text);
        Message msg = postHandler.obtainMessage(
                POST_MESSAGE_ID,
                new PostArgs(text, hdlr));
        postHandler.sendMessage(msg);
    }
}