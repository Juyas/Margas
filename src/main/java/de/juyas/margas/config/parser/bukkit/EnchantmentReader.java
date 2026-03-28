package de.juyas.margas.config.parser.bukkit;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.config.ConfigSectionReader;
import de.juyas.margas.api.config.ValueProvider;
import de.juyas.margas.config.DefaultValueProvider;
import de.juyas.margas.config.parser.NumberReader;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import java.util.Map;

/**
 * Class EnchantmentReader to read an enchantment from a configuration section at a given path.
 */
public class EnchantmentReader implements ConfigSectionReader<Map.Entry<Enchantment, Integer>> {

    /**
     * Number of parts of which an enchantment is composed in an inline definition.
     */
    private static final int INLINE_SPLIT_PARTS = 2;

    /**
     * The name of the field containing the type of the enchantment.
     */
    private static final String FIELD_TYPE = "type";

    /**
     * The name of the field containing the level of the enchantment.
     */
    private static final String FIELD_LEVEL = "level";

    /**
     * Creates a new instance of EnchantmentReader.
     */
    public EnchantmentReader() {
        super();
    }

    @Override
    public ValueProvider<Map.Entry<Enchantment, Integer>> read(final ConfigurationSection section, final String path) throws MargasException {
        if (section.isConfigurationSection(path)) {
            return parseSection(section, path);
        }
        if (section.isString(path)) {
            return parseInline(section, path);
        }
        throw new MargasException("Invalid enchantment definition in section '%s' at path '%s'.".formatted(section.getCurrentPath(), path));
    }

    private ValueProvider<Map.Entry<Enchantment, Integer>> parseSection(final ConfigurationSection section, final String path) throws MargasException {
        final ConfigurationSection enchantmentSection = section.getConfigurationSection(path);
        if (enchantmentSection == null) {
            throw new MargasException("Missing enchantment definition '%s' at path '%s'.".formatted(path, section.getCurrentPath()));
        }
        final String enchantmentType = enchantmentSection.getString(FIELD_TYPE);
        if (enchantmentType == null) {
            throw new MargasException("Missing enchantment 'type' at path '%s'.".formatted(enchantmentSection.getCurrentPath()));
        }
        final Enchantment enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.minecraft(enchantmentType));
        if (enchantment == null) {
            throw new MargasException("Invalid enchantment definition at path '%s'. Unknown enchantment type '%s'.".formatted(enchantmentSection.getCurrentPath(), enchantmentType));
        }
        final ValueProvider<Number> level = parseLevel(enchantmentSection);
        return new DefaultValueProvider<>(useDefault -> Map.entry(enchantment, level.generate(useDefault).intValue()), level.isStatic());
    }

    private ValueProvider<Map.Entry<Enchantment, Integer>> parseInline(final ConfigurationSection section, final String path) throws MargasException {
        final String value = section.getString(path);
        if (value == null) {
            throw new MargasException("Missing enchantment definition in section '%s' at path '%s'.".formatted(section.getCurrentPath(), path));
        }
        final String[] split = value.split(":");
        if (split.length != INLINE_SPLIT_PARTS) {
            throw new MargasException("Invalid enchantment definition at path '%s'.".formatted(path));
        }
        final Enchantment enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.minecraft(split[0]));
        if (enchantment == null) {
            throw new MargasException("Invalid enchantment definition at path '%s'. Unknown enchantment type '%s'.".formatted(path, split[0]));
        }
        final int level = Integer.parseInt(split[1]);
        return new DefaultValueProvider<>(Map.entry(enchantment, level));
    }

    private ValueProvider<Number> parseLevel(final ConfigurationSection section) throws MargasException {
        final NumberReader reader = new NumberReader(1, 255);
        return reader.read(section, FIELD_LEVEL);
    }

}
