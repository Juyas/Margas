package de.juyas.margas.config.parser;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.config.ValueProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NumberReaderTest extends AbstractReaderFixture {

    private static Stream<Arguments> valid() throws IOException {
        return readFromFile("config", "numbers.yml", "numbers");
    }

    private static Stream<Arguments> invalid() throws IOException {
        return readFromFile("config", "bad_numbers.yml", "numbers");
    }

    private static Stream<Arguments> invalidRanges() throws IOException {
        return readFromFile("config", "bad_numbers.yml", "number-ranges");
    }

    @ParameterizedTest
    @MethodSource("valid")
    void valid_numbers_should_parse_correctly(final ConfigurationSection section, final String path) {
        final NumberReader reader = new NumberReader();
        final ValueProvider<Number> valueProvider = assertDoesNotThrow(() -> reader.read(section, path));
        assertDoesNotThrow(valueProvider::defaultValue);
    }

    @ParameterizedTest
    @MethodSource("invalid")
    void invalid_numbers_should_fail_to_parse(final ConfigurationSection section, final String path) {
        final NumberReader reader = new NumberReader();
        assertThrows(MargasException.class, () -> reader.read(section, path));
    }

    @ParameterizedTest
    @MethodSource("invalidRanges")
    void invalid_number_ranges_should_fail_to_parse(final ConfigurationSection section, final String path) {
        final NumberReader reader = new NumberReader(1, 10);
        assertThrows(MargasException.class, () -> reader.read(section, path));
    }
}
