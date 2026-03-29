package de.juyas.margas.api.loot;

import de.juyas.margas.api.MargasElement;
import de.juyas.margas.api.MargasIdentifier;
import de.juyas.margas.api.MargasType;
import org.bukkit.Material;

import java.util.List;

/**
 * Interface MargasChest represents a chest configured in the section for {@link MargasType#CHEST}.
 */
public interface MargasChest extends MargasElement<MargasChest> {

    /**
     * Returns the type of the chest.
     *
     * @return the type of the chest
     */
    Material type();

    /**
     * Returns the loot table of the chest.
     *
     * @return the loot table of the chest
     */
    MargasLootTable lootTable();

    /**
     * Returns the keys to open the chest.
     *
     * @return the keys to open the chest
     */
    List<MargasIdentifier<MargasKey>> keys();

}
