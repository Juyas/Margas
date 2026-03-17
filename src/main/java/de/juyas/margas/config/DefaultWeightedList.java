package de.juyas.margas.config;

import de.juyas.margas.api.config.WeightedList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * A default implementation of the WeightedList interface, which manages a collection of weighted
 * elements and provides functionality to add elements, retrieve all elements, and select random
 * elements based on their weights.
 *
 * @param <T> the type of the elements in the weighted list
 */
public class DefaultWeightedList<T> implements WeightedList<T> {

    /**
     * The list of weighted elements.
     */
    private final List<Weighted<T>> values;

    /**
     * Creates a new instance of DefaultWeightedList.
     */
    public DefaultWeightedList() {
        this.values = new ArrayList<>();
    }

    @Override
    public void add(final T value, final Number weight) {
        this.values.add(new DefaultWeighted<>(value, weight));
    }

    @Override
    public List<Weighted<T>> elements() {
        return Collections.unmodifiableList(values);
    }

    @Override
    public T randomValue() {
        final double totalWeight = totalWeight().doubleValue();
        if (totalWeight == 0) {
            throw new IllegalStateException("Total weight is zero, cannot select random value");
        }

        final double random = Math.random() * totalWeight;
        double cumulative = 0.0;

        for (final Weighted<T> weighted : values) {
            cumulative += weighted.weight().doubleValue();
            if (random <= cumulative) {
                return weighted.value();
            }
        }
        throw new IllegalStateException("Random value not found. This exception should never happen.");
    }

    @Override
    public List<T> randomValues(final int amount) {
        return IntStream.range(0, amount)
                .boxed()
                .map(i -> randomValue())
                .toList();
    }

    /**
     * Default implementation of Weighted.
     *
     * @param value  the value of the weighted element
     * @param weight the weight of the weighted element
     * @param <T>    the type of the value of the weighted element
     */
    private record DefaultWeighted<T>(T value, Number weight) implements Weighted<T> {

    }
}
