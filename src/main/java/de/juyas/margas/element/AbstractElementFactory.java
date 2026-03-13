package de.juyas.margas.element;

import de.juyas.margas.api.MargasElement;
import de.juyas.margas.api.MargasElementFactory;
import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.MargasType;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Class AbstractElementFactory is a default implementation of {@link MargasElementFactory} offering template methods.
 *
 * @param <T> the type of the element to parse
 */
public abstract class AbstractElementFactory<T extends MargasElement> implements MargasElementFactory<T> {

    private final MargasType type;

    public AbstractElementFactory(final MargasType type) {
        this.type = type;
    }

    protected void validateType(final ConfigurationSection section) throws MargasException {
        if (!section.getName().equals(type.getSectionName())) {
            throw new MargasException("Invalid type '%s' for section: %s".formatted(type.getSectionName(), section.getName()));
        }
    }

    protected void require(final ConfigurationSection section, final String... paths) throws MargasException {
        for (final String path : paths) {
            if (!section.contains(path)) {
                throw new MargasException("Missing required entry for type '%s': '%s'".formatted(type, path));
            }
        }
    }

}
