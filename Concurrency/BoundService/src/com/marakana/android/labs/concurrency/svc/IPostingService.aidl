package com.marakana.android.labs.concurrency.svc;

import com.marakana.android.labs.concurrency.svc.IPostCompletionHandler;

oneway interface IPostingService {
    void post(in String text, in IPostCompletionHandler hdlr);
}