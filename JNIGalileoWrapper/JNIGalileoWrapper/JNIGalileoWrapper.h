#ifndef __JNI_GALILEO_WRAPPER_H__
#define __JNI_GALILEO_WRAPPER_H__
#include <jni.h>
#include <javagalileo_GalileoSDK.h>
#ifdef _WIN32
#include "GalileoSDK.h"
#else
#include <GalileoSDK/GalileoSDK.h>
#endif
#endif // !__JNI_GALILEO_WRAPPER_H__
