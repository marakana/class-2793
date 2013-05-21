package com.marakana.android.fibonaccicommon;

import android.os.Parcel;
import android.os.Parcelable;

public class FibonacciResponse implements Parcelable {

    public static final Parcelable.Creator<FibonacciResponse> CREATOR
    = new Parcelable.Creator<FibonacciResponse>() {
        public FibonacciResponse createFromParcel(Parcel in) {
            long result = in.readLong();
            long time = in.readLong();
            return new FibonacciResponse(result, time);
        }

        public FibonacciResponse[] newArray(int size) { return new FibonacciResponse[size]; }
    };

	private final long result;
	private final long timeInMillis;

	public FibonacciResponse(long result, long timeInMillis) {
		this.result = result;
		this.timeInMillis = timeInMillis;
	}

	public long getResult() { return result; }

	public long getTimeInMillis() { return timeInMillis; }

	public int describeContents() { return 0; }

	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(this.result);
		parcel.writeLong(this.timeInMillis);
	}
}
