package de.juyas.margas.config.parser;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.config.ConfigSectionReader;
import de.juyas.margas.api.config.TextValue;
import de.juyas.margas.api.config.ValueGenerator;
import de.juyas.margas.api.config.ValueProvider;
import de.juyas.margas.config.DefaultValueProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.function.Supplier;

/**
 * Class TextParser to parse text values.
 */
public class TextReader implements ConfigSectionReader<TextValue> {

    /**
     * Pattern to check if a string is a legacy color code with the section symbol.
     */
    private static final String LEGACY_SECTION_PATTERN = "^(.|\n)*§[0-9a-fk-orA-FK-OR](.|\n)*$";

    /**
     * Pattern to check if a string is a legacy color code with the ampersand symbol.
     */
    private static final String LEGACY_AMPERSAND_PATTERN = "^(.|\n)*&[0-9a-fk-orA-FK-OR](.|\n)*$";

    /**
     * Creates a new instance of TextParser.
     */
    public TextReader() {
        super();
    }

    @Override
    public ValueProvider<TextValue> read(final ConfigurationSection section, final String path) throws MargasException {
        if (section.isString(path)) {
            return parseDirectly(section, path);
        }
        if (section.isList(path)) {
            return parseList(section, path);
        }
        throw new MargasException("Invalid text definition at path '%s'.".formatted(path));
    }

    private ValueProvider<TextValue> parseDirectly(final ConfigurationSection section, final String path) throws MargasException {
        final String text = section.getString(path);
        if (text == null) {
            throw new MargasException("Text definition not found at path '%s' in section '%s'.".formatted(path, section.getCurrentPath()));
        }
        if (text.matches(LEGACY_AMPERSAND_PATTERN)) {
            return create(text, () -> LegacyComponentSerializer.legacyAmpersand().deserialize(text));
        }
        if (text.matches(LEGACY_SECTION_PATTERN)) {
            return create(text, () -> LegacyComponentSerializer.legacySection().deserialize(text));
        }
        return create(text, () -> MiniMessage.miniMessage().deserialize(text));
    }

    private ValueProvider<TextValue> parseList(final ConfigurationSection section, final String path) throws MargasException {
        final List<String> list = section.getStringList(path);
        if (list.isEmpty()) {
            throw new MargasException("Text definition does not contain elements at path '%s' in section '%s'.".formatted(path, section.getCurrentPath()));
        }
        final Supplier<List<Component>> parser = () -> list.stream().map(input -> {
            if (input.matches(LEGACY_AMPERSAND_PATTERN)) {
                return LegacyComponentSerializer.legacyAmpersand().deserialize(input);
            }
            if (input.matches(LEGACY_SECTION_PATTERN)) {
                return LegacyComponentSerializer.legacySection().deserialize(input);
            }
            return MiniMessage.miniMessage().deserialize(input);
        }).toList();
        return create(list, parser);
    }

    private ValueProvider<TextValue> create(final List<String> rawText, final Supplier<List<Component>> parser) throws MargasException {
        final ValueGenerator<TextValue> generator = () -> {
            final List<Component> preParsed = parser.get();
            final String raw = String.join("\n", rawText);
            final Component text = Component.textOfChildren(preParsed.toArray(ComponentLike[]::new));
            return new DefaultTextValue(raw, rawText, text, preParsed);
        };
        return new DefaultValueProvider<>(generator.generate(), generator, false);
    }

    private ValueProvider<TextValue> create(final String rawText, final Supplier<Component> parser) throws MargasException {
        final ValueGenerator<TextValue> generator = () -> {
            final Component parsed = parser.get();
            return new DefaultTextValue(rawText, List.of(rawText), parsed, List.of(parsed));
        };
        return new DefaultValueProvider<>(generator.generate(), generator, false);
    }

    /**
     * Default implementation of TextValue.
     *
     * @param rawText  the raw text
     * @param rawLines the raw lines
     * @param text     the parsed text
     * @param lines    the parsed lines
     */
    private record DefaultTextValue(String rawText, List<String> rawLines, Component text,
                                    List<Component> lines) implements TextValue {

    }

}
