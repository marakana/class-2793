package com.marakana.jniexamples;

public class Hello {

    public static native void sayHi(String who, int times);

    static { System.loadLibrary("hello"); }

    public static void main(String[] args) {
        sayHi(args[0], Integer.parseInt(args[1])); //
    }
}
