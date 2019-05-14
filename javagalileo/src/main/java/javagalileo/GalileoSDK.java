package javagalileo;

import java.io.IOException;

import javagalileo.listeners.OnConnectEventListener;
import javagalileo.listeners.OnDisconnectEventListener;
import javagalileo.listeners.OnGoalReachedEventListener;
import javagalileo.listeners.OnStatusUpdateEventListener;
import javagalileo.models.ServerInfo;
import javagalileo.models.ServerInfo.GALILEO_RETURN_CODE;
import javagalileo.utils.NativeUtils;
import javagalileo.models.GalileoStatus;;

public class GalileoSDK { // Save as HelloJNI.java
    static {
        try {
            System.loadLibrary("GalileoSDK");
            System.loadLibrary("JNIGalileoWrapper");
        } catch (UnsatisfiedLinkError e) {
            try {
                NativeUtils.loadLibraryFromJar("/" + System.mapLibraryName("GalileoSDK"));
                NativeUtils.loadLibraryFromJar("/" + System.mapLibraryName("JNIGalileoWrapper"));
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
        }
    }

    private native void sayHello();

    private native long CreateInstance();

    private native void ReleaseInstance(long instance);

    private native GALILEO_RETURN_CODE Connect(long instance, String targetID, boolean auto_connect, int timeout,
            OnConnectEventListener onConnect, OnDisconnectEventListener onDisconnect);

    private native GALILEO_RETURN_CODE ConnectIOT(long instance, String targetID, int timeout, String password,
            OnConnectEventListener onConnect, OnDisconnectEventListener onDisconnect);

    private native ServerInfo[] GetServersOnline(long instance);

    private native ServerInfo GetCurrentServer(long instance);

    private native GALILEO_RETURN_CODE PublishTest(long instance);

    public static native long GetInstance();

    private native GALILEO_RETURN_CODE SendCMD(long instance, byte[] cmd);

    private native GALILEO_RETURN_CODE StartNav(long instance);

    private native GALILEO_RETURN_CODE StopNav(long instance);

    private native GALILEO_RETURN_CODE SetGoal(long instance, int goalIndex);

    private native GALILEO_RETURN_CODE PauseGoal(long instance);

    private native GALILEO_RETURN_CODE ResumeGoal(long instance);

    private native GALILEO_RETURN_CODE CancelGoal(long instance);

    private native GALILEO_RETURN_CODE InsertGoal(long instance, float x, float y);

    private native GALILEO_RETURN_CODE ResetGoal(long instance);

    private native GALILEO_RETURN_CODE SetSpeed(long instance, float vLinear, float vAngle);

    private native GALILEO_RETURN_CODE Shutdown(long instance);

    private native GALILEO_RETURN_CODE SetAngle(long instance, byte sign, byte angle);

    private native GALILEO_RETURN_CODE StartLoop(long instance);

    private native GALILEO_RETURN_CODE StopLoop(long instance);

    private native GALILEO_RETURN_CODE SetLoopWaitTime(long instance, byte time);

    private native GALILEO_RETURN_CODE StartMapping(long instance);

    private native GALILEO_RETURN_CODE StopMapping(long instance);

    private native GALILEO_RETURN_CODE SaveMap(long instance);

    private native GALILEO_RETURN_CODE UpdateMap(long instance);

    private native GALILEO_RETURN_CODE StartChargeLocal(long instance);

    private native GALILEO_RETURN_CODE StopChargeLocal(long instance);

    private native GALILEO_RETURN_CODE SaveChargeBasePosition(long instance);

    private native GALILEO_RETURN_CODE StartCharge(long instance, float x, float y);

    private native GALILEO_RETURN_CODE StopCharge(long instance);

    private native int MoveTo(long instance, float x, float y);

    private native int GetGoalNum(long instance);

    private native GalileoStatus GetCurrentStatus(long instance);

    private native void SetCurrentStatusCallback(long instance, OnStatusUpdateEventListener onStatusUpdate);

    private native void SetGoalReachedCallback(long instance, OnGoalReachedEventListener onGoalReached);

    private native GALILEO_RETURN_CODE WaitForGoal(long instance, int goalIndex);

    private native GALILEO_RETURN_CODE SendAudio(long instance, String audio);

    private native GALILEO_RETURN_CODE SendRawAudio(long instance, byte[] audioData);

    private native GALILEO_RETURN_CODE EnableGreeting(long instance, boolean flag);

    private native boolean CheckServerOnline(long instance, String targetID);

    private native void Dispose(long instance);

    private static long instance;

    public GalileoSDK() {
        instance = CreateInstance();
    }

    public void Release() {
        ReleaseInstance(instance);
    }

    public void Dispose() {
        Dispose(instance);
    }

    public GALILEO_RETURN_CODE Connect(String targetID, boolean auto_connect, int timeout,
            OnConnectEventListener onConnect, OnDisconnectEventListener onDisconnect) {
        return Connect(instance, targetID, auto_connect, timeout, onConnect, onDisconnect);
    }

    public GALILEO_RETURN_CODE ConnectIOT(String targetID, int timeout, String password,
            OnConnectEventListener onConnect, OnDisconnectEventListener onDisconnect) {
        return ConnectIOT(instance, targetID, timeout, password, onConnect, onDisconnect);
    }

    public ServerInfo[] GetServersOnline() {
        return GetServersOnline(instance);
    }

    public ServerInfo GetCurrentServer() {
        return GetCurrentServer(instance);
    }

    public GALILEO_RETURN_CODE PublishTest() {
        return PublishTest(instance);
    }

    public GALILEO_RETURN_CODE SendCMD(byte[] cmd) {
        return SendCMD(instance, cmd);
    }

    public GALILEO_RETURN_CODE StartNav() {
        return StartNav(instance);
    }

    public GALILEO_RETURN_CODE StopNav() {
        return StopNav(instance);
    }

    public GALILEO_RETURN_CODE SetGoal(int goalIndex) {
        return SetGoal(instance, goalIndex);
    }

    public GALILEO_RETURN_CODE PauseGoal() {
        return PauseGoal(instance);
    }

    public GALILEO_RETURN_CODE ResumeGoal() {
        return ResumeGoal(instance);
    }

    public GALILEO_RETURN_CODE CancelGoal() {
        return CancelGoal(instance);
    }

    public GALILEO_RETURN_CODE InsertGoal(float x, float y) {
        return InsertGoal(instance, x, y);
    }

    public GALILEO_RETURN_CODE ResetGoal() {
        return ResetGoal(instance);
    }

    public GALILEO_RETURN_CODE SetSpeed(float vLinear, float vAngle) {
        return SetSpeed(instance, vLinear, vAngle);
    }

    public GALILEO_RETURN_CODE Shutdown() {
        return Shutdown(instance);
    }

    public GALILEO_RETURN_CODE SetAngle(byte sign, byte angle) {
        return SetAngle(instance, sign, angle);
    }

    public GALILEO_RETURN_CODE StartLoop() {
        return StartLoop(instance);
    }

    public GALILEO_RETURN_CODE StopLoop() {
        return StopLoop(instance);
    }

    public GALILEO_RETURN_CODE SetLoopWaitTime(byte time) {
        return SetLoopWaitTime(instance, time);
    }

    public GALILEO_RETURN_CODE StartMapping() {
        return StartMapping(instance);
    }

    public GALILEO_RETURN_CODE StopMapping() {
        return StopMapping(instance);
    }

    public GALILEO_RETURN_CODE SaveMap() {
        return SaveMap(instance);
    }

    public GALILEO_RETURN_CODE UpdateMap() {
        return UpdateMap(instance);
    }

    public GALILEO_RETURN_CODE StartChargeLocal() {
        return StartChargeLocal(instance);
    }

    public GALILEO_RETURN_CODE StopChargeLocal() {
        return StopChargeLocal(instance);
    }

    public GALILEO_RETURN_CODE SaveChargeBasePosition() {
        return SaveChargeBasePosition(instance);
    }

    public GALILEO_RETURN_CODE StartCharge(float x, float y) {
        return StartCharge(instance, x, y);
    }

    public GALILEO_RETURN_CODE StopCharge() {
        return StopCharge(instance);
    }

    public int MoveTo(float x, float y) {
        return MoveTo(instance, x, y);
    }

    public int GetGoalNum() {
        return GetGoalNum(instance);
    }

    public GalileoStatus GetCurrentStatus() {
        return GetCurrentStatus(instance);
    }

    public void SetCurrentStatusCallback(OnStatusUpdateEventListener onStatusUpdate) {
        SetCurrentStatusCallback(instance, onStatusUpdate);
    }

    public void SetGoalReachedCallback(OnGoalReachedEventListener onGoalReached) {
        SetGoalReachedCallback(instance, onGoalReached);
    }

    public GALILEO_RETURN_CODE WaitForGoal(int goalIndex) {
        return WaitForGoal(instance, goalIndex);
    }

    public GALILEO_RETURN_CODE SendAudio(String audio){
        return SendAudio(instance, audio);
    }

    public GALILEO_RETURN_CODE SendRawAudio(byte[] audioData){
        return SendRawAudio(instance, audioData);
    }

    public GALILEO_RETURN_CODE EnableGreeting(boolean flag){
        return EnableGreeting(instance, flag);
    }

    public boolean CheckServerOnline(String targetID){
        return CheckServerOnline(instance, targetID);
    }

    public static void main(String[] args) {
        System.out.println("Create new SDK instance");
        GalileoSDK sdk = new GalileoSDK();
        System.out.println("Created");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Release SDK instance");
        sdk.Release();
        System.out.println("Released");
        sdk = null;
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
