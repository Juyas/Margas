package de.juyas.margas.config.parser.margas;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.MargasIdentifier;
import de.juyas.margas.api.MargasType;
import de.juyas.margas.api.config.ConfigSectionReader;
import de.juyas.margas.api.config.ConfigValueReader;
import de.juyas.margas.api.config.ValueProvider;
import de.juyas.margas.api.creature.MargasCreature;
import de.juyas.margas.api.creature.MargasCreatureAttributeModifier;
import de.juyas.margas.api.creature.MargasCreatureEffect;
import de.juyas.margas.api.loot.MargasItem;
import de.juyas.margas.api.loot.MargasLootTable;
import de.juyas.margas.api.manager.MargasManager;
import de.juyas.margas.config.DefaultValueProvider;
import de.juyas.margas.config.parser.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.*;

/**
 * Class MargasCreatureReader to read a creature from a configuration section.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class MargasCreatureReader implements ConfigSectionReader<MargasCreature> {

    /**
     * The name of the field containing the type of the creature.
     */
    private static final String FIELD_TYPE = "type";

    /**
     * The name of the field containing the effects of the creature.
     */
    private static final String FIELD_EFFECTS = "effects";

    /**
     * The name of the field containing the equipment of the creature.
     */
    private static final String FIELD_EQUIPMENT = "equipment";

    /**
     * The name of the field containing the loot tables of the creature.
     */
    private static final String FIELD_LOOT_TABLE = "loot";

    /**
     * The name of the field containing the default loot table of the creature.
     * Listed under the loot table field.
     */
    private static final String FIELD_LOOT_DEFAULT_TABLE = "default";

    /**
     * The name of the field containing the attribute modifiers of the creature.
     */
    private static final String FIELD_ATTRIBUTE_MODIFIERS = "modifiers";

    /**
     * The manager of creature effects.
     */
    private final MargasManager<MargasCreatureEffect> creatureEffectManager;

    /**
     * The manager of creature attribute modifiers.
     */
    private final MargasManager<MargasCreatureAttributeModifier> creatureAttributeModifierManager;

    /**
     * The manager of loot tables.
     */
    private final MargasManager<MargasLootTable> lootTableManager;

    /**
     * The manager of items.
     */
    private final MargasManager<MargasItem> itemManager;

    /**
     * Creates a new instance of MargasCreatureReader.
     *
     * @param creatureEffectManager            the manager of creature effects
     * @param creatureAttributeModifierManager the manager of creature attribute modifiers
     * @param lootTableManager                 the manager of loot tables
     * @param itemManager                      the manager of items
     */
    public MargasCreatureReader(final MargasManager<MargasCreatureEffect> creatureEffectManager, final MargasManager<MargasCreatureAttributeModifier> creatureAttributeModifierManager, final MargasManager<MargasLootTable> lootTableManager, final MargasManager<MargasItem> itemManager) {
        this.creatureEffectManager = creatureEffectManager;
        this.creatureAttributeModifierManager = creatureAttributeModifierManager;
        this.lootTableManager = lootTableManager;
        this.itemManager = itemManager;
    }

    @Override
    public ValueProvider<MargasCreature> read(final ConfigurationSection section, final String path) throws MargasException {
        if (!section.isConfigurationSection(path)) {
            throw new MargasException("Invalid creature definition in section '%s' at path '%s'.".formatted(section.getCurrentPath(), path));
        }
        final ConfigurationSection creatureSection = section.getConfigurationSection(path);
        if (creatureSection == null) {
            throw new MargasException("Missing creature definition in section '%s' at path '%s'.".formatted(section.getCurrentPath(), path));
        }
        if (!creatureSection.contains(FIELD_LOOT_TABLE)) {
            throw new MargasException("Invalid creature definition in section '%s' at path '%s'. Missing loot table definitions at '%s'.".formatted(section.getCurrentPath(), path, FIELD_LOOT_TABLE));
        }
        if (!creatureSection.contains(FIELD_LOOT_TABLE + '.' + FIELD_LOOT_DEFAULT_TABLE)) {
            throw new MargasException("Invalid creature definition in section '%s' at path '%s'. Missing default loot table definition at '%s'.".formatted(section.getCurrentPath(), path, FIELD_LOOT_TABLE + '.' + FIELD_LOOT_DEFAULT_TABLE));
        }

        final EnumReader<EntityType> typeReader = new EnumReader<>(EntityType.class);
        final SectionListReader<MargasCreatureAttributeModifier> attributeModifierReader = new SectionListReader<>(
                new InlineWrapperReader<>(MargasType.CREATURE_ATTRIBUTE, new MargasCreatureAttributeModifierReader(), creatureAttributeModifierManager));
        final FallbackParser<ValueProvider<List<MargasCreatureEffect>>> creatureEffectReader = getCreatureEffectReader(creatureEffectManager);
        final InlineWrapperReader<MargasLootTable> lootTableReader = new InlineWrapperReader<>(MargasType.LOOT_TABLE, new MargasLootTableReader(lootTableManager, itemManager), lootTableManager);
        final SectionMappedListReader<MargasLootTable> lootTablesReader = new SectionMappedListReader<>(lootTableReader);
        final SectionMappedListReader<MargasItem> equipmentReader = new SectionMappedListReader<>(new InlineWrapperReader<>(MargasType.ITEM, new MargasItemReader(), itemManager));

        final CreatureIdentifier creatureIdentifier = new CreatureIdentifier(creatureSection.getName());
        final EntityType entityType = typeReader.read(creatureSection, FIELD_TYPE);
        final ValueProvider<List<MargasCreatureEffect>> effects = creatureEffectReader.read(creatureSection, FIELD_EFFECTS);
        final ValueProvider<List<MargasCreatureAttributeModifier>> attributeModifiers = attributeModifierReader.read(creatureSection, FIELD_ATTRIBUTE_MODIFIERS);
        final ValueProvider<List<Map.Entry<String, MargasLootTable>>> lootTableEntries = lootTablesReader.read(creatureSection, FIELD_LOOT_TABLE);
        final ValueProvider<MargasLootTable> defaultLootTable = lootTableReader.read(creatureSection, FIELD_LOOT_TABLE + '.' + FIELD_LOOT_DEFAULT_TABLE);
        final ValueProvider<List<Map.Entry<String, MargasItem>>> equipmentEntries = equipmentReader.read(creatureSection, FIELD_EQUIPMENT);
        final ValueProvider<Map<EntityDamageEvent.DamageCause, MargasLootTable>> lootTables = polishList(EntityDamageEvent.DamageCause.class, lootTableEntries);
        final ValueProvider<Map<EquipmentSlot, MargasItem>> equipment = polishList(EquipmentSlot.class, equipmentEntries);

        return new DefaultValueProvider<>(useDefault -> new DefaultCreature(creatureIdentifier, entityType,
                effects.generate(useDefault), equipment.generate(useDefault), defaultLootTable.generate(useDefault), lootTables.generate(useDefault), attributeModifiers.generate(useDefault)), false);
    }

    private FallbackParser<ValueProvider<List<MargasCreatureEffect>>> getCreatureEffectReader(final MargasManager<MargasCreatureEffect> creatureEffectManager) {
        final ConfigValueReader<ValueProvider<List<MargasCreatureEffect>>> sectionListReader = new SectionListReader<>(new InlineWrapperReader<>(MargasType.CREATURE_EFFECT, new MargasCreatureEffectReader(), creatureEffectManager));
        final ConfigValueReader<ValueProvider<List<MargasCreatureEffect>>> identifierList = new IdentifierListReader<>(MargasType.CREATURE_EFFECT).map(this::mapperFunction);
        return new FallbackParser<>(sectionListReader, identifierList, "Could not parse creature effects. Invalid list format.");
    }

    private ValueProvider<List<MargasCreatureEffect>> mapperFunction(final List<MargasIdentifier<MargasCreatureEffect>> identifierList) {
        return new DefaultValueProvider<>(useDefault -> {
            final List<MargasCreatureEffect> list = new ArrayList<>();
            for (final ValueProvider<MargasCreatureEffect> provider : creatureEffectManager.get(identifierList)) {
                final MargasCreatureEffect generate = provider.generate(useDefault);
                list.add(generate);
            }
            return list;
        }, false);
    }

    private <E extends Enum<E>, T> ValueProvider<Map<E, T>> polishList(final Class<E> enumClass, final ValueProvider<List<Map.Entry<String, T>>> lootTableEntries) {
        return lootTableEntries.map(list -> list.stream()
                .map(entry -> parseEnum(enumClass, entry.getKey()).map(cause -> Map.entry(cause, entry.getValue())))
                .flatMap(Optional::stream)
                .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), HashMap::putAll)
        );
    }

    private <E extends Enum<E>> Optional<E> parseEnum(final Class<E> enumClass, final String stringValue) {
        try {
            final E value = Enum.valueOf(enumClass, stringValue.toUpperCase(Locale.ROOT));
            return Optional.of(value);
        } catch (final IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private record DefaultCreature(CreatureIdentifier identifier, EntityType type,
                                   List<MargasCreatureEffect> effects,
                                   Map<EquipmentSlot, MargasItem> equipment, MargasLootTable lootTable,
                                   Map<EntityDamageEvent.DamageCause, MargasLootTable> specialLootTables,
                                   List<MargasCreatureAttributeModifier> attributeModifiers) implements MargasCreature {

    }

    private record CreatureIdentifier(String name) implements MargasIdentifier<MargasCreature> {

        @Override
        public MargasType<MargasCreature> type() {
            return MargasType.CREATURE;
        }
    }
}
