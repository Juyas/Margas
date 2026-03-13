package de.juyas.margas.element.creature;

import de.juyas.margas.api.MargasElementFactory;
import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.MargasType;
import de.juyas.margas.api.creature.MargasCreature;
import de.juyas.margas.element.AbstractElementFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

/**
 * Class MargasCreatureFactory is a default implementation of {@link MargasElementFactory} for {@link MargasCreature}.
 */
public class MargasCreatureFactory extends AbstractElementFactory<MargasCreature> {

    private static final String FIELD_TYPE = "type";

    private static final String FIELD_ATTRIBUTES = "attributes";

    private static final String FIELD_EFFECTS = "effects";

    private static final String FIELD_EQUIPMENT = "equipment";

    private static final String FIELD_LOOT_TABLE = "loot-table";

    public MargasCreatureFactory(final MargasType type) {
        super(type);
    }

    @Override
    public MargasCreature parseElement(final ConfigurationSection section) throws MargasException {
        validateType(section);
        require(section, FIELD_TYPE);
        return null;
    }

    private record LoadedCreature(MargasType type, String name, EntityType entityType) {

    }


}

