package de.juyas.margas.api.config;

import de.juyas.margas.api.MargasException;
import org.jetbrains.annotations.Contract;

import java.util.function.Function;

/**
 * Interface ValueProvider to provide a value with a default and generation method.
 *
 * @param <T> the type of the value
 */
public interface ValueProvider<T> {

    /**
     * Returns the default value.
     *
     * @return the default value
     * @throws MargasException if the default value cannot be retrieved
     */
    T defaultValue() throws MargasException;

    /**
     * Generates a new value.
     *
     * @return the generated value
     * @throws MargasException if the generation fails
     */
    T generate() throws MargasException;

    /**
     * Returns true if the value is static.
     * <p>
     * A static value is always the same.
     * {@link #defaultValue()} and {@link #generate()} will always return an equal value.
     * However, it is not guaranteed that the value is always the same instance.
     *
     * @return true if the value is static
     */
    boolean isStatic();

    /**
     * Maps the value to another type.
     *
     * @param function the function to map the value
     * @param <U>      the type of the mapped value
     * @return the mapped value provider
     */
    @Contract(pure = true, value = "_ -> new")
    <U> ValueProvider<U> map(Function<T, U> function);

}
