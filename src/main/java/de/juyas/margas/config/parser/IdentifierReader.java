package de.juyas.margas.config.parser;

import de.juyas.margas.api.MargasElement;
import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.MargasIdentifier;
import de.juyas.margas.api.MargasType;
import de.juyas.margas.api.config.ConfigValueReader;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Class IdentifierReader to read an identifier from a configuration section at a given path.
 *
 * @param <T> the type of the identifier to be read
 */
public class IdentifierReader<T extends MargasElement<T>> implements ConfigValueReader<MargasIdentifier<T>> {

    /**
     * The type of the identifier to be read.
     */
    private final MargasType<T> type;

    /**
     * Creates a new instance of IdentifierReader.
     *
     * @param type the type of the identifier to be read
     */
    public IdentifierReader(final MargasType<T> type) {
        super();
        this.type = type;
    }

    @Override
    public MargasIdentifier<T> read(final ConfigurationSection section, final String path) throws MargasException {
        final String value = section.getString(path);
        if (value == null) {
            throw new MargasException("Identifier definition not found at path '%s' in section '%s'.".formatted(path, section.getCurrentPath()));
        }
        try {
            return new Identifier<>(type, value);
        } catch (final IllegalArgumentException e) {
            throw new MargasException("Invalid identifier definition at path '%s': '%s'".formatted(path, value), e);
        }
    }

    /**
     * Identifier record as a default implementation of MargasIdentifier.
     *
     * @param type the type of the identifier
     * @param name the name of the identifier
     * @param <T>  the type of the element of the identifier
     */
    private record Identifier<T extends MargasElement<T>>(MargasType<T> type,
                                                          String name) implements MargasIdentifier<T> {

    }
}
