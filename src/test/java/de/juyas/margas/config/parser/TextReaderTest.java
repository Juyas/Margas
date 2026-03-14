package de.juyas.margas.config.parser;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.config.TextValue;
import de.juyas.margas.api.config.ValueProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TextReaderTest extends AbstractReaderFixture {

    private static Stream<Arguments> valid() throws IOException {
        return readFromFile("config", "texts.yml", "texts");
    }

    private static Stream<Arguments> invalid() throws IOException {
        return readFromFile("config", "bad_texts.yml", "texts");
    }

    @ParameterizedTest
    @MethodSource("valid")
    void valid_texts_should_parse_correctly(final ConfigurationSection section, final String path) {
        final TextReader reader = new TextReader();
        final ValueProvider<TextValue> valueProvider = assertDoesNotThrow(() -> reader.read(section, path));
        assertDoesNotThrow(valueProvider::defaultValue);
    }

    @ParameterizedTest
    @MethodSource("invalid")
    void invalid_texts_should_fail_to_parse(final ConfigurationSection section, final String path) {
        final TextReader reader = new TextReader();
        assertThrows(MargasException.class, () -> reader.read(section, path));
    }

}
