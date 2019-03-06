package javagalileo.listeners;

import javagalileo.models.ServerInfo.GALILEO_RETURN_CODE;

public interface OnDisconnectEventListener {
    public void OnDisconnected(GALILEO_RETURN_CODE status, String id);
}
