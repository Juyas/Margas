package de.juyas.margas.api.loot;

import de.juyas.margas.api.MargasElement;
import de.juyas.margas.api.MargasType;
import de.juyas.margas.api.config.TextValue;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.util.Map;
import java.util.Set;

/**
 * Interface MargasItem represents an item configured in the section {@link MargasType#ITEM}.
 */
public interface MargasItem extends MargasElement<MargasItem> {

    /**
     * Returns the material type of the item.
     *
     * @return the type of the item
     */
    Material type();

    /**
     * Returns the amount of the item.
     *
     * @return the amount of the item
     */
    int amount();

    /**
     * Returns if the item is unbreakable.
     *
     * @return true if the item is unbreakable, false otherwise
     */
    boolean unbreakable();

    /**
     * Returns the name of the item.
     *
     * @return the name of the item
     */
    TextValue name();

    /**
     * Returns the description of the item.
     *
     * @return the description of the item
     */
    TextValue description();

    /**
     * Returns the enchantments of the item.
     *
     * @return the enchantments of the item
     */
    Map<Enchantment, Integer> enchantments();

    /**
     * Returns the flags of the item.
     *
     * @return the flags of the item
     */
    Set<ItemFlag> flags();
}
