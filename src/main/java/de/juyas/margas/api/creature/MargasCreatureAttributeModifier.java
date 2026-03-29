package de.juyas.margas.api.creature;

import de.juyas.margas.api.MargasElement;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;

/**
 * Interface MargasCreatureAttributeModifier represents an attribute modifier configured for a creature.
 */
public interface MargasCreatureAttributeModifier extends MargasElement<MargasCreatureAttributeModifier> {

    /**
     * Returns the value of the attribute modifier.
     *
     * @return the value of the attribute modifier
     */
    Number value();

    /**
     * Returns the attribute type of the attribute modifier.
     *
     * @return the attribute type of the attribute modifier
     */
    Attribute attribute();

    /**
     * Returns if the attribute modifier is persistent.
     *
     * @return true if the attribute modifier is persistent, false otherwise
     */
    boolean persistent();

    /**
     * Returns the operation of the attribute modifier.
     *
     * @return the operation of the attribute modifier
     */
    AttributeModifier.Operation operation();

    /**
     * Returns the slot group of the attribute modifier.
     *
     * @return the slot group of the attribute modifier
     */
    EquipmentSlotGroup slotGroup();

}
