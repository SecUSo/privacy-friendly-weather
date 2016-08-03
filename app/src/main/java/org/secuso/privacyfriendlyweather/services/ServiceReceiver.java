package org.secuso.privacyfriendlyweather.services;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * This is a generic receiver to interact with services like the CreateDatabaseService.
 */
@SuppressLint("ParcelCreator")
public class ServiceReceiver extends ResultReceiver {

    /**
     * Member variables
     */
    private Receiver receiver;

    /**
     * @see ResultReceiver#ResultReceiver(Handler)
     */
    public ServiceReceiver(Handler handler) {
        super(handler);
    }

    /**
     * @param receiver The receiver to set
     */
    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    /**
     * Defines the event interface for communication.
     */
    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }

    /**
     * @see ResultReceiver#onReceiveResult(int, Bundle)
     */
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (receiver != null) {
            receiver.onReceiveResult(resultCode, resultData);
        }
    }

}
