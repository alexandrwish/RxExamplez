package com.magenta.mc.client.exception;

/**
 * Created by IntelliJ IDEA.
 * User: const
 * Date: 09.03.11
 * Time: 12:54
 * To change this template use File | Settings | File Templates.
 */
public class OperationTimeoutException extends RuntimeException {
    public OperationTimeoutException() {
    }

    public OperationTimeoutException(String message) {
        super(message);
    }

    public OperationTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public OperationTimeoutException(Throwable cause) {
        super(cause);
    }
}
