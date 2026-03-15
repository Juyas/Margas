package de.juyas.margas.config.parser;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.MargasIdentifier;
import de.juyas.margas.api.MargasType;
import de.juyas.margas.api.config.ConfigValueReader;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Class IdentifierReader to read an identifier from a configuration section at a given path.
 */
public class IdentifierListReader implements ConfigValueReader<List<MargasIdentifier>> {

    /**
     * Number of parts of which an identifier is composed.
     */
    private static final int IDENTIFIER_PARTS = 2;

    /**
     * Creates a new instance of IdentifierReader.
     */
    public IdentifierListReader() {
        super();
    }

    @Override
    public List<MargasIdentifier> read(final ConfigurationSection section, final String path) throws MargasException {
        final List<String> valueList = section.getStringList(path);
        if (valueList.isEmpty()) {
            throw new MargasException("Identifier list definition empty at path '%s' in section '%s'.".formatted(path, section.getCurrentPath()));
        }
        final List<MargasIdentifier> identifiers = new ArrayList<>(valueList.size());
        for (final String value : valueList) {
            final String[] split = value.split(":", IDENTIFIER_PARTS);
            if (split.length != IDENTIFIER_PARTS) {
                throw new MargasException("Invalid identifier definition in list at path '%s': '%s'".formatted(path, valueList));
            }
            try {
                identifiers.add(new Identifier(MargasType.getByName(split[0]), split[1]));
            } catch (final IllegalArgumentException e) {
                throw new MargasException("Invalid identifier definition in list at path '%s': '%s'".formatted(path, valueList), e);
            }
        }
        return identifiers;
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
