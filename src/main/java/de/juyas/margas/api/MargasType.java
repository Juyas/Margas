package de.juyas.margas.api;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Enum MargasType to represent all types of elements loaded from configuration.
 */
public enum MargasType {

    /**
     * This type represents an entity that supposed to be spawned in margas areas.
     */
    CREATURE("mob", "mobs"),

    /**
     * This type represents an effect that supposed to be applied to or by margas creatures.
     */
    CREATURE_EFFECT("effect", "effects"),

    /**
     * This type represents an attribute that supposed to modify the attributes of margas creatures.
     */
    CREATURE_ATTRIBUTE("attribute", "attributes"),

    /**
     * This type represents an item that supposed to be used in margas areas as loot or equipment.
     */
    ITEM("item", "items"),

    /**
     * This type represents a margas area.
     */
    AREA("area", "areas"),

    /**
     * This type represents a chest that be placed in margas areas and populated with items for players to loot.
     */
    CHEST("chest", "chests"),

    /**
     * This type represents a key that be treated as loot as well as a key to open loot chests.
     */
    CHEST_KEY("key", "chest-keys"),

    /**
     * This type represents a loot table that be used to randomly generate items and manipulate the chances.
     */
    LOOT_TABLE("table", "loot-tables");

    /**
     * A lookup map for {@link MargasType} by name.
     */
    private static final Map<String, MargasType> BY_NAME = createLookupMap(MargasType::getName);

    /**
     * A lookup map for {@link MargasType} by section name.
     */
    private static final Map<String, MargasType> BY_SECTION_NAME = createLookupMap(MargasType::getSectionName);

    /**
     * The name of the type used for identifiers.
     */
    private final String name;

    /**
     * The section name of the type used for configurations.
     */
    private final String sectionName;

    /**
     * Creates a new instance of the enum.
     *
     * @param name        the name of the type
     * @param sectionName the section name of the type
     */
    MargasType(final String name, final String sectionName) {
        this.name = name;
        this.sectionName = sectionName;
    }

    /**
     * Returns the type by the given name.
     *
     * @param name the name of the type to get
     * @return the type or null if not found
     */
    public static MargasType getByName(final String name) {
        return BY_NAME.get(name.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns the type by the given section name.
     *
     * @param sectionName the section name
     * @return the type or null if not found
     */
    public static MargasType getBySectionName(final String sectionName) {
        return BY_SECTION_NAME.get(sectionName.toLowerCase(Locale.ROOT));
    }

    /**
     * Creates a lookup map for the given key extractor.
     * Used only internally.
     *
     * @param keyExtractor the key extractor function
     * @return the lookup map
     */
    private static Map<String, MargasType> createLookupMap(final Function<MargasType, String> keyExtractor) {
        return Arrays.stream(values())
                .collect(Collectors.toUnmodifiableMap(
                        type -> keyExtractor.apply(type).toLowerCase(Locale.ROOT),
                        Function.identity()
                ));
    }

    /**
     * Returns the name of the type.
     *
     * @return the name of the type
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the section name of the type.
     *
     * @return the section name of the type
     */
    public String getSectionName() {
        return sectionName;
    }

}
