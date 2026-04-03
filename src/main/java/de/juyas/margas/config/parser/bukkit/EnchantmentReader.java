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
     * Creates a new instance of EnchantmentReader.
     */
    public EnchantmentReader() {
        super();
    }

    @Override
    public ValueProvider<Map.Entry<Enchantment, Integer>> read(final ConfigurationSection section, final String path) throws MargasException {
        final NumberReader numberReader = new NumberReader(0, 255);
        final ValueProvider<Number> level = numberReader.read(section, path);
        final Enchantment enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.minecraft(path));
        if (enchantment != null) {
            return new DefaultValueProvider<>(useDefault -> Map.entry(enchantment, level.generate(useDefault).intValue()), level.isStatic());
        }
        throw new MargasException("Invalid enchantment definition in section '%s' at path '%s'.".formatted(section.getCurrentPath(), path));
    }

}
