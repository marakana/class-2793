package com.marakana.android.lognativecommon;

import com.marakana.android.lognativecommon.LogRequest;

interface ILogNativeService {
   void logJ(int priority, String tag, String msg);
   void logN(int priority, String tag, String msg);
   void log(in LogRequest req);
}
