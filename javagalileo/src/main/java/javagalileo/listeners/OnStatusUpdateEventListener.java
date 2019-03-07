package javagalileo.listeners;

import javagalileo.models.GalileoStatus;
import javagalileo.models.ServerInfo.GALILEO_RETURN_CODE;

public interface OnStatusUpdateEventListener {
    public void OnStatusUpdated(GALILEO_RETURN_CODE code, GalileoStatus status);
}
