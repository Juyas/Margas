package de.juyas.margas.api.config;

import net.kyori.adventure.text.Component;

import java.util.List;

/**
 * Interface TextValue to provide a text value in a configuration formatted in different ways.
 */
public interface TextValue {

    /**
     * Returns the raw text of the value.
     * Separate lines will be combined with an empty space character.
     *
     * @return the raw text
     */
    String rawText();

    /**
     * Returns the raw lines of the value.
     *
     * @return the raw lines
     */
    List<String> rawLines();

    /**
     * Returns the text of the value parsed into a component.
     *
     * @return the parsed text component
     */
    Component text();

    /**
     * Returns the lines of the value parsed into components.
     *
     * @return the parsed components
     */
    List<Component> lines();

}
