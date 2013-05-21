package com.marakana.android.lognativecommon;


import android.os.Parcel;
import android.os.Parcelable;

public class LogRequest implements Parcelable {
    public enum Type { LOG_NATIVE, LOG_JAVA; }

    public static final Parcelable.Creator<LogRequest> CREATOR
        = new Parcelable.Creator<LogRequest>() {
            @Override
            public LogRequest createFromParcel(Parcel source) {
                int type = source.readInt();
                int level = source.readInt();
                String tag = source.readString();
                String message = source.readString();
                return new LogRequest(Type.values()[type], level, tag, message);
            }

            @Override
            public LogRequest[] newArray(int size) { return new LogRequest[size]; }
    };


    private final Type type;
    private final int level;
    private final String tag;
    private final String message;

    public LogRequest(Type type, int level, String tag, String message) {
        this.type = type;
        this.level = level;
        this.tag = tag;
        this.message = message;
    }

    public Type getType() { return type; }

    public int getLevel() { return level; }

    public String getTag() { return tag; }

    public String getMessage() { return message; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(type.ordinal());
        parcel.writeInt(level);
        parcel.writeString(tag);
        parcel.writeString(message);
    }
}
