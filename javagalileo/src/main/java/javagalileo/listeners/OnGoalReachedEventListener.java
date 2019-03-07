package javagalileo.listeners;

import javagalileo.models.GalileoStatus;

public interface OnGoalReachedEventListener {
    public void OnGoalReached(int goalIndex, GalileoStatus status);
}
