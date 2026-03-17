package de.juyas.margas.api.config;

import de.juyas.margas.api.MargasException;

/**
 * Interface ValueGenerator to generate a value.
 * It is essentially a supplier allowed to throw exceptions.
 *
 * @param <T> the type of the value
 */
@FunctionalInterface
public interface ValueGenerator<T> {

    /**
     * Generates a new value of type T.
     *
     * @return the generated value
     * @throws MargasException if the generation fails
     */
    T generate() throws MargasException;

}
