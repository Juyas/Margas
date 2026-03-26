package de.juyas.margas.config.parser;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.config.ConfigSectionReader;
import de.juyas.margas.api.config.ValueGenerator;
import de.juyas.margas.api.config.ValueProvider;
import de.juyas.margas.api.config.WeightedList;
import de.juyas.margas.config.DefaultValueProvider;
import de.juyas.margas.config.DefaultWeightedList;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Class WeightedListReader to read a weighted list from a configuration section at a given path.
 *
 * @param <T> the type of the elements of the weighted list
 */
public class WeightedListReader<T> implements ConfigSectionReader<WeightedList<ValueProvider<T>>> {

    /**
     * The name of the field containing the weight of an element.
     */
    private static final String FIELD_WEIGHT = "weight";

    /**
     * The reader to read the elements of the weighted list.
     */
    private final ConfigSectionReader<T> elementReader;

    /**
     * Creates a new instance of WeightedListReader for the given element reader.
     *
     * @param elementReader the reader to read the elements of the weighted list
     */
    public WeightedListReader(final ConfigSectionReader<T> elementReader) {
        this.elementReader = elementReader;
    }

    @Override
    public ValueProvider<WeightedList<ValueProvider<T>>> read(final ConfigurationSection section, final String path) throws MargasException {
        if (section.isList(path)) {
            return parseSection(ListToSectionConverter.convert(section, path), path);
        }
        if (section.isConfigurationSection(path)) {
            return parseSection(section, path);
        }
        throw new MargasException("Invalid weighted identifier list definition at path '%s'.".formatted(path));
    }

    private ValueProvider<WeightedList<ValueProvider<T>>> parseSection(final ConfigurationSection section, final String path) throws MargasException {
        final ValueGenerator<WeightedList<ValueProvider<T>>> generator = createGenerator(section, path, false);
        return new DefaultValueProvider<>(createGenerator(section, path, true).generate(), generator, false);
    }

    private ValueGenerator<WeightedList<ValueProvider<T>>> createGenerator(final ConfigurationSection section, final String path, final boolean useDefault) {
        final NumberReader numberReader = new NumberReader(0, Integer.MAX_VALUE);
        return () -> {
            final WeightedList<ValueProvider<T>> weightedList = new DefaultWeightedList<>();
            for (final String key : section.getKeys(false)) {
                final String pathDown = path + "." + key;
                final ValueProvider<T> valueProvider = elementReader.read(section, pathDown);
                final ValueProvider<Number> weight = numberReader.read(section, pathDown + "." + FIELD_WEIGHT);
                weightedList.add(valueProvider, useDefault ? weight.defaultValue() : weight.generate());
            }
            return weightedList;
        };
    }
}
