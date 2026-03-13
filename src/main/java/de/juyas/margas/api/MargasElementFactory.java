package de.juyas.margas.api;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Interface MargasElementFactory to parse a configuration section to an element of type T.
 *
 * @param <T> the type of the element to parse
 */
@FunctionalInterface
public interface MargasElementFactory<T extends MargasElement> {

    /**
     * Parses a configuration section to an element of type T.
     *
     * @param section the section to parse
     * @return the parsed element of type T
     * @throws MargasException if the section is invalid or the element cannot be parsed
     */
    T parseElement(ConfigurationSection section) throws MargasException;

}
