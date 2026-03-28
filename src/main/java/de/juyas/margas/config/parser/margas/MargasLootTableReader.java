package de.juyas.margas.config.parser.margas;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.MargasIdentifier;
import de.juyas.margas.api.MargasType;
import de.juyas.margas.api.config.ConfigSectionReader;
import de.juyas.margas.api.config.ValueGenerator;
import de.juyas.margas.api.config.ValueProvider;
import de.juyas.margas.api.config.WeightedList;
import de.juyas.margas.api.loot.MargasItem;
import de.juyas.margas.api.loot.MargasLootTable;
import de.juyas.margas.api.manager.MargasManager;
import de.juyas.margas.config.DefaultValueProvider;
import de.juyas.margas.config.DefaultWeightedList;
import de.juyas.margas.config.parser.IdentifierListReader;
import de.juyas.margas.config.parser.IdentifierReader;
import de.juyas.margas.config.parser.InlineWrapperReader;
import de.juyas.margas.config.parser.WeightedListReader;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Class MargasLootTableReader to read loot tables from a configuration section.
 */
public class MargasLootTableReader implements ConfigSectionReader<MargasLootTable> {

    /**
     * The name of the field containing the items of the loot table.
     */
    private static final String FIELD_ITEMS = "items";

    /**
     * The name of the field containing the parent of the loot table.
     */
    private static final String FIELD_PARENT = "parent";

    /**
     * The margas loot table manager.
     */
    private final MargasManager<MargasLootTable> margasLootTableManager;

    /**
     * The margas item manager.
     */
    private final MargasManager<MargasItem> margasItemManager;

    /**
     * Creates a new instance of MargasLootTableReader.
     *
     * @param margasLootTableManager the margas loot table manager
     * @param margasItemManager      the margas item manager
     */
    public MargasLootTableReader(final MargasManager<MargasLootTable> margasLootTableManager, final MargasManager<MargasItem> margasItemManager) {
        super();
        this.margasLootTableManager = margasLootTableManager;
        this.margasItemManager = margasItemManager;
    }

    @Override
    public ValueProvider<MargasLootTable> read(final ConfigurationSection section, final String path) throws MargasException {
        if (!section.isConfigurationSection(path)) {
            throw new MargasException("Invalid margas loot table definition in section '%s' at path '%s'.".formatted(section.getCurrentPath(), path));
        }
        final ConfigurationSection lootTableSection = section.getConfigurationSection(path);
        if (lootTableSection == null) {
            throw new MargasException("Missing margas loot table definition in section '%s' at path '%s'.".formatted(section.getCurrentPath(), path));
        }

        final IdentifierListReader parentReader = new IdentifierListReader();
        final LootTableIdentifier identifier = new LootTableIdentifier(lootTableSection.getName());
        final WeightedListReader<MargasItem> weightedListReader = new WeightedListReader<>(new InlineWrapperReader<>("Item", new MargasItemReader(), margasItemManager));
        final ValueProvider<WeightedList<ValueProvider<MargasItem>>> itemList = weightedListReader.read(lootTableSection, FIELD_ITEMS);
        final List<MargasIdentifier> parents = lootTableSection.isList(FIELD_PARENT) ? parentReader.read(lootTableSection, FIELD_PARENT) : new ArrayList<>();
        if (lootTableSection.isString(FIELD_PARENT)) {
            parents.add(new IdentifierReader().read(lootTableSection, FIELD_PARENT));
        }

        final List<ValueProvider<MargasLootTable>> parentTables = new ArrayList<>(margasLootTableManager.get(parents));
        final ValueProvider<WeightedList<ValueProvider<MargasItem>>> resultingTable = combine(parentTables, itemList);

        return new DefaultValueProvider<>(useDefault -> new DefaultMargasLootTable(identifier, parents, resultingTable.generate(useDefault)), false);
    }

    private ValueProvider<WeightedList<ValueProvider<MargasItem>>> combine(final List<ValueProvider<MargasLootTable>> tables,
                                                                           final ValueProvider<WeightedList<ValueProvider<MargasItem>>> additionalItems) {
        final List<ValueProvider<WeightedList<ValueProvider<MargasItem>>>> providers =
                new ArrayList<>(tables.stream().map(provider -> provider.map(MargasLootTable::items)).toList());
        providers.add(additionalItems);
        final ValueGenerator<WeightedList<ValueProvider<MargasItem>>> valueGenerator = useDefault -> {
            final DefaultWeightedList<ValueProvider<MargasItem>> valueProviderDefaultWeightedList = new DefaultWeightedList<>();
            for (final ValueProvider<WeightedList<ValueProvider<MargasItem>>> provider : providers) {
                final WeightedList<ValueProvider<MargasItem>> generate = provider.generate(useDefault);
                valueProviderDefaultWeightedList.add(generate);
            }
            return valueProviderDefaultWeightedList;
        };
        return new DefaultValueProvider<>(valueGenerator, false);
    }

    private record DefaultMargasLootTable(MargasIdentifier identifier, List<MargasIdentifier> parents,
                                          WeightedList<ValueProvider<MargasItem>> items) implements MargasLootTable {
    }

    private record LootTableIdentifier(String name) implements MargasIdentifier {

        @Override
        public MargasType type() {
            return MargasType.LOOT_TABLE;
        }
    }

}
