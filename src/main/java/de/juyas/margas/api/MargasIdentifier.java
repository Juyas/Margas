package de.juyas.margas.api;

/**
 * Interface MargasIdentifier to identify all elements with a unique id.
 */
public interface MargasIdentifier {

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
        return type().getName() + TYPE_SEPARATOR + name();
    }

    /**
     * Returns the type of the identifier.
     *
     * @return the type of the identifier
     */
    MargasType type();

    /**
     * Returns the name of the identifier.
     *
     * @return the name of the identifier
     */
    String name();

}
