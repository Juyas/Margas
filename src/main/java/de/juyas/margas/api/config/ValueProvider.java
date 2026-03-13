package de.juyas.margas.api.config;

/**
 * Interface ValueProvider to provide a value with default and generation method.
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
     */
    T generate();

}
