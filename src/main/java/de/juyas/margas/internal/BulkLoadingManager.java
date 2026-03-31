package de.juyas.margas.internal;

import de.juyas.margas.api.MargasElement;
import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.MargasIdentifier;
import de.juyas.margas.api.MargasType;
import de.juyas.margas.api.config.ConfigSectionReader;
import de.juyas.margas.api.config.ValueProvider;
import de.juyas.margas.api.manager.MargasManager;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.function.Function;

/**
 * Class BulkLoader to load all elements of a given type from a configuration section.
 *
 * @param <T> the type of the elements to be loaded
 */
public class BulkLoadingManager<T extends MargasElement<T>> implements MargasManager<T> {

    /**
     * The loaded elements.
     */
    private final Map<String, ValueProvider<T>> loadedElements;

    /**
     * The reader for the elements.
     */
    private final ConfigSectionReader<T> elementReaderSupplier;

    /**
     * The type of the manager to load.
     */
    private final MargasType<T> margasType;

    /**
     * Creates a new instance of BulkLoadingManager.
     *
     * @param margasType            the type of the manager to load
     * @param elementReaderSupplier the reader for the elements to load
     */
    public BulkLoadingManager(final MargasType<T> margasType, final Function<BulkLoadingManager<T>, ConfigSectionReader<T>> elementReaderSupplier) {
        super();
        this.margasType = margasType;
        this.elementReaderSupplier = elementReaderSupplier.apply(this);
        loadedElements = new HashMap<>();
    }

    @Override
    public void add(final String identifier, final ValueProvider<T> element) throws MargasException {
        loadedElements.put(identifier, element);
    }

    @Override
    public Optional<ValueProvider<T>> get(final MargasIdentifier<T> identifier) {
        return Optional.ofNullable(loadedElements.get(identifier.name()));
    }

    @Override
    public List<ValueProvider<T>> get(final List<MargasIdentifier<T>> identifiers) {
        return identifiers.stream().map(this::get).flatMap(Optional::stream).toList();
    }

    @Override
    public List<ValueProvider<T>> all() {
        return loadedElements.values().stream().toList();
    }

    /**
     * Clears all loaded elements.
     */
    public void clear() {
        loadedElements.clear();
    }

    /**
     * Loads the elements from the given section into the manager.
     * <p>
     * If the section does not exist, this method does nothing.
     *
     * @param section the section to load the elements from (if it exists)
     * @throws MargasException if the section is invalid to parse or the elements are invalid to parse
     */
    public void load(final ConfigurationSection section) throws MargasException {
        if (!section.isConfigurationSection(margasType.sectionName())) {
            return;
        }
        final ConfigurationSection elementsSection = section.getConfigurationSection(margasType.sectionName());
        if (elementsSection == null) {
            return;
        }
        final Set<String> elements = elementsSection.getKeys(false);
        for (final String element : elements) {
            try {
                final ValueProvider<T> valueProvider = elementReaderSupplier.read(elementsSection, element);
                add(element, valueProvider);
            } catch (final MargasException e) {
                throw new MargasException("Invalid element definition at path '%s.%s'.".formatted(margasType.sectionName(), element), e);
            }
        }
    }

}
