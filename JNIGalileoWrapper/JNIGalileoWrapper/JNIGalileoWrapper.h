#ifndef __JNI_GALILEO_WRAPPER_H__
#define __JNI_GALILEO_WRAPPER_H__
#include <jni.h>
#include <javagalileo_GalileoSDK.h>
#include <mutex>
#ifdef __ANDROID__
#include <android/log.h>
#define LOGV(TAG,...) __android_log_print(ANDROID_LOG_VERBOSE, TAG,__VA_ARGS__)
#define LOGD(TAG,...) __android_log_print(ANDROID_LOG_DEBUG  , TAG,__VA_ARGS__)
#define LOGI(TAG,...) __android_log_print(ANDROID_LOG_INFO   , TAG,__VA_ARGS__)
#define LOGW(TAG,...) __android_log_print(ANDROID_LOG_WARN   , TAG,__VA_ARGS__)
#define LOGE(TAG,...) __android_log_print(ANDROID_LOG_ERROR  , TAG,__VA_ARGS__)
#else
#define LOGV(TAG,...)
#define LOGD(TAG,...)
#define LOGI(TAG,...)
#define LOGW(TAG,...)
#define LOGE(TAG,...)
#endif
#ifdef _WIN32
#include "GalileoSDK.h"
#else
#include <GalileoSDK/GalileoSDK.h>
#endif
std::mutex mMutex;
#endif // !__JNI_GALILEO_WRAPPER_H__
