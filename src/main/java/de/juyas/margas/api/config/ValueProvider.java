package de.juyas.margas.api.config;

import de.juyas.margas.api.MargasException;

/**
 * Interface ValueProvider to provide a value with a default and generation method.
 *
 * @param <T> the type of the value
 */
public interface ValueProvider<T> {

    /**
     * Returns the default value.
     *
     * @return the default value
     */
    T defaultValue();

    /**
     * Generates a new value.
     *
     * @return the generated value
     * @throws MargasException if the generation fails
     */
    T generate() throws MargasException;

    /**
     * Returns true if the value is static.
     *
     * @return true if the value is static
     */
    boolean isStatic();

}
