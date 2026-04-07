package de.juyas.margas.internal;

import de.juyas.margas.api.MargasElement;
import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.MargasType;
import de.juyas.margas.api.creature.MargasCreature;
import de.juyas.margas.api.creature.MargasCreatureAttributeModifier;
import de.juyas.margas.api.creature.MargasCreatureEffect;
import de.juyas.margas.api.loot.MargasChest;
import de.juyas.margas.api.loot.MargasItem;
import de.juyas.margas.api.loot.MargasKey;
import de.juyas.margas.api.loot.MargasLootTable;
import de.juyas.margas.config.parser.margas.*;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Class MargasElementManager
 */
public class MargasElementManager {

    /**
     * The loaded managers.
     */
    private final Map<MargasType<?>, BulkLoadingManager<?>> loadedManagers;

    /**
     * Creates a new instance of MargasElementManager.
     */
    public MargasElementManager() {
        final BulkLoadingManager<MargasKey> chestKeyManager = new BulkLoadingManager<>(MargasType.CHEST_KEY, manager -> new MargasKeyReader());
        final BulkLoadingManager<MargasItem> itemManager = new BulkLoadingManager<>(MargasType.ITEM, manager -> new MargasItemReader());
        final BulkLoadingManager<MargasLootTable> lootTableManager = new BulkLoadingManager<>(MargasType.LOOT_TABLE, manager -> new MargasLootTableReader(manager, itemManager));
        final BulkLoadingManager<MargasChest> chestManager = new BulkLoadingManager<>(MargasType.CHEST, manager -> new MargasChestReader(lootTableManager, itemManager));
        final BulkLoadingManager<MargasCreatureEffect> creatureEffectManager = new BulkLoadingManager<>(MargasType.CREATURE_EFFECT, manager -> new MargasCreatureEffectReader());
        final BulkLoadingManager<MargasCreatureAttributeModifier> attributeModifierManager = new BulkLoadingManager<>(MargasType.CREATURE_ATTRIBUTE, manager -> new MargasCreatureAttributeModifierReader());
        final BulkLoadingManager<MargasCreature> creatureManager = new BulkLoadingManager<>(MargasType.CREATURE, manager -> new MargasCreatureReader(creatureEffectManager, attributeModifierManager, lootTableManager, itemManager));
        this.loadedManagers = new HashMap<>();
        loadedManagers.put(MargasType.CHEST_KEY, chestKeyManager);
        loadedManagers.put(MargasType.ITEM, itemManager);
        loadedManagers.put(MargasType.LOOT_TABLE, lootTableManager);
        loadedManagers.put(MargasType.CHEST, chestManager);
        loadedManagers.put(MargasType.CREATURE_EFFECT, creatureEffectManager);
        loadedManagers.put(MargasType.CREATURE_ATTRIBUTE, attributeModifierManager);
        loadedManagers.put(MargasType.CREATURE, creatureManager);
    }

    /**
     * Returns the manager for the given type.
     *
     * @param margasType the margas type to identify the manager
     * @param <T>        the type of the element to be managed
     * @return the manager for the given type
     */
    public <T extends MargasElement<T>> BulkLoadingManager<T> get(final MargasType<T> margasType) {
        //noinspection unchecked
        return (BulkLoadingManager<T>) loadedManagers.get(margasType);
    }

    /**
     * Clears all loaded elements.
     */
    public void clear() {
        loadedManagers.values().forEach(BulkLoadingManager::clear);
    }

    /**
     * Prints information about the loaded elements.
     *
     * @param logger the logger to log messages to
     */
    public void printInfo(final Logger logger) {
        final String info = loadedManagers.entrySet().stream()
                .filter(entry -> !entry.getValue().all().isEmpty())
                .map(entry -> "%s elements of type %s".formatted(entry.getValue().all().size(), entry.getKey().name()))
                .collect(Collectors.joining(", "));
        logger.info("Loaded %s".formatted(info));
    }

    /**
     * Attempts to load the configuration and its elements for all managers.
     *
     * @param section the configuration section to load the elements from
     * @throws MargasException if an error occurs during loading the configuration or elements for any manager
     */
    public void loadConfiguration(final ConfigurationSection section) throws MargasException {
        clear();
        for (final BulkLoadingManager<?> manager : loadedManagers.values()) {
            manager.load(section);
        }
    }
}
