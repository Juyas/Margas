package de.juyas.margas.api.creature;

import de.juyas.margas.api.MargasElement;
import org.bukkit.potion.PotionEffect;

import java.util.List;

/**
 * Interface MargasCreatureEffect represents an effect configured for a creature.
 */
public interface MargasCreatureEffect extends MargasElement {

    /**
     * Returns the list of effects to be applied.
     *
     * @return the list of effects to be applied
     */
    List<PotionEffect> effects();

    /**
     * Returns the cause of the effects to be applied.
     *
     * @return the cause of the effects to be applied
     */
    PotionEffectApplicationCause cause();

    /**
     * Returns the target of the effects to be applied.
     *
     * @return the target of the effects to be applied
     */
    PotionEffectApplicationTarget target();

    /**
     * Returns the range in which the effects should be applied.
     * <p>
     * Only used if the target requires a range to be defined.
     *
     * @return the effect range
     */
    Number range();

}
