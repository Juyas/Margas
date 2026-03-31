package de.juyas.margas.config.parser;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.config.ConfigValueReader;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Locale;

/**
 * Class EnumReader to read an enumeration from a configuration section at a given path.
 *
 * @param <T> the type of the enumeration
 */
public class EnumReader<T extends Enum<T>> implements ConfigValueReader<T> {

    /**
     * The class of the enum to read from the configuration.
     */
    private final Class<T> enumClass;

    /**
     * Creates a new instance of EnumReader for the given enum class.
     *
     * @param enumClass the class of the enum to read from the configuration
     */
    public EnumReader(final Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public T read(final ConfigurationSection section, final String path) throws MargasException {
        final String value = section.getString(path);
        if (value == null) {
            throw new MargasException("Enum definition not found at path '%s' in section '%s'.".formatted(path, section.getCurrentPath()));
        }
        try {
            return Enum.valueOf(enumClass, value.toUpperCase(Locale.ROOT));
        } catch (final IllegalArgumentException e) {
            throw new MargasException("Invalid enum definition at path '%s'. '%s' is not a valid enum value for type '%s'".formatted(path, value, enumClass.getSimpleName()), e);
        }
    }

}
