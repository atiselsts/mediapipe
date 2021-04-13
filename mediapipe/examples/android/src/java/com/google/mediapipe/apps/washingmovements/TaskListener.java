package com.google.mediapipe.apps.washingmovements;

/**
 * An interface that can be used by the NotificationThread class to notify an
 * object that a thread has completed. 
 * @author Greg Cope
 */
public interface TaskListener {
    /**
     * Notifies this object that the Runnable object has completed its work. 
     * @param runner The runnable interface whose work has finished.
     */
    public void threadComplete( Runnable runner );
}
