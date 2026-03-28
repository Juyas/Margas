package de.juyas.margas.config.parser.bukkit;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.config.ConfigSectionReader;
import de.juyas.margas.api.config.ValueProvider;
import de.juyas.margas.config.DefaultValueProvider;
import de.juyas.margas.config.parser.ListToSectionConverter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class EnchantmentListReader to read a list of enchantments.
 */
public class EnchantmentListReader implements ConfigSectionReader<Map<Enchantment, Integer>> {

    /**
     * Creates a new instance of EnchantmentListReader.
     */
    public EnchantmentListReader() {
        super();
    }

    @Override
    public ValueProvider<Map<Enchantment, Integer>> read(final ConfigurationSection section, final String path) throws MargasException {
        if (section.isConfigurationSection(path)) {
            return parseSection(section, path);
        }
        if (section.isList(path)) {
            return parseSection(ListToSectionConverter.convert(section, path), path);
        }
        throw new MargasException("Invalid enchantment list definition in section '%s' at path '%s'.".formatted(section.getCurrentPath(), path));
    }

    private ValueProvider<Map<Enchantment, Integer>> parseSection(final ConfigurationSection section, final String path) {
        return new DefaultValueProvider<>(useDefault -> parseMap(section, path, useDefault), false);
    }

    private Map<Enchantment, Integer> parseMap(final ConfigurationSection section, final String path, final boolean useDefault) throws MargasException {
        final EnchantmentReader enchantmentReader = new EnchantmentReader();
        final ConfigurationSection mapSection = section.getConfigurationSection(path);
        if (mapSection == null) {
            throw new MargasException("Invalid enchantment list definition in section '%s' at path '%s'.".formatted(section.getCurrentPath(), path));
        }
        final Set<String> keys = mapSection.getKeys(false);
        final Map<Enchantment, Integer> enchantments = new HashMap<>(keys.size());
        for (final String key : keys) {
            final ValueProvider<Map.Entry<Enchantment, Integer>> enchantmentData = enchantmentReader.read(mapSection, key);
            if (useDefault) {
                enchantments.put(enchantmentData.defaultValue().getKey(), enchantmentData.defaultValue().getValue());
            } else {
                enchantments.put(enchantmentData.generate().getKey(), enchantmentData.generate().getValue());
            }
        }
        return enchantments;
    }
}
