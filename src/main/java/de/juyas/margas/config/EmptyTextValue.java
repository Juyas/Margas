package de.juyas.margas.config;

import de.juyas.margas.api.config.TextValue;
import net.kyori.adventure.text.Component;

import java.util.List;

/**
 * Class EmptyTextValue to provide an empty text value instance.
 */
public class EmptyTextValue implements TextValue {

    /**
     * Creates a new instance of EmptyTextValue.
     */
    public EmptyTextValue() {
        super();
    }

    @Override
    public String rawText() {
        return "";
    }

    @Override
    public List<String> rawLines() {
        return List.of();
    }

    @Override
    public Component text() {
        return Component.empty();
    }

    @Override
    public List<Component> lines() {
        return List.of();
    }
}
