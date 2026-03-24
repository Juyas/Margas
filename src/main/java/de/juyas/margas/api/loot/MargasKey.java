package de.juyas.margas.api.loot;

import de.juyas.margas.api.MargasElement;
import de.juyas.margas.api.MargasType;
import de.juyas.margas.api.config.TextValue;
import org.bukkit.Material;

/**
 * Interface MargasKey represents a key configured in the section for {@link MargasType#CHEST_KEY}.
 */
public interface MargasKey extends MargasElement {

    /**
     * Returns the item type of the key.
     *
     * @return the type of the key
     */
    Material type();

    /**
     * Returns the name of the key.
     *
     * @return the name of the key
     */
    TextValue name();

    /**
     * Returns the description of the key.
     *
     * @return the description of the key
     */
    TextValue description();

    /**
     * Returns if the key is enchanted.
     *
     * @return true if the key is enchanted, false otherwise
     */
    boolean enchanted();

}
