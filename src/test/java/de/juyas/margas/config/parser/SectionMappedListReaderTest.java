package de.juyas.margas.config.parser;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.config.ValueProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SectionMappedListReaderTest extends AbstractReaderFixture {

    private static Stream<Arguments> valid() throws IOException {
        return readFromFile("config", "section_lists.yml", "sections");
    }

    private static Stream<Arguments> invalid() throws IOException {
        return readFromFile("config", "bad_section_lists.yml", "sections");
    }

    @ParameterizedTest
    @MethodSource("valid")
    void valid_map_section_lists_should_parse_correctly(final ConfigurationSection section, final String path) {
        final SectionMappedListReader<Number> reader = new SectionMappedListReader<>(new NumberReader());
        final ValueProvider<List<Map.Entry<String, Number>>> valueProvider = assertDoesNotThrow(() -> reader.read(section, path));
        assertDoesNotThrow(valueProvider::defaultValue);
    }

    @ParameterizedTest
    @MethodSource("invalid")
    void invalid_map_section_lists_should_fail_to_parse(final ConfigurationSection section, final String path) {
        final SectionMappedListReader<Number> reader = new SectionMappedListReader<>(new NumberReader());
        assertThrows(MargasException.class, () -> reader.read(section, path));
    }

}
