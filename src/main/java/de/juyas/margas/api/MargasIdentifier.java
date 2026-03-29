package de.juyas.margas.api;

/**
 * Interface MargasIdentifier to identify all elements with a unique id.
 *
 * @param <T> the type of the element identified by this identifier
 */
public interface MargasIdentifier<T extends MargasElement<T>> {

    /**
     * Separator for type and name.
     */
    String TYPE_SEPARATOR = ":";

    /**
     * Returns the full identifier.
     *
     * @return the full identifier
     */
    default String full() {
        return type().name() + TYPE_SEPARATOR + name();
    }

    /**
     * Returns the type of the identifier.
     *
     * @return the type of the identifier
     */
    MargasType<T> type();

    /**
     * Returns the name of the identifier.
     *
     * @return the name of the identifier
     */
    String name();

}
