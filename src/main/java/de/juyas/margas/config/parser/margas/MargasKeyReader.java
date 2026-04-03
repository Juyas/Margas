package de.juyas.margas.config.parser.margas;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.MargasIdentifier;
import de.juyas.margas.api.MargasType;
import de.juyas.margas.api.config.ConfigSectionReader;
import de.juyas.margas.api.config.TextValue;
import de.juyas.margas.api.config.ValueProvider;
import de.juyas.margas.api.loot.MargasKey;
import de.juyas.margas.config.DefaultValueProvider;
import de.juyas.margas.config.EmptyTextValue;
import de.juyas.margas.config.parser.EnumReader;
import de.juyas.margas.config.parser.TextReader;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Class MargasKeyReader to read a margas key from a configuration section at a given path.
 */
public class MargasKeyReader implements ConfigSectionReader<MargasKey> {

    /**
     * The name of the field containing the type of the item.
     */
    private static final String FIELD_ITEM_TYPE = "item";

    /**
     * The name of the field containing the name of the item.
     */
    private static final String FIELD_NAME = "name";

    /**
     * The name of the field containing the description of the item.
     */
    private static final String FIELD_DESCRIPTION = "description";

    /**
     * The name of the field containing the enchantment state of the item.
     */
    private static final String FIELD_ENCHANTED = "enchanted";

    /**
     * Creates a new instance of MargasKeyReader.
     */
    public MargasKeyReader() {
        super();
    }

    @Override
    public ValueProvider<MargasKey> read(final ConfigurationSection section, final String path) throws MargasException {
        if (!section.isConfigurationSection(path)) {
            throw new MargasException("Invalid margas key definition in section '%s' at path '%s'.".formatted(section.getCurrentPath(), path));
        }
        final ConfigurationSection keySection = section.getConfigurationSection(path);
        if (keySection == null) {
            throw new MargasException("Missing margas key definition in section '%s' at path '%s'.".formatted(section.getCurrentPath(), path));
        }

        final EnumReader<Material> typeReader = new EnumReader<>(Material.class);
        final TextReader textReader = new TextReader();

        final KeyIdentifier identifier = new KeyIdentifier(keySection.getName());
        final Material type = typeReader.read(keySection, FIELD_ITEM_TYPE);
        final ValueProvider<TextValue> name = textReader.read(keySection, FIELD_NAME);
        final ValueProvider<TextValue> description = keySection.contains(FIELD_DESCRIPTION) ? textReader.read(keySection, FIELD_DESCRIPTION) : new DefaultValueProvider<>(new EmptyTextValue());
        final boolean enchanted = keySection.getBoolean(FIELD_ENCHANTED, false);

        return new DefaultValueProvider<>(useDefault -> new DefaultMargasKey(identifier, type, name.generate(useDefault), description.generate(useDefault), enchanted), false);
    }

    private record DefaultMargasKey(KeyIdentifier identifier, Material type, TextValue name,
                                    TextValue description, boolean enchanted) implements MargasKey {
    }

    /**
     * KeyIdentifier record as a default implementation of MargasIdentifier.
     *
     * @param name the name of the identifier
     */
    private record KeyIdentifier(String name) implements MargasIdentifier<MargasKey> {

        @Override
        public MargasType<MargasKey> type() {
            return MargasType.CHEST_KEY;
        }
    }
}
