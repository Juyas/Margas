package de.juyas.margas.config.parser.margas;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.MargasIdentifier;
import de.juyas.margas.api.MargasType;
import de.juyas.margas.api.config.ConfigSectionReader;
import de.juyas.margas.api.config.ValueProvider;
import de.juyas.margas.api.creature.MargasCreatureAttributeModifier;
import de.juyas.margas.config.DefaultValueProvider;
import de.juyas.margas.config.parser.EnumReader;
import de.juyas.margas.config.parser.NumberReader;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.EquipmentSlotGroup;

/**
 * Class MargasCreatureAttributeModifierReader to read a creature attribute modifier from a configuration.
 */
public class MargasCreatureAttributeModifierReader implements ConfigSectionReader<MargasCreatureAttributeModifier> {

    /**
     * The name of the field containing the value of the attribute modifier.
     */
    private static final String FIELD_VALUE = "value";

    /**
     * The name of the field containing the operation of the attribute modifier.
     */
    private static final String FIELD_OPERATION = "operation";

    /**
     * The name of the field containing the attribute type of the attribute modifier.
     */
    private static final String FIELD_ATTRIBUTE = "attribute";

    /**
     * The name of the field containing the persistent status of the attribute modifier.
     */
    private static final String FIELD_PERSISTENT = "persistent";

    /**
     * The name of the field containing the equipment slot group of the attribute modifier.
     */
    private static final String FIELD_SLOT = "slot";

    /**
     * Creates a new instance of MargasCreatureAttributeModifierReader.
     */
    public MargasCreatureAttributeModifierReader() {
        super();
    }

    @Override
    public ValueProvider<MargasCreatureAttributeModifier> read(final ConfigurationSection section, final String path) throws MargasException {
        if (!section.isConfigurationSection(path)) {
            throw new MargasException("Invalid creature attribute modifier definition in section '%s' at path '%s'.".formatted(section.getCurrentPath(), path));
        }
        final ConfigurationSection modifierSection = section.getConfigurationSection(path);
        if (modifierSection == null) {
            throw new MargasException("Missing creature attribute modifier definition in section '%s' at path '%s'.".formatted(section.getCurrentPath(), path));
        }
        if (!modifierSection.contains(FIELD_ATTRIBUTE)) {
            throw new MargasException("Invalid creature attribute modifier definition in section '%s' at path '%s'. Missing attribute.".formatted(section.getCurrentPath(), path));
        }

        final NumberReader valueReader = new NumberReader();
        final EnumReader<AttributeModifier.Operation> operationReader = new EnumReader<>(AttributeModifier.Operation.class);

        final AttributeIdentifier identifier = new AttributeIdentifier(modifierSection.getName());
        final ValueProvider<Number> value = valueReader.read(modifierSection, FIELD_VALUE);
        final AttributeModifier.Operation operation = operationReader.read(modifierSection, FIELD_OPERATION);
        final boolean persistent = modifierSection.getBoolean(FIELD_PERSISTENT, false);

        final EquipmentSlotGroup slotGroup = getEquipmentSlotGroup(section, path, modifierSection);
        final Attribute attribute = getAttribute(section, path, modifierSection);

        return new DefaultValueProvider<>(new DefaultCreatureAttributeModifier(identifier, value.defaultValue(), attribute, operation, persistent, slotGroup),
                () -> new DefaultCreatureAttributeModifier(identifier, value.generate(), attribute, operation, persistent, slotGroup), value.isStatic());
    }

    private EquipmentSlotGroup getEquipmentSlotGroup(final ConfigurationSection section, final String path, final ConfigurationSection modifierSection) throws MargasException {
        final String slotValue = modifierSection.getString(FIELD_SLOT);
        //noinspection UnstableApiUsage
        final EquipmentSlotGroup slotGroup = slotValue == null ? EquipmentSlotGroup.ANY : EquipmentSlotGroup.getByName(slotValue);
        if (slotGroup == null) {
            throw new MargasException("Invalid creature attribute modifier definition in section '%s' at path '%s'. Invalid slot value '%s'.".formatted(section.getCurrentPath(), path, slotValue));
        }
        return slotGroup;
    }

    private Attribute getAttribute(final ConfigurationSection section, final String path, final ConfigurationSection modifierSection) throws MargasException {
        final String attributeValue = modifierSection.getString(FIELD_ATTRIBUTE);
        if (attributeValue == null) {
            throw new MargasException("Invalid creature attribute modifier definition in section '%s' at path '%s'. Missing attribute value.".formatted(section.getCurrentPath(), path));
        }
        final NamespacedKey namespacedKey = NamespacedKey.fromString(attributeValue);
        if (namespacedKey == null) {
            throw new MargasException("Invalid creature attribute modifier definition in section '%s' at path '%s'. Invalid attribute value '%s'.".formatted(section.getCurrentPath(), path, attributeValue));
        }
        final Attribute attribute = Registry.ATTRIBUTE.get(namespacedKey);
        if (attribute == null) {
            throw new MargasException("Invalid creature attribute modifier definition in section '%s' at path '%s'. Unknown attribute '%s'.".formatted(section.getCurrentPath(), path, attributeValue));
        }
        return attribute;
    }

    private record DefaultCreatureAttributeModifier(MargasIdentifier identifier, Number value, Attribute attribute,
                                                    AttributeModifier.Operation operation, boolean persistent,
                                                    EquipmentSlotGroup slotGroup) implements MargasCreatureAttributeModifier {

    }

    private record AttributeIdentifier(String name) implements MargasIdentifier {

        @Override
        public MargasType type() {
            return MargasType.CREATURE_ATTRIBUTE;
        }
    }
}
