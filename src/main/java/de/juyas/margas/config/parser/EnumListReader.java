package de.juyas.margas.config.parser;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.config.ConfigValueReader;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

/**
 * Class EnumReader to read an enumeration from a configuration section at a given path.
 */
public class EnumListReader<T extends Enum<T>> implements ConfigValueReader<List<T>> {

    /**
     * The class of the enum to read from the configuration.
     */
    private final Class<T> enumClass;

    /**
     * Creates a new instance of EnumReader for the given enum class.
     *
     * @param enumClass the class of the enum to read from the configuration
     */
    public EnumListReader(final Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public List<T> read(final ConfigurationSection section, final String path) throws MargasException {
        final List<String> valueList = section.getStringList(path);
        if (valueList.isEmpty()) {
            throw new MargasException("Enum-list definition empty at path '%s' in section '%s'.".formatted(path, section.getCurrentPath()));
        }
        try {
            return valueList.stream().map(value -> Enum.valueOf(enumClass, value)).toList();
        } catch (final IllegalArgumentException e) {
            throw new MargasException("Invalid enum-list definition at path '%s'. '%s' contains invalid enum value for type '%s'".formatted(path, String.join(",", valueList), enumClass.getSimpleName()));
        }
    }

}
