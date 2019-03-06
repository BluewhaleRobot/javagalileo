// JNIGalileoWrapper.cpp : Defines the exported functions for the DLL application.
//
#include "JNIGalileoWrapper.h"


JNIEXPORT void JNICALL Java_javagalileo_GalileoSDK_sayHello
(JNIEnv *, jobject) {
    printf("Hello world\n");
    return;
}


JNIEXPORT jlong JNICALL Java_javagalileo_GalileoSDK_CreateInstance
(JNIEnv *, jobject) {
    GalileoSDK::GalileoSDK* instance = new GalileoSDK::GalileoSDK();
    return (long)instance;
}

JNIEXPORT void JNICALL Java_javagalileo_GalileoSDK_ReleaseInstance
(JNIEnv *, jobject, jlong instance) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    sdk->Dispose();
    delete sdk;
}

jobject OnConnectCB;
jobject OnDisconnectCB;
JavaVM* jvm = 0;

jobject ConvertGalileoReturnCode(JNIEnv* env, GalileoSDK::GALILEO_RETURN_CODE res) {
    std::string res_str = GalileoSDK::GalileoReturnCodeToString(res);
    jclass clSTATUS = env->FindClass("Ljavagalileo/models/ServerInfo$GALILEO_RETURN_CODE;");
    jfieldID fidONE = env->GetStaticFieldID(clSTATUS, res_str.data(), "Ljavagalileo/models/ServerInfo$GALILEO_RETURN_CODE;");
    jobject STATUS = env->GetStaticObjectField(clSTATUS, fidONE);
    return STATUS;
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_Connect
(JNIEnv *env, jobject, jlong instance, jstring targetID, jboolean auto_connect, jint timeout,
    jobject OnConnect, jobject OnDisconnect) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    env->GetJavaVM(&jvm);
    // parse target id
    const char *target_id = env->GetStringUTFChars(targetID, NULL);
    jsize length = env->GetStringUTFLength(targetID);
    std::string target_id_str(target_id, length);

    // set referance to callbacks
    OnConnectCB = env->NewGlobalRef(OnConnect);
    OnDisconnectCB = env->NewGlobalRef(OnDisconnect);

    std::cout << "OK1" << std::endl;
    // parse callback
    jmethodID OnConnectID = NULL;
    if (!env->IsSameObject(OnConnect, NULL)) {
        jclass OnConnectClass = env->GetObjectClass(OnConnect);
        OnConnectID = env->GetMethodID(OnConnectClass, "OnConnected",
            "(Ljavagalileo/models/ServerInfo$GALILEO_RETURN_CODE;Ljava/lang/String;)V");
    }

    std::cout << "OK2" << std::endl;
    
    jmethodID OnDisconnectID = NULL;
    if (!env->IsSameObject(OnConnect, NULL)) {
        jclass OnDisconnectClass = env->GetObjectClass(OnDisconnect);
        OnDisconnectID = env->GetMethodID(OnDisconnectClass, "OnDisconnected",
            "(Ljavagalileo/models/ServerInfo$GALILEO_RETURN_CODE;Ljava/lang/String;)V");
    }

    std::cout << "OK3" << std::endl;
    
    void(*OnConnectTmp)(GalileoSDK::GALILEO_RETURN_CODE, std::string id);
    void(*OnDisconnectTmp)(GalileoSDK::GALILEO_RETURN_CODE, std::string id);
    OnConnectTmp = NULL;
    OnDisconnectTmp = NULL;

    std::cout << "OK4" << std::endl;

    if (NULL != OnConnectID) {
        OnConnectTmp = [](GalileoSDK::GALILEO_RETURN_CODE status, std::string id) {
            JNIEnv* menv;
            jvm->AttachCurrentThread((void**)&menv, NULL);
            jclass OnConnectClass = menv->GetObjectClass(OnConnectCB);
            jmethodID OnConnectID = menv->GetMethodID(OnConnectClass, "OnConnected",
                "(Ljavagalileo/models/ServerInfo$GALILEO_RETURN_CODE;Ljava/lang/String;)V");
            if (NULL != OnConnectID) {
                jobject statusJ = ConvertGalileoReturnCode(menv, status);
                jstring idJ = menv->NewStringUTF(id.data());
                menv->CallVoidMethod(OnConnectCB, OnConnectID, statusJ, idJ);
            }
            jvm->DetachCurrentThread();
        };
    }
    std::cout << "OK5" << std::endl;
    if (NULL != OnDisconnectID) {
        OnDisconnectTmp = [](GalileoSDK::GALILEO_RETURN_CODE status, std::string id) {
            JNIEnv* menv;
            jvm->AttachCurrentThread((void**)&menv, NULL);
            jclass OnDisconnectClass = menv->GetObjectClass(OnDisconnectCB);
            jmethodID OnDisconnectID = menv->GetMethodID(OnDisconnectClass, "OnDisconnected",
                "(Ljavagalileo/models/ServerInfo$GALILEO_RETURN_CODE;Ljava/lang/String;)V");
            if (NULL != OnDisconnectID) {
                jobject statusJ = ConvertGalileoReturnCode(menv, status);
                jstring idJ = menv->NewStringUTF(id.data());
                menv->CallVoidMethod(OnConnectCB, OnDisconnectID, statusJ, idJ);
            }
            jvm->DetachCurrentThread();
        };
    }
    
    auto res = sdk->Connect(target_id_str, auto_connect, timeout, OnConnectTmp, OnDisconnectTmp);
    std::cout << "OK6" << std::endl;
    env->ReleaseStringUTFChars(targetID, target_id);
    std::cout << "OK7" << std::endl;
    return ConvertGalileoReturnCode(env, res);
}

