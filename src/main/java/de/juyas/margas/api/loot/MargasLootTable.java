package de.juyas.margas.api.loot;

import de.juyas.margas.api.MargasElement;
import de.juyas.margas.api.MargasIdentifier;
import de.juyas.margas.api.MargasType;
import de.juyas.margas.api.config.ValueProvider;
import de.juyas.margas.api.config.WeightedList;

import java.util.List;

/**
 * Interface MargasLootTable represents a loot table configured in the section for {@link MargasType#LOOT_TABLE}.
 */
public interface MargasLootTable extends MargasElement<MargasLootTable> {

    /**
     * Returns the parents of the loot table.
     *
     * @return the parents of the loot table
     */
    List<MargasIdentifier<MargasLootTable>> parents();

    /**
     * Returns the items of the loot table.
     * This includes the items of the parents as well.
     *
     * @return the items of the loot table
     */
    WeightedList<ValueProvider<MargasItem>> items();

}
