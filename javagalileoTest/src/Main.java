import javagalileo.GalileoSDK;
import javagalileo.models.ServerInfo;

public class Main {

    public static void main(String[] args) {
        GalileoSDK sdk = new GalileoSDK();
        int timecount = 0;
        while (timecount < 10 * 1000){
            ServerInfo[] servers = sdk.GetServersOnline();
            if(servers.length == 0){
                System.out.println("No server found");
            }
            for(ServerInfo server:servers){
               System.out.println("ID: " + server.getID());
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
}
