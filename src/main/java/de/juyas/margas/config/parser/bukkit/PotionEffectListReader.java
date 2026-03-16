package de.juyas.margas.config.parser.bukkit;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.config.ConfigSectionReader;
import de.juyas.margas.api.config.ValueGenerator;
import de.juyas.margas.api.config.ValueProvider;
import de.juyas.margas.config.DefaultValueProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class PotionEffectListReader
 */
public class PotionEffectListReader implements ConfigSectionReader<List<PotionEffect>> {

    /**
     * Creates a new instance of PotionEffectListReader.
     */
    public PotionEffectListReader() {
        super();
    }

    @Override
    public ValueProvider<List<PotionEffect>> read(final ConfigurationSection section, final String path) throws MargasException {
        if (section.isConfigurationSection(path)) {
            return parseSection(section, path);
        }
        if (section.isList(path)) {
            final List<Map<?, ?>> list = section.getMapList(path);
            final YamlConfiguration configuration = new YamlConfiguration();
            final ConfigurationSection configurationSection = configuration.createSection(path);
            for (final Map<?, ?> map : list) {
                map.keySet().forEach(key -> configurationSection.set(key.toString(), map.get(key)));
            }
            return parseSection(configurationSection, path);
        }
        throw new MargasException("Invalid potion effect list definition at path '%s'.".formatted(path));
    }

    private ValueProvider<List<PotionEffect>> parseSection(final ConfigurationSection section, final String path) throws MargasException {
        final PotionEffectReader potionEffectReader = new PotionEffectReader();
        final Set<String> keys = section.getKeys(false);
        final ValueGenerator<List<PotionEffect>> generator = () -> {
            final List<PotionEffect> list = new ArrayList<>(keys.size());
            for (final String key : keys) {
                final ConfigurationSection configurationSection = section.getConfigurationSection(key);
                if (configurationSection == null) {
                    throw new MargasException("Invalid potion effect list definition at path '%s'. Nothing at key: '%s'".formatted(path, key));
                }
                final PotionEffect generated = potionEffectReader.read(configurationSection, path).generate();
                list.add(generated);
            }
            return list;
        };
        return new DefaultValueProvider<>(generator.generate(), generator, false);
    }
}
