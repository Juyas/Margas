package de.juyas.margas.config.parser;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.config.ConfigSectionReader;
import de.juyas.margas.api.config.ValueGenerator;
import de.juyas.margas.api.config.ValueProvider;
import de.juyas.margas.config.DefaultValueProvider;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class SectionMappedListReader to read a section list from a configuration section at a given path for a given type.
 * Results are mapped to a list of entries containing the key of the section the value was read from.
 *
 * @param <T> the type of the section list
 * @see ListToSectionConverter
 * @see SectionListReader
 */
public class SectionMappedListReader<T> implements ConfigSectionReader<List<Map.Entry<String, T>>> {

    /**
     * The reader to read the elements of the section list.
     */
    private final ConfigSectionReader<T> elementReader;

    /**
     * Creates a new instance of SectionMappedListReader for the given element reader.
     *
     * @param elementReader the reader to read the elements of the section list
     */
    public SectionMappedListReader(final ConfigSectionReader<T> elementReader) {
        this.elementReader = elementReader;
    }

    @Override
    public ValueProvider<List<Map.Entry<String, T>>> read(final ConfigurationSection section, final String path) throws MargasException {
        if (section.isConfigurationSection(path)) {
            return parseSection(section, path);
        }
        if (section.isList(path)) {
            return parseSection(ListToSectionConverter.convert(section, path), path);
        }
        throw new MargasException("Invalid section map-list definition in section '%s' at path '%s'.".formatted(section.getCurrentPath(), path));
    }

    private ValueProvider<List<Map.Entry<String, T>>> parseSection(final ConfigurationSection section, final String path) throws MargasException {
        return new DefaultValueProvider<>(createList(section, path), false);
    }

    private ValueGenerator<List<Map.Entry<String, T>>> createList(final ConfigurationSection section, final String path) throws MargasException {
        final ConfigurationSection elements = section.getConfigurationSection(path);
        if (elements == null) {
            throw new MargasException("Invalid map-list definition in section '%s' at path '%s'. No section at path".formatted(section.getCurrentPath(), path));
        }
        final Set<String> keys = elements.getKeys(false);
        if (keys.isEmpty()) {
            throw new MargasException("Invalid map-list definition at path '%s'. No keys found.".formatted(path));
        }
        final List<Map.Entry<String, ValueProvider<T>>> elementList = new ArrayList<>(keys.size());
        for (final String key : keys) {
            if (!elements.isConfigurationSection(key)) {
                throw new MargasException("Invalid map-list definition at path '%s'. No section at key: '%s'".formatted(path, key));
            }
            final ValueProvider<T> element = elementReader.read(elements, key);
            elementList.add(Map.entry(key, element));
        }
        return useDefault -> {
            final List<Map.Entry<String, T>> list = new ArrayList<>();
            for (final Map.Entry<String, ValueProvider<T>> providerEntry : elementList) {
                final T generate = providerEntry.getValue().generate(useDefault);
                list.add(Map.entry(providerEntry.getKey(), generate));
            }
            return list;
        };
    }
}
