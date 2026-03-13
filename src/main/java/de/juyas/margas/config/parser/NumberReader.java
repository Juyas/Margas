package de.juyas.margas.config.parser;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.config.ConfigSectionReader;
import de.juyas.margas.api.config.ValueProvider;
import org.bukkit.configuration.ConfigurationSection;

import java.util.function.Supplier;

/**
 * Class NumberReader to read a number from a configuration section at a given path allowing different formats.
 */
public class NumberReader implements ConfigSectionReader<Number> {

    private static final String INTEGER_PATTERN = "^[0-9]+$";

    private static final String DOUBLE_PATTERN = "^([0-9]*\\.[0-9]+|[0-9]+\\.[0-9]*)$";

    private static final String FIELD_DEFAULT = "default";

    private static final String FIELD_RANGE = "range";

    private static final String FIELD_MIN = "min";

    private static final String FIELD_MAX = "max";

    @Override
    public ValueProvider<Number> read(final ConfigurationSection section, final String path) throws MargasException {
        if (section.isConfigurationSection(path)) {
            return parseDefaultedSection(section, path);
        }
        return parseDirectly(section, path);
    }

    /**
     * Parses the number directly from the configuration section.
     * <br>
     * Example: <code>path: 1234</code>
     * <br>
     * Example: <code>path: 42.69</code>
     *
     * @param section the section to parse
     * @param path    the path to parse
     * @return the parsed number or NaN if the path does not lead to a number
     * @throws MargasException if the path does not lead to valid number
     */
    private ValueProvider<Number> parseDirectly(final ConfigurationSection section, final String path) throws MargasException {
        if (section.isInt(path)) {
            return create(section.getInt(path));
        }
        if (section.isDouble(path)) {
            return create(section.getDouble(path));
        }
        throw new MargasException("Invalid at path '%s'. '%s' is not a number.".formatted(path, section.get(path)));
    }

    /**
     * Parses a subsection defining a default value and some additional options to generate a random.
     *
     * @param section the section to parse
     * @param path    the path to parse
     * @return the parsed number provider
     * @throws MargasException if the path does not lead to a valid number section definition
     */
    private ValueProvider<Number> parseDefaultedSection(final ConfigurationSection section, final String path) throws MargasException {
        final boolean hasDefault = section.contains(path + "." + FIELD_DEFAULT);
        final boolean hasRange = section.contains(path + "." + FIELD_RANGE);
        final boolean hasMin = section.contains(path + "." + FIELD_MIN);
        final boolean hasMax = section.contains(path + "." + FIELD_MAX);
        if (!hasDefault) {
            throw new MargasException("Invalid at path '%s'. '%s' is not a valid number section definition, missing `default`.".formatted(path, section.get(path)));
        }
        if (hasRange) {
            return parseRange(section, path);
        }
        if (hasMin && hasMax) {
            return parseMinMax(section, path);
        }
        throw new MargasException("Invalid at path '%s'. '%s' is not a valid number section definition.".formatted(path, section.get(path)));
    }

    /**
     * Parses a subsection defining a range of numbers to generate a random.
     * <br>
     * Example with integers:
     * <blockquote><pre>
     *     section:
     *       default: 5
     *       range: 1-10
     * </pre></blockquote>
     * Example with doubles:
     * <blockquote><pre>
     *     section:
     *       default: 0.5
     *       range: .3-.9
     * </pre></blockquote>
     *
     * @param section the section to parse
     * @param path    the path to parse
     * @return the parsed number provider
     * @throws MargasException if the path does not lead to a valid number range section definition
     */
    private ValueProvider<Number> parseRange(final ConfigurationSection section, final String path) throws MargasException {
        final String range = section.getString(path + "." + FIELD_RANGE);
        if (range == null) {
            throw new MargasException("Invalid at path '%s'. '%s' is not a valid range.".formatted(path, section.get(path)));
        }
        final String[] split = range.split("-");
        if (section.isInt(path + "." + FIELD_DEFAULT) && split[0].matches(INTEGER_PATTERN) && split[1].matches(INTEGER_PATTERN)) {
            final int def = section.getInt(path + "." + FIELD_DEFAULT);
            final int min = Integer.parseInt(split[0]);
            final int max = Integer.parseInt(split[1]);
            return create(balancedIntRandom(min, max), def);
        }
        if (section.isDouble(path + "." + FIELD_DEFAULT) && split[0].matches(DOUBLE_PATTERN) && split[1].matches(DOUBLE_PATTERN)) {
            final double def = section.getDouble(path + "." + FIELD_DEFAULT);
            final double min = Double.parseDouble(split[0]);
            final double max = Double.parseDouble(split[1]);
            return create(balancedDoubleRandom(min, max), def);
        }
        throw new MargasException("Invalid at path '%s'. '%s' is not a valid number-range definition.".formatted(path, section.get(path)));
    }

    /**
     * Parses a subsection defining a min and max number to generate a random.
     * <br>
     * Example with integers:
     * <blockquote><pre>
     *     section:
     *       default: 5
     *       min: 1
     *       max: 10
     * </pre></blockquote>
     * Example with doubles:
     * <blockquote><pre>
     *     section:
     *       default: 0.5
     *       min: .3
     *       max: .9
     * </pre></blockquote>
     *
     * @param section the section to parse
     * @param path    the path to parse
     * @return the parsed number provider
     * @throws MargasException if the path does not lead to a valid number min-max section definition
     */
    private ValueProvider<Number> parseMinMax(final ConfigurationSection section, final String path) throws MargasException {
        if (section.isInt(path + "." + FIELD_DEFAULT) && section.isInt(path + "." + FIELD_MIN) && section.isInt(path + "." + FIELD_MAX)) {
            return parseIntMinMax(section, path);
        }
        if (section.isDouble(path + "." + FIELD_DEFAULT) && section.isDouble(path + "." + FIELD_MIN) && section.isDouble(path + "." + FIELD_MAX)) {
            return parseDoubleMinMax(section, path);
        }
        throw new MargasException("Invalid at path '%s'. '%s' is not a number.".formatted(path, section.get(path)));
    }

    private ValueProvider<Number> parseIntMinMax(final ConfigurationSection section, final String path) {
        final int def = section.getInt(path + "." + FIELD_DEFAULT);
        final int min = section.getInt(path + "." + FIELD_MIN);
        final int max = section.getInt(path + "." + FIELD_MAX);
        return create(balancedIntRandom(min, max), def);
    }

    private ValueProvider<Number> parseDoubleMinMax(final ConfigurationSection section, final String path) {
        final double def = section.getDouble(path + "." + FIELD_DEFAULT);
        final double min = section.getDouble(path + "." + FIELD_MIN);
        final double max = section.getDouble(path + "." + FIELD_MAX);
        return create(balancedDoubleRandom(min, max), def);
    }

    /**
     * Returns a random number between min and max (inclusive).
     *
     * @param min the lower bound (inclusive)
     * @param max the upper bound (inclusive)
     * @return a random number provider
     */
    private Supplier<Number> balancedIntRandom(final int min, final int max) {
        return () -> Math.floor(Math.random() * (max - min + 1)) + min;
    }

    /**
     * Returns a random number between min and max (inclusive).
     *
     * @param min the lower bound (inclusive)
     * @param max the upper bound (inclusive)
     * @return a random number provider
     */
    private Supplier<Number> balancedDoubleRandom(final double min, final double max) {
        return () -> Math.random() * (max - min) + min;
    }

    private ValueProvider<Number> create(final Number defaultNumber) {
        return create(() -> defaultNumber, defaultNumber);
    }

    private ValueProvider<Number> create(final Supplier<Number> generator, final Number defaultNumber) {
        return new ValueProvider<>() {
            @Override
            public Number defaultValue() {
                return defaultNumber;
            }

            @Override
            public Number generate() {
                return generator.get();
            }
        };
    }

}
