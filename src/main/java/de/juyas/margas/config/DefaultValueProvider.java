package de.juyas.margas.config;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.config.ValueGenerator;
import de.juyas.margas.api.config.ValueProvider;

import java.util.function.Function;

/**
 * Class DefaultValueProvider to provide a value with a default value.
 *
 * @param <T>                  the type of the value
 * @param defaultValueSupplier the default value supplier
 * @param generator            the generator to generate the value
 * @param staticValue          if the value is static. static values are not changing if repeatedly generated
 */
public record DefaultValueProvider<T>(ValueGenerator<T> defaultValueSupplier,
                                      ValueGenerator<T> generator,
                                      boolean staticValue) implements ValueProvider<T> {

    /**
     * Creates a new instance of DefaultValueProvider with the given default value, generator, and static value.
     * <p>
     * The default value is provided statically.
     *
     * @param defaultValue the default value
     * @param generator    the generator to generate the value
     * @param staticValue  if the value is static
     */
    public DefaultValueProvider(final T defaultValue, final ValueGenerator<T> generator,
                                final boolean staticValue) {
        this(() -> defaultValue, generator, staticValue);
    }

    /**
     * Creates a new instance of DefaultValueProvider for a static value with the given default value.
     *
     * @param defaultValue the default value
     */
    public DefaultValueProvider(final T defaultValue) {
        this(defaultValue, () -> defaultValue, true);
    }

    @Override
    public T defaultValue() throws MargasException {
        return defaultValueSupplier.generate();
    }

    @Override
    public T generate() throws MargasException {
        return generator.generate();
    }

    @Override
    public boolean isStatic() {
        return staticValue;
    }

    @Override
    public <U> ValueProvider<U> map(final Function<T, U> function) {
        final ValueGenerator<U> defaultSupplier = () -> function.apply(defaultValueSupplier.generate());
        final ValueGenerator<U> mappedGenerator = () -> function.apply(generate());
        return new DefaultValueProvider<>(defaultSupplier, mappedGenerator, isStatic());
    }
}
