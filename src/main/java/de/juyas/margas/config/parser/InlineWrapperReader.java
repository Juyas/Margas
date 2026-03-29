package de.juyas.margas.config.parser;

import de.juyas.margas.api.MargasElement;
import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.MargasIdentifier;
import de.juyas.margas.api.MargasType;
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
public class InlineWrapperReader<T extends MargasElement<T>> implements ConfigSectionReader<T> {

    /**
     * The margas type of the inline wrapper.
     */
    private final MargasType<T> margasType;

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
     * @param margasType the margas type of the inline wrapper
     * @param reader     the reader of the wrapped type
     * @param manager    the manager of the wrapped type
     */
    public InlineWrapperReader(final MargasType<T> margasType, final ConfigSectionReader<T> reader, final MargasManager<T> manager) {
        this.margasType = margasType;
        this.reader = reader;
        this.manager = manager;
    }

    @Override
    public ValueProvider<T> read(final ConfigurationSection section, final String path) throws MargasException {
        if (section.isString(path)) {
            final IdentifierReader<T> identifierReader = new IdentifierReader<>(margasType);
            final MargasIdentifier<T> margasIdentifier = identifierReader.read(section, path);
            final Optional<ValueProvider<T>> valueProvider = manager.get(margasIdentifier);
            if (valueProvider.isPresent()) {
                return valueProvider.get();
            }
            throw new MargasException("Invalid inline identifier definition in section '%s' at path '%s'. Identifier '%s' not found for type '%s'.".formatted(section.getCurrentPath(), path, margasIdentifier.full(), margasType.name()));
        }
        return reader.read(section, path);
    }
}
