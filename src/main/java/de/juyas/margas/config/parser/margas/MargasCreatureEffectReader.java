package de.juyas.margas.config.parser.margas;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.MargasIdentifier;
import de.juyas.margas.api.MargasType;
import de.juyas.margas.api.config.ConfigSectionReader;
import de.juyas.margas.api.config.ValueProvider;
import de.juyas.margas.api.creature.MargasCreatureEffect;
import de.juyas.margas.api.creature.PotionEffectApplicationCause;
import de.juyas.margas.api.creature.PotionEffectApplicationTarget;
import de.juyas.margas.config.DefaultValueProvider;
import de.juyas.margas.config.parser.EnumReader;
import de.juyas.margas.config.parser.NumberReader;
import de.juyas.margas.config.parser.SectionListReader;
import de.juyas.margas.config.parser.bukkit.PotionEffectReader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;

import java.util.List;

/**
 * Class MargasCreatureEffectReader reads creature effects from a configuration.
 */
public class MargasCreatureEffectReader implements ConfigSectionReader<MargasCreatureEffect> {

    /**
     * The name of the field containing the effects of the creature effect.
     */
    private static final String FIELD_EFFECTS = "effects";

    /**
     * The name of the field containing the trigger of the creature effect.
     */
    private static final String FIELD_TRIGGER = "trigger";

    /**
     * The name of the field containing the target of the creature effect.
     */
    private static final String FIELD_TARGET = "target";

    /**
     * The name of the field containing the range of the creature effect.
     */
    private static final String FIELD_RANGE = "range";

    /**
     * Creates a new instance of MargasCreatureEffectReader.
     */
    public MargasCreatureEffectReader() {
        super();
    }

    @Override
    public ValueProvider<MargasCreatureEffect> read(final ConfigurationSection section, final String path) throws MargasException {
        if (!section.isConfigurationSection(path)) {
            throw new MargasException("Invalid creature effect definition in section '%s' at path '%s'.".formatted(section.getCurrentPath(), path));
        }
        final ConfigurationSection effectSection = section.getConfigurationSection(path);
        if (effectSection == null) {
            throw new MargasException("Missing creature effect definition in section '%s' at path '%s'.".formatted(section.getCurrentPath(), path));
        }

        final ConfigSectionReader<List<PotionEffect>> potionEffectListReader = new SectionListReader<>(new PotionEffectReader());
        final EnumReader<PotionEffectApplicationCause> triggerReader = new EnumReader<>(PotionEffectApplicationCause.class);
        final EnumReader<PotionEffectApplicationTarget> targetReader = new EnumReader<>(PotionEffectApplicationTarget.class);
        final NumberReader rangeReader = new NumberReader(1, 100);

        final EffectIdentifier identifier = new EffectIdentifier(effectSection.getName());
        final ValueProvider<List<PotionEffect>> effectsList = potionEffectListReader.read(effectSection, FIELD_EFFECTS);
        final PotionEffectApplicationCause trigger = triggerReader.read(effectSection, FIELD_TRIGGER);
        final PotionEffectApplicationTarget target = targetReader.read(effectSection, FIELD_TARGET);
        final ValueProvider<Number> range = effectSection.contains(FIELD_RANGE) ? rangeReader.read(effectSection, FIELD_RANGE) : new DefaultValueProvider<>(0);

        return new DefaultValueProvider<>(useDefault -> new DefaultCreatureEffect(identifier, effectsList.generate(useDefault), trigger, target, range.generate(useDefault)), false);
    }

    private record DefaultCreatureEffect(EffectIdentifier identifier, List<PotionEffect> effects,
                                         PotionEffectApplicationCause cause, PotionEffectApplicationTarget target,
                                         Number range) implements MargasCreatureEffect {

    }

    private record EffectIdentifier(String name) implements MargasIdentifier {

        @Override
        public MargasType type() {
            return MargasType.CREATURE_EFFECT;
        }
    }
}
