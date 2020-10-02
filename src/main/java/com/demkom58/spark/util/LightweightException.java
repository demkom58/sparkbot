package com.demkom58.spark.util;

public class LightweightException extends Exception {
    public LightweightException() {
        this(null, null);
    }

    public LightweightException(String message) {
        this(message, null);
    }

    public LightweightException(Throwable cause) {
        this(null, cause);
    }

    public LightweightException(String message, Throwable cause) {
        this(message, cause, false);
    }

    public LightweightException(String message, Throwable cause, boolean enableSuppression) {
        super(message, cause, enableSuppression, false);
    }
}
