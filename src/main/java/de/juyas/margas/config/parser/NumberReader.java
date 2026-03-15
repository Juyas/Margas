package de.juyas.margas.config.parser;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.config.ConfigSectionReader;
import de.juyas.margas.api.config.ValueGenerator;
import de.juyas.margas.api.config.ValueProvider;
import de.juyas.margas.config.DefaultValueProvider;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Class NumberReader to read a number from a configuration section at a given path allowing different formats.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class NumberReader implements ConfigSectionReader<Number> {

    /**
     * Pattern to check if a string is a valid integer.
     */
    private static final String INTEGER_PATTERN = "^[0-9]+$";

    /**
     * Pattern to check if a string is a valid double.
     */
    private static final String DOUBLE_PATTERN = "^([+-]?(?:[0-9]+\\.?|[0-9]*\\.[0-9]+))(?:[Ee][+-]?[0-9]+)?$";

    /**
     * The field name in the configuration to read the default value.
     */
    private static final String FIELD_DEFAULT = "default";

    /**
     * The field name in the configuration to read the number range for random generation.
     */
    private static final String FIELD_RANGE = "range";

    /**
     * The field name in the configuration to read the min of the number range for random generation.
     */
    private static final String FIELD_MIN = "min";

    /**
     * The field name in the configuration to read the max of the number range for random generation.
     */
    private static final String FIELD_MAX = "max";

    /**
     * The readers minimally allowed value.
     */
    private final Number minAllowed;

    /**
     * The readers maximally allowed value.
     */
    private final Number maxAllowed;

    /**
     * Creates a new instance of NumberReader.
     */
    public NumberReader() {
        this(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    /**
     * Creates a new instance of NumberReader with a min allowed value.
     *
     * @param minAllowed the minimally allowed value
     */
    public NumberReader(final Number minAllowed) {
        this(minAllowed, Double.POSITIVE_INFINITY);
    }

    /**
     * Creates a new instance of NumberReader with a min and max allowed value.
     *
     * @param minAllowed the minimally allowed value
     * @param maxAllowed the maximally allowed value
     */
    public NumberReader(final Number minAllowed, final Number maxAllowed) {
        super();
        this.minAllowed = minAllowed;
        this.maxAllowed = maxAllowed;
    }

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
            final int value = section.getInt(path);
            validateAllowedRange("number", value, section);
            return create(value);
        }
        if (section.isDouble(path)) {
            final double value = section.getDouble(path);
            validateAllowedRange("number", value, section);
            return create(value);
        }
        throw new MargasException("Invalid number definition at path '%s'. '%s' is not a number.".formatted(path, section.get(path)));
    }

    private boolean isNumber(final ConfigurationSection section, final String path) {
        return section.isInt(path) || section.isDouble(path);
    }

    private boolean isInvalidIntRange(final int min, final int max, final int def) {
        return def > max || def < min;
    }

    private boolean isInvalidDoubleRange(final double min, final double max, final double def) {
        return min > max || def > max || def < min;
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
            throw new MargasException("Invalid number section definition at path '%s'. Missing `default`.".formatted(path));
        }
        if (hasRange) {
            return parseRange(section, path);
        }
        if (hasMin && hasMax) {
            return parseMinMax(section, path);
        }
        throw new MargasException("Invalid number section definition at path '%s'.".formatted(path));
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
    @SuppressWarnings("PMD.CyclomaticComplexity")
    private ValueProvider<Number> parseRange(final ConfigurationSection section, final String path) throws MargasException {
        final String range = section.getString(path + "." + FIELD_RANGE);
        if (range == null) {
            throw new MargasException("Invalid number definition at path '%s'. '%s' is not a valid range.".formatted(path, section.get(path)));
        }
        final String[] split = range.split("-", 2);
        if (section.isInt(path + "." + FIELD_DEFAULT) && split[0].matches(INTEGER_PATTERN) && split[1].matches(INTEGER_PATTERN)) {
            final int def = section.getInt(path + "." + FIELD_DEFAULT);
            validateAllowedRange("default", def, section);
            final int min = Integer.parseInt(split[0]);
            validateAllowedRange("range-min", min, section);
            final int max = Integer.parseInt(split[1]);
            validateAllowedRange("range-max", max, section);
            if (isInvalidIntRange(min, max, def)) {
                throw new MargasException("Invalid integer range definition at path '%s'. Range limits are potentially swapped.".formatted(path));
            }
            return create(balancedIntRandom(min, max), def);
        }
        if (isNumber(section, path + "." + FIELD_DEFAULT) && split[0].matches(DOUBLE_PATTERN) && split[1].matches(DOUBLE_PATTERN)) {
            final double def = section.getDouble(path + "." + FIELD_DEFAULT);
            validateAllowedRange("default", def, section);
            final double min = Double.parseDouble(split[0]);
            validateAllowedRange("range-min", min, section);
            final double max = Double.parseDouble(split[1]);
            validateAllowedRange("range-max", max, section);
            if (isInvalidDoubleRange(min, max, def)) {
                throw new MargasException("Invalid decimal range definition at path '%s'. Range limits are potentially swapped.".formatted(path));
            }
            return create(balancedDoubleRandom(min, max), def);
        }
        throw new MargasException("Invalid number range definition at path '%s'. '%s' is not a valid number-range definition.".formatted(path, section.get(path)));
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
        if (isNumber(section, path + "." + FIELD_DEFAULT) && isNumber(section, path + "." + FIELD_MIN) && isNumber(section, path + "." + FIELD_MAX)) {
            return parseDoubleMinMax(section, path);
        }
        throw new MargasException("Invalid number min-max definition at path '%s'.".formatted(path));
    }

    private ValueProvider<Number> parseIntMinMax(final ConfigurationSection section, final String path) throws MargasException {
        final int def = section.getInt(path + "." + FIELD_DEFAULT);
        validateAllowedRange("default", def, section);
        final int min = section.getInt(path + "." + FIELD_MIN);
        validateAllowedRange("min", min, section);
        final int max = section.getInt(path + "." + FIELD_MAX);
        validateAllowedRange("max", max, section);
        if (isInvalidIntRange(min, max, def)) {
            throw new MargasException("Invalid integer min-max definition at path '%s'. Range limits are potentially swapped.".formatted(path));
        }
        return create(balancedIntRandom(min, max), def);
    }

    private ValueProvider<Number> parseDoubleMinMax(final ConfigurationSection section, final String path) throws MargasException {
        final double def = section.getDouble(path + "." + FIELD_DEFAULT);
        validateAllowedRange("default", def, section);
        final double min = section.getDouble(path + "." + FIELD_MIN);
        validateAllowedRange("min", min, section);
        final double max = section.getDouble(path + "." + FIELD_MAX);
        validateAllowedRange("max", max, section);
        if (isInvalidDoubleRange(min, max, def)) {
            throw new MargasException("Invalid decimal min-max definition at path '%s'. Range limits are potentially swapped.".formatted(path));
        }
        return create(balancedDoubleRandom(min, max), def);
    }

    /**
     * Returns a random number between min and max (inclusive).
     *
     * @param min the lower bound (inclusive)
     * @param max the upper bound (inclusive)
     * @return a random number provider
     */
    private ValueGenerator<Number> balancedIntRandom(final int min, final int max) {
        return () -> Math.floor(Math.random() * (max - min + 1)) + min;
    }

    /**
     * Returns a random number between min and max (inclusive).
     *
     * @param min the lower bound (inclusive)
     * @param max the upper bound (inclusive)
     * @return a random number provider
     */
    private ValueGenerator<Number> balancedDoubleRandom(final double min, final double max) {
        return () -> Math.random() * (max - min) + min;
    }

    /**
     * Validates the given value to be within the allowed range.
     *
     * @param valueName the name of the value to validate, e.g. "min"
     * @param value     the value to validate
     * @param section   the section where the value is defined
     * @throws MargasException if the value is not within the allowed range
     */
    private void validateAllowedRange(final String valueName, final Number value, final ConfigurationSection section) throws MargasException {
        if (value.doubleValue() < minAllowed.doubleValue() || value.doubleValue() > maxAllowed.doubleValue()) {
            throw new MargasException("%s '%s' is not within the allowed range of '%s' and '%s' in section '%s'.".formatted(valueName, value, minAllowed, maxAllowed, section.getCurrentPath()));
        }
    }

    private ValueProvider<Number> create(final Number defaultNumber) {
        return new DefaultValueProvider<>(defaultNumber, () -> defaultNumber, true);
    }

    private ValueProvider<Number> create(final ValueGenerator<Number> generator, final Number defaultNumber) {
        return new DefaultValueProvider<>(defaultNumber, generator, false);
    }

}
