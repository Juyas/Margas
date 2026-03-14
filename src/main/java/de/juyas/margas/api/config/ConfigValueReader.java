package de.juyas.margas.api.config;

import de.juyas.margas.api.MargasException;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Interface ConfigSectionReader to read a configuration section.
 *
 * @param <T> the type produced by the reader
 */
@FunctionalInterface
public interface ConfigValueReader<T> {

    /**
     * Reads a configuration section at the given path and returns the result.
     *
     * @param section the section to read from
     * @param path    the path to read
     * @return the result of the read operation
     * @throws MargasException if the section or the path is invalid to parse
     */
    T read(ConfigurationSection section, String path) throws MargasException;

}
