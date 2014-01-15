LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := Poker24

# Builds against the public SDK
#LOCAL_SDK_VERSION := current

LOCAL_STATIC_JAVA_LIBRARIES += domob_android_offer_wall_sdk

# open proguard
# LOCAL_PROGUARD_ENABLED := full

LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)

##################################################
# import the thread .jar files.
include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := domob_android_offer_wall_sdk:lib/domob_android_offer_wall_sdk.jar
include $(BUILD_MULTI_PREBUILT)