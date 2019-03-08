package javagalileo;

import org.junit.Test;

import javagalileo.listeners.OnConnectEventListener;
import javagalileo.listeners.OnDisconnectEventListener;
import javagalileo.models.GalileoStatus;
import javagalileo.models.ServerInfo;
import javagalileo.models.ServerInfo.GALILEO_RETURN_CODE;

import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void testCreateRelease() {
        System.out.println("Create new SDK instance");
        GalileoSDK sdk = new GalileoSDK();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Release SDK instance");
        sdk.Release();
        sdk = null;
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testConnectCallback() {
        GalileoSDK sdk = new GalileoSDK();
        final boolean[] connected = { false };
        GALILEO_RETURN_CODE res = sdk.Connect(
                "71329A5B0F2D68364BB7B44F3F125531E4C7F5BC3BCE2694DFE39B505FF9C730A614FF2790C1", true, 10000,
                new OnConnectEventListener() {
                    @Override
                    public void OnConnected(GALILEO_RETURN_CODE status, String id) {
                        System.out.println("OnConnected: " + id);
                        System.out.println("Status: " + status);
                        assertEquals(GALILEO_RETURN_CODE.OK, status);
                        connected[0] = true;
                    }
                }, new OnDisconnectEventListener() {

                    @Override
                    public void OnDisconnected(GALILEO_RETURN_CODE status, String id) {
                        System.out.println("OnDisconnected: " + id);
                        System.out.println("Status: " + status);
                        assertTrue(true);
                    }
                });
        assertEquals(GALILEO_RETURN_CODE.OK, res);
        int timecount = 0;
        while (!connected[0] && timecount < 5 * 1000) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timecount += 1000;
            System.out.println(timecount / 1000);
        }
        sdk.Release();
        assertTrue(connected[0]);
    }

    @Test
    public void testConnectCallbackTimeout() {
        GalileoSDK sdk = new GalileoSDK();
        final boolean[] connected = { false };
        GALILEO_RETURN_CODE res = sdk.Connect(
                "71329A5B0F2D68364BB7B44F3F125531E4C7F5BC3BCE2694DFE39B505FF9C730A614FF2790C1", true, 10000,
                new OnConnectEventListener() {
                    @Override
                    public void OnConnected(GALILEO_RETURN_CODE status, String id) {
                        System.out.println("OnConnected: " + id);
                        System.out.println("Status: " + status);
                        assertEquals(GALILEO_RETURN_CODE.NO_SERVER_FOUND, status);
                        connected[0] = true;
                    }
                }, new OnDisconnectEventListener() {

                    @Override
                    public void OnDisconnected(GALILEO_RETURN_CODE status, String id) {
                        System.out.println("OnDisconnected: " + id);
                        System.out.println("Status: " + status);
                        assertTrue(true);
                    }
                });
        assertEquals(GALILEO_RETURN_CODE.OK, res);
        int timecount = 0;
        while (!connected[0] && timecount < 15 * 1000) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timecount += 1000;
        }
        sdk.Release();
        assertTrue(connected[0]);
    }

    @Test
    public void testConnectTimeout() {
        GalileoSDK sdk = new GalileoSDK();
        GALILEO_RETURN_CODE res = sdk.Connect(
                "71329A5B0F2D68364BB7B44F3F125531E4C7F5BC3BCE2694DFE39B505FF9C730A614FF2790C1", true, 10, null, null);
        assertEquals(GALILEO_RETURN_CODE.NO_SERVER_FOUND, res);
        sdk.Release();
    }

    @Test
    public void testPub() {
        GalileoSDK sdk = new GalileoSDK();
        int timecount = 0;
        while (timecount < 20 * 1000) {
            ServerInfo[] servers = sdk.GetServersOnline();
            if (servers.length == 0)
                System.out.println("No Server Found");
            for (ServerInfo server : servers) {
                System.out.println("ID: " + server.getID());
                sdk.Connect("", true, 10000, null, null);
            }
            sdk.PublishTest();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timecount += 1000;
        }
        sdk.Release();
    }

    @Test
    public void testGetServersOnline() {
        GalileoSDK sdk = new GalileoSDK();
        int timecount = 0;
        while (timecount < 20 * 1000) {
            ServerInfo[] servers = sdk.GetServersOnline();
            if (servers.length == 0)
                System.out.println("No Server Found");
            for (ServerInfo server : servers) {
                System.out.println("Found Server ID: " + server.getID());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timecount += 1000;
        }
        sdk.Release();
    }

    @Test
    public void testGetCurrentServer() {
        GalileoSDK sdk = new GalileoSDK();
        int timecount = 0;
        while (timecount < 20 * 1000) {
            ServerInfo[] servers = sdk.GetServersOnline();
            if (servers.length == 0)
                System.out.println("No Server Found");
            for (ServerInfo server : servers) {
                System.out.println("Found Server ID: " + server.getID());
                sdk.Connect("", true, 10000, null, null);
            }
            if (sdk.GetCurrentServer() != null) {
                System.out.println("Current Server: " + sdk.GetCurrentServer().getID());
                break;
            } else {
                System.out.println("Current Server is null");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timecount += 1000;
        }
        sdk.Release();
    }

    @Test
    public void testSub() {
        GalileoSDK sdk = new GalileoSDK();
        sdk.Connect("", true, 10000, null, null);
        int timecout = 0;
        while (timecout < 10 * 1000) {
            GalileoStatus status = sdk.GetCurrentStatus();
            if (status == null) {
                System.out.print("Get status failed");
            } else {
                System.out.println("Power: " + status.getPower());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timecout += 1000;
        }
        sdk.Release();
    }

    @Test
    public void testReconnect() {
        boolean[] flags = { false, false }; // connect callback flag and connected flag
        GalileoSDK sdk = new GalileoSDK();
        int timecount = 0;
        while (timecount < 120 * 1000) {
            // not connected
            if (!flags[1]) {
                System.out.println("Start connect");
                GALILEO_RETURN_CODE res = sdk.Connect("", true, 10000, new OnConnectEventListener() {

                    @Override
                    public void OnConnected(GALILEO_RETURN_CODE status, String id) {
                        System.out.println("OnConnected: result " + status);
                        System.out.println("OnConnected: connect to " + id);
                        // connect_callback_flag true
                        flags[0] = true;
                        if (status == GALILEO_RETURN_CODE.OK) {
                            // connected_flag true
                            flags[1] = true;
                        }
                    }
                }, new OnDisconnectEventListener() {

                    @Override
                    public void OnDisconnected(GALILEO_RETURN_CODE status, String id) {
                        System.out.println("OnDisconnected: result " + status);
                        System.out.println("OnDisconnected: connect to " + id);
                        // connected flag false
                        flags[1] = false;
                    }
                });
                System.out.println("Connect Res: " + res);
                // callback not reached
                while (!flags[0]) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Wait for callback");
                    timecount += 1000;
                    if (timecount >= 120 * 1000) {
                        System.out.println("Timeout break");
                        break;
                    }
                }
                flags[0] = false;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timecount += 1000;
            System.out.println("Waitting");
        }
        sdk.Release();
    }

    @Test
    public void testDisposedAndConnect() {
        GalileoSDK sdk = new GalileoSDK();
        sdk.Connect("", true, 10000, new OnConnectEventListener() {

            @Override
            public void OnConnected(GALILEO_RETURN_CODE status, String id) {
                System.out.println("Connect callback1");
            }
        }, new OnDisconnectEventListener() {

            @Override
            public void OnDisconnected(GALILEO_RETURN_CODE status, String id) {
                System.out.println("Disconnect callback1");
            }
        });
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sdk.Dispose();
        System.out.print("Connect again: ");
        GALILEO_RETURN_CODE res = sdk.Connect("", true, 10000, new OnConnectEventListener() {

            @Override
            public void OnConnected(GALILEO_RETURN_CODE status, String id) {
                System.out.println("Connect callback2");
            }
        }, new OnDisconnectEventListener() {

            @Override
            public void OnDisconnected(GALILEO_RETURN_CODE status, String id) {
                System.out.println("Disconnect callback2");
            }
        });
        System.out.println(res);
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sdk.Release();
    }

    @Test
    public void testSendGalileoCmd() {
        GalileoSDK sdk = new GalileoSDK();
        if (sdk.Connect("", true, 10000, null, null) != GALILEO_RETURN_CODE.OK) {
            System.out.println("Connect to server failed");
        }
        int timecout = 0;
        while (timecout < 10 * 1000) {
            sdk.SendCMD(new byte[] { 0x01, 0x01, 0x01, 0x01, 0x01, 0x01 });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timecout += 1000;
        }
    }

    @Test
    public void testNav() {
        GalileoSDK sdk = new GalileoSDK();
        if (sdk.Connect("", true, 10000, null, null) != GALILEO_RETURN_CODE.OK) {
            System.out.println("Connect to server failed");
        }
        GalileoStatus status = sdk.GetCurrentStatus();
        System.out.println("status: " + status.getNavStatus());
        System.out.println("Start nav");
        sdk.StartNav();
        try {
            Thread.sleep(20 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        status = sdk.GetCurrentStatus();
        System.out.println("status: " + status.getNavStatus());
        System.out.println("Stop nav");
        sdk.StopNav();
        try {
            Thread.sleep(20 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        status = sdk.GetCurrentStatus();
        System.out.println("status: " + status.getNavStatus());
        sdk.Release();
    }

    @Test
    public void testSetSpeed() {
        GalileoSDK sdk = new GalileoSDK();
        if (sdk.Connect("71329A5B0F2D68364BB7B44F3F125531E4C7F5BC3BCE2694DFE39B505FF9C730A614FF2790C1", true, 10000,
                null, null) != GALILEO_RETURN_CODE.OK) {
            System.out.println("Connect to server failed");
        }
        GalileoStatus status = sdk.GetCurrentStatus();
        System.out.println("currentSpeedX: " + status.getCurrentSpeedX());
        System.out.println("currentSpeedTheta: " + status.getCurrentSpeedTheta());
        sdk.SetSpeed(0.1f, 0.0f);
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("currentSpeedX: " + status.getCurrentSpeedX());
        System.out.println("currentSpeedTheta: " + status.getCurrentSpeedTheta());
        sdk.SetSpeed(0.0f, 1f);
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("currentSpeedX: " + status.getCurrentSpeedX());
        System.out.println("currentSpeedTheta: " + status.getCurrentSpeedTheta());
        sdk.SetSpeed(-0.1f, 0.0f);
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("currentSpeedX: " + status.getCurrentSpeedX());
        System.out.println("currentSpeedTheta: " + status.getCurrentSpeedTheta());
        sdk.SetSpeed(0.1f, -1f);
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("currentSpeedX: " + status.getCurrentSpeedX());
        System.out.println("currentSpeedTheta: " + status.getCurrentSpeedTheta());
        sdk.SetSpeed(0.0f, 0.0f);
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sdk.Release();
    }

    @Test
    public void testGoals() {
        GalileoSDK sdk = new GalileoSDK();
        if (sdk.Connect("71329A5B0F2D68364BB7B44F3F125531E4C7F5BC3BCE2694DFE39B505FF9C730A614FF2790C1", true, 10000,
                null, null) != GALILEO_RETURN_CODE.OK) {
            System.out.println("Connect to server failed");
        }
        sdk.StartNav();
        GalileoStatus status = sdk.GetCurrentStatus();
        while (status.getVisualStatus() != 1 || status.getNavStatus() != 1) {
            System.out.println("Wait for navigation initialization");
            status = sdk.GetCurrentStatus();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        sdk.SetGoal(0);
        // wait for goal status
        status = sdk.GetCurrentStatus();
        while(status.getTargetStatus() != 1){
            System.out.println("Wait for goal start");
            status = sdk.GetCurrentStatus();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Goal started");

        // pause target
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sdk.PauseGoal();
        status = sdk.GetCurrentStatus();
        while(status.getTargetStatus() != 2){
            System.out.println("Wait for goal pause");
            status = sdk.GetCurrentStatus();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Goal paused");

        // goal resume
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sdk.ResumeGoal();
        status = sdk.GetCurrentStatus();
        while (status.getTargetStatus() != 1) {
            System.out.println("Wait for goal resume");
            status = sdk.GetCurrentStatus();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Goal resumed");

        // cancel goal
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sdk.CancelGoal();
        status = sdk.GetCurrentStatus();
        while (status.getTargetStatus() != 0 || status.getTargetNumID() != -1) {
            System.out.println("Wait for goal cancel");
            status = sdk.GetCurrentStatus();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Goal cancelled");

        // set goal again
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Set goal again");
        sdk.SetGoal(0);

        // wait goal complete
        status = sdk.GetCurrentStatus();
        while(status.getTargetStatus() != 1){
            System.out.println("Wait goal start");
            status = sdk.GetCurrentStatus();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Goal started");
        while(status.getTargetStatus() != 0 || status.getTargetNumID() != 0){
            System.out.println("Wait goal complete");
            status = sdk.GetCurrentStatus();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // move to target location
        // get current location
        status = sdk.GetCurrentStatus();
        float pos0_x = status.getCurrentPoseX();
        float pos0_y = status.getCurrentPoseY();
        // set goal again, move to #1 goal
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Set Goal again");
        sdk.SetGoal(1);
        // wait goal complete
        status = sdk.GetCurrentStatus();
        while(status.getTargetStatus() !=1){
            System.out.println("Wait for goal start");
            status = sdk.GetCurrentStatus();
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Goal started");
        while(status.getTargetStatus() != 0 || status.getTargetNumID() != 1){
            System.out.println("Wait for goal complete");
            status = sdk.GetCurrentStatus();
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // get location again
        status = sdk.GetCurrentStatus();
        float pos1_x = status.getCurrentPoseX();
        float pos1_y = status.getCurrentPoseY();
        // move to point between #0 and #1
        System.out.println("Start move to " + (pos0_x + pos1_x) / 2 + " " + (pos0_y + pos1_y) / 2);
        int goalNum = sdk.MoveTo((pos0_x + pos1_x) / 2,
                         (pos0_y + pos1_y) / 2);
        // wait goal complete
        status = sdk.GetCurrentStatus();
        while(status.getTargetStatus() != 1){
            System.out.println("Wait for goal start");
            status = sdk.GetCurrentStatus();
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Goal started");
        while(status.getTargetStatus() !=0 || status.getTargetNumID() != goalNum){
            System.out.println("Wait for goal reached");
            status = sdk.GetCurrentStatus();
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Move to " + (pos0_x + pos1_x) / 2 + " " + (pos0_y + pos1_y) / 2 + " complete");
        System.out.println("All complete");
        sdk.Release();
    }
}
