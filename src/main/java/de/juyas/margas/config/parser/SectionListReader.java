package de.juyas.margas.config.parser;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.config.ConfigSectionReader;
import de.juyas.margas.api.config.ValueGenerator;
import de.juyas.margas.api.config.ValueProvider;
import de.juyas.margas.config.DefaultValueProvider;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Class SectionListReader to read a section list from a configuration section at a given path for a given type.
 *
 * @param <T> the type of the section list
 * @see ListToSectionConverter
 * @see SectionMappedListReader
 */
public class SectionListReader<T> implements ConfigSectionReader<List<T>> {

    /**
     * The reader to read the elements of the section list.
     */
    private final ConfigSectionReader<T> elementReader;

    /**
     * Creates a new instance of SectionListReader for the given element reader.
     *
     * @param elementReader the reader to read the elements of the section list
     */
    public SectionListReader(final ConfigSectionReader<T> elementReader) {
        this.elementReader = elementReader;
    }

    @Override
    public ValueProvider<List<T>> read(final ConfigurationSection section, final String path) throws MargasException {
        if (section.isConfigurationSection(path)) {
            return parseSection(section, path);
        }
        if (section.isList(path)) {
            return parseSection(ListToSectionConverter.convert(section, path), path);
        }
        throw new MargasException("Invalid section list definition in section '%s' at path '%s'.".formatted(section.getCurrentPath(), path));
    }

    private ValueProvider<List<T>> parseSection(final ConfigurationSection section, final String path) throws MargasException {
        return new DefaultValueProvider<>(createList(section, path), false);
    }

    private ValueGenerator<List<T>> createList(final ConfigurationSection section, final String path) throws MargasException {
        final ConfigurationSection elements = section.getConfigurationSection(path);
        if (elements == null) {
            throw new MargasException("Invalid list definition at path '%s'.".formatted(path));
        }
        final Set<String> keys = elements.getKeys(false);
        if (keys.isEmpty()) {
            throw new MargasException("Invalid list definition at path '%s'. Nothing at key: '%s'".formatted(path, keys));
        }
        final List<ValueProvider<T>> elementList = new ArrayList<>(keys.size());
        for (final String key : keys) {
            if (!elements.isConfigurationSection(key)) {
                throw new MargasException("Invalid list definition at path '%s'. Nothing at key: '%s'".formatted(path, key));
            }
            final ValueProvider<T> element = elementReader.read(elements, key);
            elementList.add(element);
        }
        return useDefault -> {
            final List<T> list = new ArrayList<>();
            for (final ValueProvider<T> provider : elementList) {
                final T generate = provider.generate(useDefault);
                list.add(generate);
            }
            return list;
        };
    }
}
