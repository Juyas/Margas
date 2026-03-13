package de.juyas.margas.api;

/**
 * Interface MargasElement to represent all elements loaded from configurations.
 */
@FunctionalInterface
public interface MargasElement {

    /**
     * Returns the identifier of the element.
     *
     * @return the identifier of the element
     */
    MargasIdentifier identifier();

}
