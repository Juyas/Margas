package de.juyas.margas.api.creature;

/**
 * Enum PotionEffectApplicationTarget to represent the target of a potion effect application.
 */
public enum PotionEffectApplicationTarget {

    /**
     * The potion effect is applied to the creature itself.
     */
    SELF,

    /**
     * The potion effect is applied to the creature's target.
     */
    TARGET,

    /**
     * The potion effect is applied to any player in the creature's range.
     */
    PLAYERS_IN_RANGE,

    /**
     * The potion effect is applied to anything in the creature's range.
     */
    RANGE

}
