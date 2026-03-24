package de.juyas.margas.config.parser.margas;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.MargasIdentifier;
import de.juyas.margas.api.MargasType;
import de.juyas.margas.api.config.ConfigSectionReader;
import de.juyas.margas.api.config.TextValue;
import de.juyas.margas.api.config.ValueProvider;
import de.juyas.margas.api.loot.MargasItem;
import de.juyas.margas.config.DefaultValueProvider;
import de.juyas.margas.config.EmptyTextValue;
import de.juyas.margas.config.parser.EnumListReader;
import de.juyas.margas.config.parser.EnumReader;
import de.juyas.margas.config.parser.NumberReader;
import de.juyas.margas.config.parser.TextReader;
import de.juyas.margas.config.parser.bukkit.EnchantmentListReader;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Class MargasItemReader to read a margas item from a configuration section at a given path.
 */
public class MargasItemReader implements ConfigSectionReader<MargasItem> {

    /**
     * The name of the field containing the type of the item.
     */
    private static final String FIELD_TYPE = "type";

    /**
     * The name of the field containing the amount of the item.
     */
    private static final String FIELD_AMOUNT = "amount";

    /**
     * The name of the field containing the name of the item.
     */
    private static final String FIELD_NAME = "name";

    /**
     * The name of the field containing the description of the item.
     */
    private static final String FIELD_DESCRIPTION = "description";

    /**
     * The name of the field containing the enchantments of the item.
     */
    private static final String FIELD_ENCHANTMENTS = "enchantments";

    /**
     * The name of the field containing the flags of the item.
     */
    private static final String FIELD_FLAGS = "flags";

    /**
     * The name of the field containing the unbreakable status of the item.
     */
    private static final String FIELD_UNBREAKABLE = "unbreakable";

    /**
     * Creates a new instance of MargasItemReader.
     */
    public MargasItemReader() {
        super();
    }

    @Override
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public ValueProvider<MargasItem> read(final ConfigurationSection section, final String path) throws MargasException {
        if (!section.isConfigurationSection(path)) {
            throw new MargasException("Invalid margas item definition in section '%s' at path '%s'.".formatted(section.getCurrentPath(), path));
        }
        final ConfigurationSection itemSection = section.getConfigurationSection(path);
        if (itemSection == null) {
            throw new MargasException("Missing margas item definition in section '%s' at path '%s'.".formatted(section.getCurrentPath(), path));
        }

        final EnumReader<Material> typeReader = new EnumReader<>(Material.class);
        final TextReader textReader = new TextReader();
        final NumberReader amountReader = new NumberReader(1, 64);
        final EnumListReader<ItemFlag> flagReader = new EnumListReader<>(ItemFlag.class);
        final EnchantmentListReader enchantmentReader = new EnchantmentListReader();

        final ItemIdentifier identifier = new ItemIdentifier(itemSection.getName());
        final Material type = typeReader.read(itemSection, FIELD_TYPE);
        final ValueProvider<TextValue> name = itemSection.contains(FIELD_NAME) ? textReader.read(itemSection, FIELD_NAME) : new DefaultValueProvider<>(new EmptyTextValue());
        final ValueProvider<TextValue> description = itemSection.contains(FIELD_DESCRIPTION) ? textReader.read(itemSection, FIELD_DESCRIPTION) : new DefaultValueProvider<>(new EmptyTextValue());
        final ValueProvider<Number> amount = itemSection.contains(FIELD_AMOUNT) ? amountReader.read(itemSection, FIELD_AMOUNT) : new DefaultValueProvider<>(1);
        final Set<ItemFlag> flags = itemSection.contains(FIELD_FLAGS) ? EnumSet.copyOf(flagReader.read(itemSection, FIELD_FLAGS)) : Collections.emptySet();
        final ValueProvider<Map<Enchantment, Integer>> enchantments = itemSection.contains(FIELD_ENCHANTMENTS) ? enchantmentReader.read(itemSection, FIELD_ENCHANTMENTS) : new DefaultValueProvider<>(Collections.emptyMap());
        final boolean unbreakable = itemSection.getBoolean(FIELD_UNBREAKABLE, false);

        return new DefaultValueProvider<>(new DefaultMargasItem(identifier, type, amount.defaultValue().intValue(), name.defaultValue(), description.defaultValue(), enchantments.defaultValue(), flags, unbreakable),
                () -> new DefaultMargasItem(identifier, type, amount.generate().intValue(), name.generate(), description.generate(), enchantments.generate(), flags, unbreakable), false);
    }

    private record DefaultMargasItem(MargasIdentifier identifier, Material type, int amount, TextValue name,
                                     TextValue description, Map<Enchantment, Integer> enchantments,
                                     Set<ItemFlag> flags, boolean unbreakable) implements MargasItem {
    }

    private record ItemIdentifier(String name) implements MargasIdentifier {

        @Override
        public MargasType type() {
            return MargasType.ITEM;
        }
    }

}
