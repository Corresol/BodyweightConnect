package com.statletics.bodyweightconnect.exception;

/**
 * Created by Tonni on 11.07.2016.
 */
public class NoDeviceException extends Exception {

    public NoDeviceException() {
        super();
    }

    public NoDeviceException(String detailMessage) {
        super(detailMessage);
    }

    public NoDeviceException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NoDeviceException(Throwable throwable) {
        super(throwable);
    }
}