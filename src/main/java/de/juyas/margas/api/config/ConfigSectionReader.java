package de.juyas.margas.api.config;

/**
 * Interface ConfigSectionReader to read a configuration section.
 * This will read entire sections offering more configuration options and randomizable results.
 *
 * @param <T> the type produced by the reader
 */
@FunctionalInterface
public interface ConfigSectionReader<T> extends ConfigValueReader<ValueProvider<T>> {

}
