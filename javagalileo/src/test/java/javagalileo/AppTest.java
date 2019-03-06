package javagalileo;

import org.junit.Test;

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
        // sdk = null;
        // try {
        //     Thread.sleep(5000);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
    }
}
