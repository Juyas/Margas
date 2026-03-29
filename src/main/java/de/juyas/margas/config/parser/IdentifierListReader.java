package de.juyas.margas.config.parser;

import de.juyas.margas.api.MargasElement;
import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.MargasIdentifier;
import de.juyas.margas.api.MargasType;
import de.juyas.margas.api.config.ConfigValueReader;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Class IdentifierReader to read an identifier from a configuration section at a given path.
 *
 * @param <T> the type of the identifier to be read
 */
public class IdentifierListReader<T extends MargasElement<T>> implements ConfigValueReader<List<MargasIdentifier<T>>> {

    /**
     * Number of parts of which an identifier is composed.
     */
    private static final int IDENTIFIER_PARTS = 2;

    /**
     * The type of the identifiers to be read.
     */
    private final MargasType<T> type;

    /**
     * Creates a new instance of IdentifierReader.
     *
     * @param type the type of the identifiers to be read
     */
    public IdentifierListReader(final MargasType<T> type) {
        super();
        this.type = type;
    }

    @Override
    public List<MargasIdentifier<T>> read(final ConfigurationSection section, final String path) throws MargasException {
        final List<String> valueList = section.getStringList(path);
        if (valueList.isEmpty()) {
            throw new MargasException("Identifier list definition empty at path '%s' in section '%s'.".formatted(path, section.getCurrentPath()));
        }
        final List<MargasIdentifier<T>> identifiers = new ArrayList<>(valueList.size());
        for (final String value : valueList) {
            final String[] split = value.split(":", IDENTIFIER_PARTS);
            if (split.length != IDENTIFIER_PARTS) {
                throw new MargasException("Invalid identifier definition in list at path '%s': '%s'".formatted(path, valueList));
            }
            if (!type.name().equalsIgnoreCase(split[0])) {
                throw new MargasException("Invalid identifier definition in list at path '%s': '%s'".formatted(path, valueList));
            }
            identifiers.add(new Identifier<>(type, split[1]));

        }
        return identifiers;
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
