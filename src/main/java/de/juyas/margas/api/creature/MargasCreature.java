package de.juyas.margas.api.creature;

import de.juyas.margas.api.MargasElement;
import de.juyas.margas.api.MargasType;
import de.juyas.margas.api.loot.MargasItem;
import de.juyas.margas.api.loot.MargasLootTable;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;
import java.util.Map;

/**
 * Interface MargasCreature represents a creature configured in the section for {@link MargasType#CREATURE}.
 */
public interface MargasCreature extends MargasElement {

    /**
     * Returns the type of the creature.
     *
     * @return the type of the creature
     */
    EntityType type();

    /**
     * Returns the effects of the creature.
     *
     * @return the effects of the creature
     */
    List<MargasCreatureEffect> effects();

    /**
     * Returns the equipment of the creature.
     *
     * @return the equipment of the creature
     */
    Map<EquipmentSlot, MargasItem> equipment();

    /**
     * Returns the loot table of the creature.
     *
     * @return the loot table of the creature
     */
    MargasLootTable lootTable();

    /**
     * Returns the loot tables for special damage causes to override the default loot table of the creature.
     *
     * @return the loot tables for special damage causes
     */
    Map<EntityDamageEvent.DamageCause, MargasLootTable> specialLootTables();

    /**
     * Returns the attribute modifiers of the creature.
     *
     * @return the attribute modifiers of the creature
     */
    List<MargasCreatureAttributeModifier> attributeModifiers();
}
