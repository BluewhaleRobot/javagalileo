package javagalileo.listeners;
import javagalileo.models.ServerInfo.GALILEO_RETURN_CODE;

public interface OnConnectEventListener {
    public void OnConnected(GALILEO_RETURN_CODE status, String id);
}
