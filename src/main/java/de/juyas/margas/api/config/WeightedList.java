package de.juyas.margas.api.config;

import java.util.List;

/**
 * A data structure that allows management of elements associated with specific weights.
 * The WeightedList interface enables adding elements with weights, retrieving all elements,
 * performing weighted random selection, and calculating the total weight of all elements.
 *
 * @param <T> the type of the elements held in the weighted list
 */
public interface WeightedList<T> {

    /**
     * Adds a value along with its associated weight to the weighted list.
     *
     * @param value  the value to add to the list
     * @param weight the weight associated with the given value
     */
    void add(T value, Number weight);

    /**
     * Adds all elements from the given weighted list to this weighted list.
     *
     * @param list the weighted list to add
     */
    void add(WeightedList<T> list);

    /**
     * Returns the list of weighted elements.
     * Each element contains a value and its corresponding weight.
     *
     * @return a list of weighted elements
     */
    List<Weighted<T>> elements();

    /**
     * Selects and returns a random value from the weighted list based on the weights of its elements.
     * If the total weight is zero, this method will throw an {@code IllegalStateException}.
     *
     * @return a randomly selected value of type {@code T}, chosen according to the weights of the elements
     * @throws IllegalStateException if the total weight is zero or if a random value cannot be selected
     */
    T randomValue();

    /**
     * Returns a list of randomly selected values from the weighted elements in the list.
     *
     * @param amount the number of random values to select
     * @return a list containing the selected random values
     */
    List<T> randomValues(int amount);

    /**
     * Returns a list of values by extracting them from the weighted elements
     * stored in the weighted list.
     *
     * @return a list of values of type T corresponding to the weighted elements
     */
    default List<T> values() {
        return elements().stream().map(Weighted::value).toList();
    }

    /**
     * Calculates the total weight of all elements in the list by summing up their weights.
     *
     * @return the total weight as a {@link Number}, derived from summing all element weights
     */
    default Number totalWeight() {
        return elements().stream()
                .map(Weighted::weight)
                .mapToDouble(Number::doubleValue)
                .sum();
    }

    /**
     * Represents an element with an associated weight.
     * This interface is primarily used in contexts where each element in a collection
     * is assigned a weight, and operations such as weighted random selection or weight calculation
     * are performed. Such an interface is {@link WeightedList}.
     *
     * @param <T> the type of the value contained in the weighted element
     */
    interface Weighted<T> {

        /**
         * Returns the value contained in this weighted element.
         *
         * @return the value of type T associated with this element
         */
        T value();

        /**
         * Returns the weight associated with this element.
         * The weight is typically used in weighted operations such as weighted random selection
         * or weighted aggregation.
         *
         * @return the weight as a {@link Number}, representing the relative importance or probability
         * of this element within a collection
         */
        Number weight();

    }

}
