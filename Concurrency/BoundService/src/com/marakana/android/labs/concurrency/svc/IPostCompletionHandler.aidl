package com.marakana.android.labs.concurrency.svc;

oneway interface IPostCompletionHandler {
    void postCompleted(in int code);
}