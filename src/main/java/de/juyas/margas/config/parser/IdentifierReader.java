package de.juyas.margas.config.parser;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.MargasIdentifier;
import de.juyas.margas.api.MargasType;
import de.juyas.margas.api.config.ConfigValueReader;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Class IdentifierReader to read an identifier from a configuration section at a given path.
 */
public class IdentifierReader implements ConfigValueReader<MargasIdentifier> {

    /**
     * Number of parts of which an identifier is composed.
     */
    private static final int IDENTIFIER_PARTS = 2;

    /**
     * Creates a new instance of IdentifierReader.
     */
    public IdentifierReader() {
        super();
    }

    @Override
    public MargasIdentifier read(final ConfigurationSection section, final String path) throws MargasException {
        final String value = section.getString(path);
        if (value == null) {
            throw new MargasException("Identifier definition not found at path '%s' in section '%s'.".formatted(path, section.getCurrentPath()));
        }
        final String[] split = value.split(":", IDENTIFIER_PARTS);
        if (split.length != IDENTIFIER_PARTS) {
            throw new MargasException("Invalid identifier definition at path '%s': '%s'".formatted(path, value));
        }
        try {
            return new Identifier(MargasType.getByName(split[0]), split[1]);
        } catch (final IllegalArgumentException e) {
            throw new MargasException("Invalid identifier definition at path '%s': '%s'".formatted(path, value), e);
        }
    }

    /**
     * Identifier record as a default implementation of MargasIdentifier.
     *
     * @param type the type of the identifier
     * @param name the name of the identifier
     */
    private record Identifier(MargasType type, String name) implements MargasIdentifier {

    }
}
