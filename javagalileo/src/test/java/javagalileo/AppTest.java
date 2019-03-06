package javagalileo;

import org.junit.Test;

import javagalileo.listeners.OnConnectEventListener;
import javagalileo.listeners.OnDisconnectEventListener;
import javagalileo.models.ServerInfo.GALILEO_RETURN_CODE;

import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test.
     */
    @Test
    public void testApp() {
        assertTrue(true);
    }

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
        sdk.Dispose();
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
        while (!connected[0] && timecount < 10 * 1000) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timecount += 1000;
        }
        assertTrue(connected[0]);
        sdk.Dispose();
    }

    @Test
    public void testConnectTimeout() {
        GalileoSDK sdk = new GalileoSDK();
        GALILEO_RETURN_CODE res = sdk.Connect(
                "71329A5B0F2D68364BB7B44F3F125531E4C7F5BC3BCE2694DFE39B505FF9C730A614FF2790C1", true, 10, null,
                null);
        assertEquals(GALILEO_RETURN_CODE.NO_SERVER_FOUND, res);
    }
}
