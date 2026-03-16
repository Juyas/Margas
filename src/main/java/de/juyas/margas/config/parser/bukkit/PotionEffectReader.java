package de.juyas.margas.config.parser.bukkit;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.config.ConfigSectionReader;
import de.juyas.margas.api.config.ValueGenerator;
import de.juyas.margas.api.config.ValueProvider;
import de.juyas.margas.config.DefaultValueProvider;
import de.juyas.margas.config.parser.NumberReader;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Locale;

/**
 * Class PotionEffectReader to read a potion effect from a configuration.
 * <p>
 * This reader supports two formats for defining potion effects:
 * <ul>
 *     <li><b>Section format:</b>
 *          A configuration section with fields for
 *          type, duration, amplifier, ambient, particles, and icon.
 *     </li>
 *     <li><b>Inline format:</b>
 *          A string in the format "type:duration:amplifier:ambient?:particles?:icon?"
 *          where optional fields default to true.
 *     </li>
 * </ul>
 * <p>
 * The reader validates all input values and throws {@link MargasException} for invalid configurations.
 *
 * @see ConfigSectionReader
 * @see PotionEffect
 * @see PotionEffectType
 */
public class PotionEffectReader implements ConfigSectionReader<PotionEffect> {

    /**
     * The minimal required number of arguments for a potion effect definition.
     * They are: type, duration, amplifier.
     */
    private static final int MINIMAL_ARGUMENTS = 3;

    /**
     * The maximally allowed number of arguments for a potion effect definition.
     * They are: type, duration, amplifier, ambient, particles, icon.
     */
    private static final int MAXIMAL_ARGUMENTS = 6;

    /**
     * Represents the name of the configuration field that specifies the type of potion effect.
     * This field is used to identify the {@link PotionEffectType} within a configuration section.
     * <p>
     * The value of this field must correspond to a valid potion effect type identifier
     * that can be parsed and resolved using the {@link Registry#POTION_EFFECT_TYPE}.
     */
    private static final String FIELD_TYPE = "type";

    /**
     * Represents the configuration field key used to specify the duration of a potion effect.
     * This key is used to read the integer duration value from a configuration section, where the
     * duration defines the length of time (in ticks) for which the potion effect will be active.
     */
    private static final String FIELD_DURATION = "duration";

    /**
     * Represents the configuration field key used to specify the amplifier of a potion effect.
     * This key is used to read the integer amplifier value from a configuration section, where the
     * amplifier defines the strength of the potion effect.
     */
    private static final String FIELD_AMPLIFIER = "amplifier";

    /**
     * Represents the configuration field key used to specify the ambient status of a potion effect.
     * This key is used to read the boolean ambient value from a configuration section, where the
     * ambient status indicates whether the potion effect should be producing more translucend particles.
     */
    private static final String FIELD_AMBIENT = "ambient";

    /**
     * Represents the configuration field key used to specify the particles status of a potion effect.
     * This key is used to read the boolean particles value from a configuration section, where the
     * particles status indicates whether the potion effect should be producing particles.
     */
    private static final String FIELD_PARTICLES = "particles";

    /**
     * Represents the configuration field key used to specify the icon status of a potion effect.
     * This key is used to read the boolean icon value from a configuration section, where the
     * icon status indicates whether the potion effect should show up as an icon.
     */
    private static final String FIELD_ICON = "icon";

    /**
     * Creates a new instance of PotionEffectReader.
     */
    public PotionEffectReader() {
        super();
    }

    @Override
    public ValueProvider<PotionEffect> read(final ConfigurationSection section, final String path) throws MargasException {
        if (section.isConfigurationSection(path)) {
            return parseSection(section, path);
        }
        if (section.isString(path)) {
            return parseInline(section, path);
        }
        throw new MargasException("Invalid potion effect definition at path '%s'.".formatted(path));
    }

    /**
     * Parses a configuration section to generate a {@link ValueProvider} for a {@link PotionEffect}.
     * The method reads and validates a potion effect configuration from a specified section and path.
     *
     * @param section the configuration section containing the potion effect definitions
     * @param path    the path within the section where the potion effect is defined
     * @return a {@link ValueProvider} that can generate {@link PotionEffect} objects based on the configuration
     * @throws MargasException if the configuration section is invalid, missing, or contains unknown potion effect types
     */
    private ValueProvider<PotionEffect> parseSection(final ConfigurationSection section, final String path) throws MargasException {
        final ConfigurationSection effectSection = section.getConfigurationSection(path);
        if (effectSection == null) {
            throw new MargasException("Missing potion effect definition '%s' at path '%s'.".formatted(path, section.getCurrentPath()));
        }

        final String type = effectSection.getString(FIELD_TYPE);
        if (type == null) {
            throw new MargasException("Missing potion effect 'type' at path '%s'.".formatted(effectSection.getCurrentPath()));
        }
        final PotionEffectType potionEffectType = Registry.POTION_EFFECT_TYPE.get(NamespacedKey.minecraft(type));
        if (potionEffectType == null) {
            throw new MargasException("Invalid potion effect definition at path '%s'. Unknown potion effect type '%s'.".formatted(effectSection.getCurrentPath(), type));
        }

        final NumberReader durationReader = new NumberReader(-1, Integer.MAX_VALUE);
        final NumberReader amplifierReader = new NumberReader(1, 100);
        final boolean ambient = effectSection.getBoolean(FIELD_AMBIENT, true);
        final boolean particles = effectSection.getBoolean(FIELD_PARTICLES, true);
        final boolean icon = effectSection.getBoolean(FIELD_ICON, true);

        final PotionEffect defaultEffect = new PotionEffect(potionEffectType,
                durationReader.read(effectSection, FIELD_DURATION).defaultValue().intValue(),
                amplifierReader.read(effectSection, FIELD_AMPLIFIER).defaultValue().intValue(),
                ambient, particles, icon);

        final ValueGenerator<PotionEffect> generator = () -> new PotionEffect(potionEffectType,
                durationReader.read(effectSection, FIELD_DURATION).generate().intValue(),
                amplifierReader.read(effectSection, FIELD_AMPLIFIER).generate().intValue(),
                ambient, particles, icon);

        return new DefaultValueProvider<>(defaultEffect, generator, false);
    }

    /**
     * Parses an inline potion effect definition from a configuration section
     * and creates a {@link ValueProvider} for a {@link PotionEffect}.
     * This method reads the configuration at the specified path, validates its content,
     * and delegates the value parsing to the {@link #parseInlineValue(String)} method.
     *
     * @param section the configuration section containing the potion effect definitions
     * @param path    the path within the configuration section where the potion effect is defined
     * @return a static {@link ValueProvider} that can generate {@link PotionEffect} objects from the configuration
     * @throws MargasException if the specified path is missing, the potion effect value is null,
     *                         or if the potion effect cannot be parsed
     */
    private ValueProvider<PotionEffect> parseInline(final ConfigurationSection section, final String path) throws MargasException {
        final String value = section.getString(path);
        if (value == null) {
            throw new MargasException("Missing potion effect definition at path '%s'.".formatted(path));
        }
        return parseInlineValue(value);
    }

    private String[] readArguments(final String value) throws MargasException {
        final String[] arguments = value.split(":");
        if (arguments.length < MINIMAL_ARGUMENTS) {
            throw new MargasException("Invalid potion effect definition: '%s'. Effects need at least %s arguments in the format: 'type:duration:amplifier'".formatted(value, MINIMAL_ARGUMENTS));
        }
        if (arguments.length > MAXIMAL_ARGUMENTS) {
            throw new MargasException("Invalid potion effect definition: '%s'. Effects can only have %s arguments in the format: 'type:duration:amplifier:ambient?:particles?:icon?'".formatted(value, MAXIMAL_ARGUMENTS));
        }
        return arguments;
    }

    /**
     * Parses a string value to create a {@link ValueProvider} for a {@link PotionEffect}.
     * This method interprets the potion effect definition provided as a string,
     * validates its components, and constructs a {@link PotionEffect}.
     *
     * @param value the inline string representing the potion effect definition in the format:
     *              <code>type:duration:amplifier[:ambient?:particles?:icon?]</code>
     * @return a static {@link ValueProvider} for the created {@link PotionEffect}.
     * @throws MargasException if the string format is invalid, if required arguments are missing,
     *                         or if an unknown potion effect type is specified.
     */
    private ValueProvider<PotionEffect> parseInlineValue(final String value) throws MargasException {
        final String[] arguments = readArguments(value);

        final PotionEffectType potionEffectType = Registry.POTION_EFFECT_TYPE.get(NamespacedKey.minecraft(arguments[0]));
        if (potionEffectType == null) {
            throw new MargasException("Invalid potion effect definition: '%s'. Unknown potion effect type '%s'.".formatted(value, arguments[0]));
        }

        final int duration = tryParseInt(value, arguments[1]);
        final int amplifier = tryParseInt(value, arguments[2]);
        final boolean ambient = tryParseBoolean(value, arguments, 3);
        final boolean particles = tryParseBoolean(value, arguments, 4);
        final boolean icon = tryParseBoolean(value, arguments, 5);

        final PotionEffect effect = new PotionEffect(potionEffectType, duration, amplifier, ambient, particles, icon);
        return new DefaultValueProvider<>(effect, () -> effect, true);
    }

    private boolean tryParseBoolean(final String sourceValue, final String[] arguments, final int argument) throws MargasException {
        if (arguments.length <= argument) {
            return true;
        }
        final String value = arguments[argument];
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "true", "1" -> true;
            case "false", "0" -> false;
            default ->
                    throw new MargasException("Invalid potion effect definition: '%s'. '%s' is not a valid boolean like ['true', 'false', '1', '0'].".formatted(sourceValue, value));
        };
    }

    private int tryParseInt(final String sourceValue, final String value) throws MargasException {
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            throw new MargasException("Invalid potion effect definition: '%s'. '%s' is not a number.".formatted(sourceValue, value), e);
        }
    }
}
