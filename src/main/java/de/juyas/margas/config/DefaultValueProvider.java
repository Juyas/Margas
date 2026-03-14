package de.juyas.margas.config;

import de.juyas.margas.api.config.ValueProvider;

import java.util.function.Supplier;

/**
 * Class DefaultValueProvider to provide a value with a default value.
 *
 * @param <T>          the type of the value
 * @param defaultValue the default value
 * @param generator    the generator to generate the value
 */
public record DefaultValueProvider<T>(T defaultValue, Supplier<T> generator) implements ValueProvider<T> {

    @Override
    public T generate() {
        return generator.get();
    }
}
