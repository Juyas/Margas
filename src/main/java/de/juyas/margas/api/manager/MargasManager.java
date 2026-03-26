package de.juyas.margas.api.manager;

import de.juyas.margas.api.MargasElement;
import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.MargasIdentifier;
import de.juyas.margas.api.config.ValueProvider;

import java.util.List;
import java.util.Optional;

/**
 * Interface MargasManager to manage elements of a specific type.
 *
 * @param <T> the type of the elements managed by the manager
 */
public interface MargasManager<T extends MargasElement> {

    /**
     * Adds an element to the manager.
     *
     * @param element the element to add to the manager
     * @throws MargasException if an element with the same identifier already exists
     */
    void add(ValueProvider<T> element) throws MargasException;

    /**
     * Returns an element from the manager.
     *
     * @param identifier the identifier of the element to get from the manager
     * @return the element with the given identifier, if it exists, otherwise {@link Optional#empty()}
     */
    Optional<ValueProvider<T>> get(MargasIdentifier identifier);

    /**
     * Returns a list of elements from the manager by their identifiers.
     * <p>
     * The order of the elements in the returned list is retained.
     * However, elements that are not found will be omitted.
     *
     * @param identifiers the identifiers of the elements to get from the manager
     * @return a list of elements with the given identifiers
     */
    List<ValueProvider<T>> get(List<MargasIdentifier> identifiers);

    /**
     * Returns all elements from the manager.
     *
     * @return a list of all elements in the manager
     */
    List<ValueProvider<T>> all();

}
