package de.juyas.margas.api;

import de.juyas.margas.api.creature.MargasCreature;
import de.juyas.margas.api.creature.MargasCreatureAttributeModifier;
import de.juyas.margas.api.creature.MargasCreatureEffect;
import de.juyas.margas.api.group.MargasArea;
import de.juyas.margas.api.loot.MargasChest;
import de.juyas.margas.api.loot.MargasItem;
import de.juyas.margas.api.loot.MargasKey;
import de.juyas.margas.api.loot.MargasLootTable;

/**
 * Interface MargasType to represent all types of elements loaded from a configuration.
 *
 * @param <T> the type of the elements of this type
 */
public interface MargasType<T extends MargasElement<T>> {

    /**
     * This type represents an entity that supposed to be spawned in margas areas.
     */
    MargasType<MargasCreature> CREATURE = new MargasTypeImpl<>(MargasCreature.class, "mob", "mobs");

    /**
     * This type represents an effect that supposed to be applied to or by margas creatures.
     */
    MargasType<MargasCreatureEffect> CREATURE_EFFECT = new MargasTypeImpl<>(MargasCreatureEffect.class, "effect", "effects");

    /**
     * This type represents an attribute that supposed to modify the attributes of margas creatures.
     */
    MargasType<MargasCreatureAttributeModifier> CREATURE_ATTRIBUTE = new MargasTypeImpl<>(MargasCreatureAttributeModifier.class, "attribute", "attributes");

    /**
     * This type represents an item that supposed to be used in margas areas as loot or equipment.
     */
    MargasType<MargasItem> ITEM = new MargasTypeImpl<>(MargasItem.class, "item", "items");

    /**
     * This type represents a margas area.
     */
    MargasType<MargasArea> AREA = new MargasTypeImpl<>(MargasArea.class, "area", "areas");

    /**
     * This type represents a chest that be placed in margas areas and populated with items for players to loot.
     */
    MargasType<MargasChest> CHEST = new MargasTypeImpl<>(MargasChest.class, "chest", "chests");

    /**
     * This type represents a key that be treated as loot as well as a key to open loot chests.
     */
    MargasType<MargasKey> CHEST_KEY = new MargasTypeImpl<>(MargasKey.class, "key", "keys");

    /**
     * This type represents a loot table that be used to randomly generate items and manipulate the chances.
     */
    MargasType<MargasLootTable> LOOT_TABLE = new MargasTypeImpl<>(MargasLootTable.class, "table", "tables");

    /**
     * Returns the class of the element of this type.
     *
     * @return the class of the element of this type
     */
    Class<T> elementClass();

    /**
     * Returns the name of the type.
     *
     * @return the name of the type
     */
    String name();

    /**
     * Returns the section name of the type.
     *
     * @return the section name of the type
     */
    String sectionName();

    /**
     * Implementation class for MargasType.
     * Used only internally.
     *
     * @param <T>          the type of the element of this type
     * @param elementClass the class of the element of this type
     * @param name         the name of the type
     * @param sectionName  the section name of the type
     */
    record MargasTypeImpl<T extends MargasElement<T>>(Class<T> elementClass, String name,
                                                      String sectionName) implements MargasType<T> {

    }

}
