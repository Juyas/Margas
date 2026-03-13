package de.juyas.margas.api;

import java.io.Serial;

/**
 * Class MargasException to generalize all exceptions thrown by the plugin.
 */
public class MargasException extends Exception {

    @Serial
    private static final long serialVersionUID = 2526537629923892824L;

    /**
     * Creates a new instance of MargasException.
     *
     * @param message the message of the exception
     * @param cause   the cause of the exception
     */
    public MargasException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance of MargasException.
     *
     * @param message the message of the exception
     */
    public MargasException(final String message) {
        super(message);
    }

}
