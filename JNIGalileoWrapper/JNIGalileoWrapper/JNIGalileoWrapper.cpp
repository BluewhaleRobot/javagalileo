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
    // parse callback
    jmethodID OnConnectID = NULL;
    if (!env->IsSameObject(OnConnect, NULL)) {
        jclass OnConnectClass = env->GetObjectClass(OnConnect);
        OnConnectID = env->GetMethodID(OnConnectClass, "OnConnected",
            "(Ljavagalileo/models/ServerInfo$GALILEO_RETURN_CODE;Ljava/lang/String;)V");
    }

    jmethodID OnDisconnectID = NULL;
    if (!env->IsSameObject(OnDisconnect, NULL)) {
        jclass OnDisconnectClass = env->GetObjectClass(OnDisconnect);
        OnDisconnectID = env->GetMethodID(OnDisconnectClass, "OnDisconnected",
            "(Ljavagalileo/models/ServerInfo$GALILEO_RETURN_CODE;Ljava/lang/String;)V");
    }

    void(*OnConnectTmp)(GalileoSDK::GALILEO_RETURN_CODE, std::string id);
    void(*OnDisconnectTmp)(GalileoSDK::GALILEO_RETURN_CODE, std::string id);
    OnConnectTmp = NULL;
    OnDisconnectTmp = NULL;

    if (NULL != OnConnectID) {
        OnConnectTmp = [](GalileoSDK::GALILEO_RETURN_CODE status, std::string id) {
            JNIEnv* menv;
            jvm->AttachCurrentThread((JNIEnv**)&menv, NULL);
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

    if (NULL != OnDisconnectID) {
        OnDisconnectTmp = [](GalileoSDK::GALILEO_RETURN_CODE status, std::string id) {
            JNIEnv* menv;
            jvm->AttachCurrentThread((JNIEnv**)&menv, NULL);
            jclass OnDisconnectClass = menv->GetObjectClass(OnDisconnectCB);
            jmethodID OnDisconnectID = menv->GetMethodID(OnDisconnectClass, "OnDisconnected",
                "(Ljavagalileo/models/ServerInfo$GALILEO_RETURN_CODE;Ljava/lang/String;)V");
            if (NULL != OnDisconnectID) {
                jobject statusJ = ConvertGalileoReturnCode(menv, status);
                jstring idJ = menv->NewStringUTF(id.data());
                menv->CallVoidMethod(OnDisconnectCB, OnDisconnectID, statusJ, idJ);
            }
            jvm->DetachCurrentThread();
        };
    }

    auto res = sdk->Connect(target_id_str, auto_connect, timeout, OnConnectTmp, OnDisconnectTmp);
    env->ReleaseStringUTFChars(targetID, target_id);
    return ConvertGalileoReturnCode(env, res);
}

jobject ConvertServerInfo(JNIEnv *env, GalileoSDK::ServerInfo info) {
    jclass serverInfoClass = env->FindClass("javagalileo/models/ServerInfo");
    jmethodID serverInfoInit = env->GetMethodID(serverInfoClass, "<init>", "()V");
    jobject serverInfoJ = env->NewObject(serverInfoClass, serverInfoInit);
    // set attributes
    jfieldID id_field = env->GetFieldID(serverInfoClass, "ID", "Ljava/lang/String;");
    jstring id_j = env->NewStringUTF(info.getID().data());
    env->SetObjectField(serverInfoJ, id_field, id_j);

    jfieldID port_field = env->GetFieldID(serverInfoClass, "port", "Ljava/lang/Integer;");
    jclass int_class = env->FindClass("java/lang/Integer");
    jmethodID int_init = env->GetMethodID(int_class, "<init>", "(I)V");
    jobject port_j = env->NewObject(int_class, int_init, info.getPort());
    env->SetObjectField(serverInfoJ, port_field, port_j);

    jfieldID timestamp_field = env->GetFieldID(serverInfoClass, "timestamp", "Ljava/lang/Long;");
    jclass long_class = env->FindClass("java/lang/Long");
    jmethodID long_init = env->GetMethodID(long_class, "<init>", "(J)V");
    jobject timestamp_j = env->NewObject(long_class, long_init, info.getTimestamp());
    env->SetObjectField(serverInfoJ, timestamp_field, timestamp_j);

    jfieldID ip_field = env->GetFieldID(serverInfoClass, "ip", "Ljava/lang/String;");
    jstring ip_j = env->NewStringUTF(info.getIP().data());
    env->SetObjectField(serverInfoJ, ip_field, ip_j);

    jfieldID password_field = env->GetFieldID(serverInfoClass, "password", "Ljava/lang/String;");
    jstring password_j = env->NewStringUTF(info.getPassword().data());
    env->SetObjectField(serverInfoJ, password_field, password_j);

    jfieldID mac_field = env->GetFieldID(serverInfoClass, "mac", "Ljava/lang/String;");
    jstring mac_j = env->NewStringUTF(info.getMac().data());
    env->SetObjectField(serverInfoJ, mac_field, mac_j);
    return serverInfoJ;
}

JNIEXPORT jobjectArray JNICALL Java_javagalileo_GalileoSDK_GetServersOnline
(JNIEnv *env, jobject, jlong instance) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    std::vector<GalileoSDK::ServerInfo> servers = sdk->GetServersOnline();
    jclass serverInfoClass = env->FindClass("javagalileo/models/ServerInfo");
    jobjectArray serversJ = env->NewObjectArray(servers.size(), serverInfoClass, NULL);
    for (int i = 0; i < servers.size(); i++) {
        env->SetObjectArrayElement(serversJ, i, ConvertServerInfo(env, servers.at(i)));
    }
    return serversJ;
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_GetCurrentServer
(JNIEnv *env, jobject, jlong instance) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::ServerInfo* currentServer = sdk->GetCurrentServer();
    if (currentServer == NULL)
        return NULL;
    GalileoSDK::ServerInfo info = *sdk->GetCurrentServer();
    return ConvertServerInfo(env, info);
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_PublishTest
(JNIEnv *env, jobject, jlong instance) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->PublishTest();
    return ConvertGalileoReturnCode(env, res);
}

JNIEXPORT jlong JNICALL Java_javagalileo_GalileoSDK_GetInstance
(JNIEnv *, jclass) {
    GalileoSDK::GalileoSDK* sdk = GalileoSDK::GalileoSDK::GetInstance();
    return (long)sdk;
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_SendCMD
(JNIEnv *env, jobject, jlong instance, jbyteArray cmd) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    jbyte* cmds_j = env->GetByteArrayElements(cmd, NULL);
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->SendCMD((uint8_t*)cmds_j, env->GetArrayLength(cmd));
    return ConvertGalileoReturnCode(env, res);
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_StartNav
(JNIEnv *env, jobject, jlong instance) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->StartNav();
    return ConvertGalileoReturnCode(env, res);
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_StopNav
(JNIEnv *env, jobject, jlong instance) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->StopNav();
    return ConvertGalileoReturnCode(env, res);
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_SetGoal
(JNIEnv *env, jobject, jlong instance, jint goalNum) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->SetGoal(goalNum);
    return ConvertGalileoReturnCode(env, res);
}


JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_PauseGoal
(JNIEnv *env, jobject, jlong instance) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->PauseGoal();
    return ConvertGalileoReturnCode(env, res);
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_ResumeGoal
(JNIEnv *env, jobject, jlong instance) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->ResumeGoal();
    return ConvertGalileoReturnCode(env, res);
}


JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_CancelGoal
(JNIEnv *env, jobject, jlong instance) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->CancelGoal();
    return ConvertGalileoReturnCode(env, res);
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_InsertGoal
(JNIEnv *env, jobject, jlong instance, jfloat x, jfloat y) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->InsertGoal(x, y);
    return ConvertGalileoReturnCode(env, res);
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_ResetGoal
(JNIEnv *env, jobject, jlong instance) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->ResetGoal();
    return ConvertGalileoReturnCode(env, res);
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_SetSpeed
(JNIEnv *env, jobject, jlong instance, jfloat vLinear, jfloat vAngle) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->SetSpeed(vLinear, vAngle);
    return ConvertGalileoReturnCode(env, res);
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_Shutdown
(JNIEnv *env, jobject, jlong instance) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->Shutdown();
    return ConvertGalileoReturnCode(env, res);
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_SetAngle
(JNIEnv *env, jobject, jlong instance, jbyte sign, jbyte angle) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->SetAngle(sign, angle);
    return ConvertGalileoReturnCode(env, res);
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_StartLoop
(JNIEnv *env, jobject, jlong instance) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->StartLoop();
    return ConvertGalileoReturnCode(env, res);
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_StopLoop
(JNIEnv *env, jobject, jlong instance) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->StopLoop();
    return ConvertGalileoReturnCode(env, res);
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_SetLoopWaitTime
(JNIEnv *env, jobject, jlong instance, jbyte time) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->SetLoopWaitTime(time);
    return ConvertGalileoReturnCode(env, res);
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_StartMapping
(JNIEnv *env, jobject, jlong instance) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->StartMapping();
    return ConvertGalileoReturnCode(env, res);
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_StopMapping
(JNIEnv *env, jobject, jlong instance) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->StopMapping();
    return ConvertGalileoReturnCode(env, res);
}


JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_SaveMap
(JNIEnv *env, jobject, jlong instance) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->SaveMap();
    return ConvertGalileoReturnCode(env, res);
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_UpdateMap
(JNIEnv *env, jobject, jlong instance) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->UpdateMap();
    return ConvertGalileoReturnCode(env, res);
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_StartChargeLocal
(JNIEnv *env, jobject, jlong instance) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->StartChargeLocal();
    return ConvertGalileoReturnCode(env, res);
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_StopChargeLocal
(JNIEnv *env, jobject, jlong instance) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->StopChargeLocal();
    return ConvertGalileoReturnCode(env, res);
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_SaveChargeBasePosition
(JNIEnv *env, jobject, jlong instance) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->SaveChargeBasePosition();
    return ConvertGalileoReturnCode(env, res);
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_StartCharge
(JNIEnv *env, jobject, jlong instance, jfloat x, jfloat y) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->StartCharge(x, y);
    return ConvertGalileoReturnCode(env, res);
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_StopCharge
(JNIEnv *env, jobject, jlong instance) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->StopCharge();
    return ConvertGalileoReturnCode(env, res);
}

JNIEXPORT jint JNICALL Java_javagalileo_GalileoSDK_MoveTo
(JNIEnv *env, jobject, jlong instance, jfloat x, jfloat y) {
    uint8_t goalNum_c = -1;
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->MoveTo(x, y, &goalNum_c);
    if (res != GalileoSDK::GALILEO_RETURN_CODE::OK)
        return -1;
    return goalNum_c;
}

JNIEXPORT jint JNICALL Java_javagalileo_GalileoSDK_GetGoalNum
(JNIEnv *env, jobject, jlong instance) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    uint8_t goalNum_c = -1;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->GetGoalNum(&goalNum_c);
    if (res != GalileoSDK::GALILEO_RETURN_CODE::OK)
        return -1;
    return goalNum_c;
}


jobject ConvertGalileoStatus(JNIEnv *env, galileo_serial_server::GalileoStatus status) {
    jclass galileoStatus_class = env->FindClass("javagalileo/models/GalileoStatus");
    jmethodID galileoStatus_init = env->GetMethodID(galileoStatus_class, "<init>", "()V");
    jobject galileoStatusJ = env->NewObject(galileoStatus_class, galileoStatus_init);
    jmethodID setTimestamp_method = env->GetMethodID(galileoStatus_class, "setTimestamp", "(J)V");
    env->CallVoidMethod(galileoStatusJ, setTimestamp_method, status.header.stamp.toNSec() / 1000 / 1000);
    jmethodID setAngleGoalStatus_method = env->GetMethodID(galileoStatus_class, "setAngleGoalStatus", "(I)V");
    env->CallVoidMethod(galileoStatusJ, setAngleGoalStatus_method, status.angleGoalStatus);
    jmethodID setBusyStatus_method = env->GetMethodID(galileoStatus_class, "setBusyStatus", "(I)V");
    env->CallVoidMethod(galileoStatusJ, setBusyStatus_method, status.busyStatus);
    jmethodID setChargeStatus_method = env->GetMethodID(galileoStatus_class, "setChargeStatus", "(I)V");
    env->CallVoidMethod(galileoStatusJ, setChargeStatus_method, status.chargeStatus);
    jmethodID setControlSpeedTheta_method = env->GetMethodID(galileoStatus_class, "setControlSpeedTheta", "(F)V");
    env->CallVoidMethod(galileoStatusJ, setControlSpeedTheta_method, status.controlSpeedTheta);
    jmethodID setControlSpeedX_method = env->GetMethodID(galileoStatus_class, "setControlSpeedX", "(F)V");
    env->CallIntMethod(galileoStatusJ, setControlSpeedX_method, status.controlSpeedX);
    jmethodID setGbaStatus_method = env->GetMethodID(galileoStatus_class, "setGbaStatus", "(I)V");
    env->CallVoidMethod(galileoStatusJ, setGbaStatus_method, status.gbaStatus);
    jmethodID setGcStatus_method = env->GetMethodID(galileoStatus_class, "setGcStatus", "(I)V");
    env->CallVoidMethod(galileoStatusJ, setGcStatus_method, status.gcStatus);
    jmethodID setLoopStatus_method = env->GetMethodID(galileoStatus_class, "setLoopStatus", "(I)V");
    env->CallVoidMethod(galileoStatusJ, setLoopStatus_method, status.loopStatus);
    jmethodID setMapStatus_method = env->GetMethodID(galileoStatus_class, "setMapStatus", "(I)V");
    env->CallVoidMethod(galileoStatusJ, setMapStatus_method, status.mapStatus);
    jmethodID setNavStatus_method = env->GetMethodID(galileoStatus_class, "setNavStatus", "(I)V");
    env->CallIntMethod(galileoStatusJ, setNavStatus_method, status.navStatus);
    jmethodID setPower_method = env->GetMethodID(galileoStatus_class, "setPower", "(F)V");
    env->CallVoidMethod(galileoStatusJ, setPower_method, status.power);
    jmethodID setTargetDistance_method = env->GetMethodID(galileoStatus_class, "setTargetDistance", "(F)V");
    env->CallVoidMethod(galileoStatusJ, setTargetDistance_method, status.targetDistance);
    jmethodID setTargetNumID_method = env->GetMethodID(galileoStatus_class, "setTargetNumID", "(I)V");
    env->CallVoidMethod(galileoStatusJ, setTargetNumID_method, status.targetNumID);
    jmethodID setTargetStatus_method = env->GetMethodID(galileoStatus_class, "setTargetStatus", "(I)V");
    env->CallVoidMethod(galileoStatusJ, setTargetStatus_method, status.targetStatus);
    jmethodID setVisualStatus_method = env->GetMethodID(galileoStatus_class, "setVisualStatus", "(I)V");
    env->CallVoidMethod(galileoStatusJ, setVisualStatus_method, status.visualStatus);
    jmethodID setCurrentAngle_method = env->GetMethodID(galileoStatus_class, "setCurrentAngle", "(F)V");
    env->CallVoidMethod(galileoStatusJ, setCurrentAngle_method, status.currentAngle);
    jmethodID setCurrentPoseY_method = env->GetMethodID(galileoStatus_class, "setCurrentPoseY", "(F)V");
    env->CallVoidMethod(galileoStatusJ, setCurrentPoseY_method, status.currentPosY);
    jmethodID setCurrentPoseX_method = env->GetMethodID(galileoStatus_class, "setCurrentPoseX", "(F)V");
    env->CallVoidMethod(galileoStatusJ, setCurrentPoseX_method, status.currentPosX);
    jmethodID setCurrentSpeedTheta_method = env->GetMethodID(galileoStatus_class, "setCurrentSpeedTheta", "(F)V");
    env->CallVoidMethod(galileoStatusJ, setCurrentSpeedTheta_method, status.currentSpeedTheta);
    jmethodID setCurrentSpeedX_method = env->GetMethodID(galileoStatus_class, "setCurrentSpeedX", "(F)V");
    env->CallVoidMethod(galileoStatusJ, setCurrentSpeedX_method, status.currentSpeedX);
    return galileoStatusJ;
}


JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_GetCurrentStatus
(JNIEnv *env , jobject, jlong instance) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    galileo_serial_server::GalileoStatus status;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->GetCurrentStatus(&status);
    if (res != GalileoSDK::OK)
        return NULL;
    return ConvertGalileoStatus(env, status);
}


jobject OnStatusUpdateCB;

JNIEXPORT void JNICALL Java_javagalileo_GalileoSDK_SetCurrentStatusCallback
(JNIEnv *env, jobject, jlong instance, jobject onStatusUpdate) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    env->GetJavaVM(&jvm);
    // set referance to callbacks
    OnStatusUpdateCB = env->NewGlobalRef(onStatusUpdate);

    // parse callback
    jmethodID OnUpdateID = NULL;
    if (!env->IsSameObject(onStatusUpdate, NULL)) {
        jclass OnUpdateClass = env->GetObjectClass(onStatusUpdate);
        OnUpdateID = env->GetMethodID(OnUpdateClass, "OnStatusUpdateEvent",
            "(Ljavagalileo/models/ServerInfo$GALILEO_RETURN_CODE;Ljavagalileo/models/GalileoStatus;)V");
    }

    void(*OnUpdateTmp)(GalileoSDK::GALILEO_RETURN_CODE, galileo_serial_server::GalileoStatus status);
    OnUpdateTmp = NULL;

    if (NULL != OnUpdateID) {
        OnUpdateTmp = [](GalileoSDK::GALILEO_RETURN_CODE code, galileo_serial_server::GalileoStatus status) {
            JNIEnv* menv;
            jvm->AttachCurrentThread((JNIEnv**)&menv, NULL);
            jclass OnUpdateClass = menv->GetObjectClass(OnStatusUpdateCB);
            jmethodID OnUpdateID = menv->GetMethodID(OnUpdateClass, "OnStatusUpdated",
                "(Ljavagalileo/models/ServerInfo$GALILEO_RETURN_CODE;Ljavagalileo/models/GalileoStatus;)V");
            if (NULL != OnUpdateID) {
                jobject codeJ = ConvertGalileoReturnCode(menv, code);
                jobject statusJ = ConvertGalileoStatus(menv, status);
                menv->CallVoidMethod(OnStatusUpdateCB, OnUpdateID, codeJ, statusJ);
            }
            jvm->DetachCurrentThread();
        };
    }

    sdk->SetCurrentStatusCallback(OnUpdateTmp);
}

jobject OnGoalReachedCB;
JNIEXPORT void JNICALL Java_javagalileo_GalileoSDK_SetGoalReachedCallback
(JNIEnv *env, jobject, jlong instance, jobject onGoalReached) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    env->GetJavaVM(&jvm);
    // set referance to callbacks
    OnGoalReachedCB = env->NewGlobalRef(onGoalReached);

    // parse callback
    jmethodID OnGoalReachedID = NULL;
    if (!env->IsSameObject(onGoalReached, NULL)) {
        jclass OnGoalReachedClass = env->GetObjectClass(onGoalReached);
        OnGoalReachedID = env->GetMethodID(OnGoalReachedClass, "OnGoalReached",
            "(ILjavagalileo/models/GalileoStatus;)V");
    }

    void(*OnGoalReachedTmp)(int goalIndex, galileo_serial_server::GalileoStatus status);
    OnGoalReachedTmp = NULL;

    if (NULL != OnGoalReachedID) {
        OnGoalReachedTmp = [](int goalIndex, galileo_serial_server::GalileoStatus status) {
            JNIEnv* menv;
            jvm->AttachCurrentThread((JNIEnv**)&menv, NULL);
            jclass OnGoalReachedClass = menv->GetObjectClass(OnGoalReachedCB);
            jmethodID OnUpdateID = menv->GetMethodID(OnGoalReachedClass, "OnGoalReached",
                "(ILjavagalileo/models/GalileoStatus;)V");
            if (NULL != OnUpdateID) {
                jobject statusJ = ConvertGalileoStatus(menv, status);
                menv->CallVoidMethod(OnGoalReachedCB, OnUpdateID, goalIndex, statusJ);
            }
            jvm->DetachCurrentThread();
        };
    }

    sdk->SetGoalReachedCallback(OnGoalReachedTmp);
}

JNIEXPORT jobject JNICALL Java_javagalileo_GalileoSDK_WaitForGoal
(JNIEnv *env, jobject, jlong instance, jint goalIndex) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    GalileoSDK::GALILEO_RETURN_CODE res = sdk->WaitForGoal(goalIndex);
    return ConvertGalileoReturnCode(env, res);
}

JNIEXPORT void JNICALL Java_javagalileo_GalileoSDK_Dispose
(JNIEnv *env, jobject, jlong instance) {
    GalileoSDK::GalileoSDK* sdk = (GalileoSDK::GalileoSDK*)instance;
    sdk->Dispose();
}
