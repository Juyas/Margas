package de.juyas.margas.config.parser.bukkit;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.config.ConfigSectionReader;
import de.juyas.margas.api.config.ValueProvider;
import de.juyas.margas.config.parser.SectionListReader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        final SectionListReader<Map.Entry<Enchantment, Integer>> enchantmentReader = new SectionListReader<>(new EnchantmentReader());
        final ValueProvider<List<Map.Entry<Enchantment, Integer>>> valueProvider = enchantmentReader.read(section, path);
        return valueProvider.map(this::convert);
    }

    private Map<Enchantment, Integer> convert(final List<Map.Entry<Enchantment, Integer>> list) {
        return list.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
