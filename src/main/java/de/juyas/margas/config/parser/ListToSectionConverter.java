package de.juyas.margas.config.parser;

import de.juyas.margas.api.MargasException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Class ListToSectionConverter to convert a list defined in a {@link ConfigurationSection} into
 * a {@link ConfigurationSection}.
 */
public final class ListToSectionConverter {

    /**
     * Is not supposed to be instantiated.
     */
    private ListToSectionConverter() {
    }

    /**
     * Converts a list of sections defined in a {@link ConfigurationSection} into a {@link ConfigurationSection}
     * at the identical path.
     * <p>
     * The resulting {@link ConfigurationSection} will contain subsections for each element of the list.
     *
     * @param configurationSection the configurationSection to read the list from at the given path
     * @param path                 the path to the list in the configurationSection
     * @return the {@link ConfigurationSection} parsed from the list
     * @throws MargasException if the path does not lead to a list
     */
    public static ConfigurationSection convert(final ConfigurationSection configurationSection, final String path) throws MargasException {
        if (configurationSection.isList(path)) {
            final List<Map<?, ?>> list = configurationSection.getMapList(path);
            final YamlConfiguration configuration = new YamlConfiguration();
            final ConfigurationSection resultSection = configuration.createSection(path);
            IntStream.range(0, list.size()).forEach(i -> resultSection.createSection("elem" + i, list.get(i)));
            return configuration;
        }
        throw new MargasException("Invalid list definition at path '%s'.".formatted(path));
    }

}
