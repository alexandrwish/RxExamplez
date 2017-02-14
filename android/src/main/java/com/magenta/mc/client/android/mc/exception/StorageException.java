package com.magenta.mc.client.android.mc.exception;

/**
 * @author Petr Popov
 *         Created: 19.12.11 16:38
 */
public class StorageException extends RuntimeException { //todo make it checked

    public StorageException() {
        super();
    }

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageException(Throwable cause) {
        super(cause);
    }

}
