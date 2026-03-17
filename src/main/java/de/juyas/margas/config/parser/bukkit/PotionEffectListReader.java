package de.juyas.margas.config.parser.bukkit;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.config.ConfigSectionReader;
import de.juyas.margas.api.config.ValueGenerator;
import de.juyas.margas.api.config.ValueProvider;
import de.juyas.margas.config.DefaultValueProvider;
import de.juyas.margas.config.parser.ListToSectionConverter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Class PotionEffectListReader to read a list of potion effects.
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
            return parseSection(ListToSectionConverter.convert(section, path), path);
        }
        throw new MargasException("Invalid potion effect list definition at path '%s'.".formatted(path));
    }

    private ValueProvider<List<PotionEffect>> parseSection(final ConfigurationSection section, final String path) throws MargasException {
        final ValueGenerator<List<PotionEffect>> generator = () -> parseList(section, path, false);
        return new DefaultValueProvider<>(parseList(section, path, true), generator, false);
    }

    private List<PotionEffect> parseList(final ConfigurationSection section, final String path, final boolean useDefault) throws MargasException {
        final PotionEffectReader potionEffectReader = new PotionEffectReader();
        final Set<String> keys = section.getKeys(false);
        final List<PotionEffect> list = new ArrayList<>(keys.size());
        for (final String key : keys) {
            final ConfigurationSection configurationSection = section.getConfigurationSection(key);
            if (configurationSection == null) {
                throw new MargasException("Invalid potion effect list definition at path '%s'. Nothing at key: '%s'".formatted(path, key));
            }
            final ValueProvider<PotionEffect> valueProvider = potionEffectReader.read(configurationSection, path);
            final PotionEffect generated = useDefault ? valueProvider.defaultValue() : valueProvider.generate();
            list.add(generated);
        }
        return list;
    }
}
