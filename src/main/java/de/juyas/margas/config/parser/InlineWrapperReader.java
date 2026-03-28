package de.juyas.margas.config.parser;

import de.juyas.margas.api.MargasElement;
import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.MargasIdentifier;
import de.juyas.margas.api.config.ConfigSectionReader;
import de.juyas.margas.api.config.ValueProvider;
import de.juyas.margas.api.manager.MargasManager;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

/**
 * Class InlineWrapperReader to read an inline identifier from a configuration section for another reader.
 *
 * @param <T> the type of the inline wrapper
 */
public class InlineWrapperReader<T extends MargasElement> implements ConfigSectionReader<T> {

    /**
     * The name of the type of the inline wrapper.
     */
    private final String typeName;

    /**
     * The reader of the wrapped type.
     */
    private final ConfigSectionReader<T> reader;

    /**
     * The manager of the wrapped type.
     */
    private final MargasManager<T> manager;

    /**
     * Creates a new instance of InlineWrapperReader.
     *
     * @param typeName the name of the type of the inline wrapper
     * @param reader   the reader of the wrapped type
     * @param manager  the manager of the wrapped type
     */
    public InlineWrapperReader(final String typeName, final ConfigSectionReader<T> reader, final MargasManager<T> manager) {
        this.typeName = typeName;
        this.reader = reader;
        this.manager = manager;
    }

    @Override
    public ValueProvider<T> read(final ConfigurationSection section, final String path) throws MargasException {
        if (section.isString(path)) {
            final IdentifierReader identifierReader = new IdentifierReader();
            final MargasIdentifier margasIdentifier = identifierReader.read(section, path);
            final Optional<ValueProvider<T>> valueProvider = manager.get(margasIdentifier);
            if (valueProvider.isPresent()) {
                return valueProvider.get();
            }
            throw new MargasException("Invalid inline identifier definition in section '%s' at path '%s'. Identifier '%s' not found for type '%s'.".formatted(section.getCurrentPath(), path, margasIdentifier.full(), typeName));
        }
        return reader.read(section, path);
    }
}
