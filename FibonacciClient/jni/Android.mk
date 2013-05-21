LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_SRC_FILES := com_marakana_android_fibonaccinative_FibLib.c
LOCAL_MODULE := FibLib
LOCAL_LDLIBS += -llog
include $(BUILD_SHARED_LIBRARY)

