package javagalileo;

import javagalileo.listeners.OnConnectEventListener;
import javagalileo.listeners.OnDisconnectEventListener;
import javagalileo.listeners.OnGoalReachedEventListener;
import javagalileo.listeners.OnStatusUpdateEventListener;
import javagalileo.models.ServerInfo;
import javagalileo.models.ServerInfo.GALILEO_RETURN_CODE;

public class GalileoSDK { // Save as HelloJNI.java
    static {
        System.loadLibrary("GalileoSDK");
        System.loadLibrary("JNIGalileoWrapper");
    }

    private native void sayHello();

    private native long CreateInstance();

    private native void ReleaseInstance(long instance);

    private native GALILEO_RETURN_CODE Connect(long instance, String targetID, boolean auto_connect, int timeout,
            OnConnectEventListener onConnect, OnDisconnectEventListener onDisconnect);

    private native ServerInfo[] GetServersOnline();

    private native ServerInfo GetCurrentServer();

    private native GALILEO_RETURN_CODE PublishTest(long instance);

    private native long GetInstance();

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

    private native GALILEO_RETURN_CODE MoveTo(long instance, float x, float y, byte goalNum);

    private native GALILEO_RETURN_CODE GetGoalNum(long instance, byte goalNum);

    private native GALILEO_RETURN_CODE GetCurrentStatus(long instance);

    private native GALILEO_RETURN_CODE SetCurrentStatusCallback(long instance,
            OnStatusUpdateEventListener onStatusUpdate);

    private native GALILEO_RETURN_CODE SetGoalReachedCallback(long instance, OnGoalReachedEventListener onGoalReached);

    private native GALILEO_RETURN_CODE WaitForGoal(long instance, int goalIndex);

    private long instance;

    public GalileoSDK() {
        instance = CreateInstance();
    }

    public void Dispose() {
        ReleaseInstance(instance);
    }

    public GALILEO_RETURN_CODE Connect(String targetID, boolean auto_connect, int timeout,
            OnConnectEventListener onConnect, OnDisconnectEventListener onDisconnect) {
        return Connect(instance, targetID, auto_connect, timeout, onConnect, onDisconnect);
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
        sdk.Dispose();
        System.out.println("Released");
        sdk = null;
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
