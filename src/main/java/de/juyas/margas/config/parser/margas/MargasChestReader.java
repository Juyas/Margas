package de.juyas.margas.config.parser.margas;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.MargasIdentifier;
import de.juyas.margas.api.MargasType;
import de.juyas.margas.api.config.ConfigSectionReader;
import de.juyas.margas.api.config.ValueProvider;
import de.juyas.margas.api.loot.MargasChest;
import de.juyas.margas.api.loot.MargasKey;
import de.juyas.margas.api.loot.MargasLootTable;
import de.juyas.margas.api.manager.MargasManager;
import de.juyas.margas.config.DefaultValueProvider;
import de.juyas.margas.config.parser.EnumReader;
import de.juyas.margas.config.parser.IdentifierListReader;
import de.juyas.margas.config.parser.IdentifierReader;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Optional;

/**
 * Class MargasChestReader to read a margas chest from a configuration section.
 */
public class MargasChestReader implements ConfigSectionReader<MargasChest> {

    /**
     * The name of the field containing the type of the chest.
     */
    private static final String FIELD_CHEST_TYPE = "type";

    /**
     * The name of the field containing the loot table of the chest.
     */
    private static final String FIELD_LOOT_TABLE = "loot";

    /**
     * The name of the field containing the keys to open the chest.
     */
    private static final String FIELD_KEY_LIST = "keys";

    /**
     * The margas loot table manager.
     */
    private final MargasManager<MargasLootTable> lootTableManager;

    /**
     * Creates a new instance of MargasChestReader.
     *
     * @param lootTableManager the margas loot table manager
     */
    public MargasChestReader(final MargasManager<MargasLootTable> lootTableManager) {
        super();
        this.lootTableManager = lootTableManager;
    }

    @Override
    public ValueProvider<MargasChest> read(final ConfigurationSection section, final String path) throws MargasException {
        if (!section.isConfigurationSection(path)) {
            throw new MargasException("Invalid margas chest definition in section '%s' at path '%s'.".formatted(section.getCurrentPath(), path));
        }
        final ConfigurationSection chestSection = section.getConfigurationSection(path);
        if (chestSection == null) {
            throw new MargasException("Missing margas chest definition in section '%s' at path '%s'.".formatted(section.getCurrentPath(), path));
        }

        final EnumReader<Material> chestTypeReader = new EnumReader<>(Material.class);
        final IdentifierReader<MargasLootTable> tableReader = new IdentifierReader<>(MargasType.LOOT_TABLE);
        final IdentifierListReader<MargasKey> keyReader = new IdentifierListReader<>(MargasType.CHEST_KEY);

        final ChestIdentifier identifier = new ChestIdentifier(chestSection.getName());
        final Material chestType = chestTypeReader.read(chestSection, FIELD_CHEST_TYPE);
        final List<MargasIdentifier<MargasKey>> keys = keyReader.read(chestSection, FIELD_KEY_LIST);
        final MargasIdentifier<MargasLootTable> table = tableReader.read(chestSection, FIELD_LOOT_TABLE);
        final Optional<ValueProvider<MargasLootTable>> margasLootTable = lootTableManager.get(table);
        if (margasLootTable.isEmpty()) {
            throw new MargasException("Invalid loot table definition in section '%s' at path '%s'. Identifier '%s' not found.".formatted(section.getCurrentPath(), path + "." + FIELD_LOOT_TABLE, table.full()));
        }

        return new DefaultValueProvider<>(useDefault -> new DefaultMargasChest(identifier, chestType, margasLootTable.get().generate(useDefault), keys), false);
    }

    private record DefaultMargasChest(ChestIdentifier identifier, Material type,
                                      MargasLootTable lootTable,
                                      List<MargasIdentifier<MargasKey>> keys) implements MargasChest {
    }

    private record ChestIdentifier(String name) implements MargasIdentifier<MargasChest> {

        @Override
        public MargasType<MargasChest> type() {
            return MargasType.CHEST;
        }
    }
}
