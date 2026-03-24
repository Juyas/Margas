package de.juyas.margas.config;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.config.ValueGenerator;
import de.juyas.margas.api.config.ValueProvider;

/**
 * Class DefaultValueProvider to provide a value with a default value.
 *
 * @param <T>          the type of the value
 * @param defaultValue the default value
 * @param generator    the generator to generate the value
 * @param staticValue  if the value is static. static values are not changing if repeatedly generated
 */
public record DefaultValueProvider<T>(T defaultValue, ValueGenerator<T> generator,
                                      boolean staticValue) implements ValueProvider<T> {

    /**
     * Creates a new instance of DefaultValueProvider for a static value with the given default value.
     *
     * @param defaultValue the default value
     */
    public DefaultValueProvider(final T defaultValue) {
        this(defaultValue, () -> defaultValue, true);
    }

    @Override
    public T generate() throws MargasException {
        return generator.generate();
    }

    @Override
    public boolean isStatic() {
        return staticValue;
    }
}
