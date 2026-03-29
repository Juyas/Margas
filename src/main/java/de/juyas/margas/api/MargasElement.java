package de.juyas.margas.api;

/**
 * Interface MargasElement to represent all elements loaded from configurations.
 *
 * @param <T> the type of the element
 */
@FunctionalInterface
public interface MargasElement<T extends MargasElement<T>> {

    /**
     * Returns the identifier of the element.
     *
     * @return the identifier of the element
     */
    MargasIdentifier<T> identifier();

}
