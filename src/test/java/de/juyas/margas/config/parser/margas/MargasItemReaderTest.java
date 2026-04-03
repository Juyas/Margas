package de.juyas.margas.config.parser.margas;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.config.ValueProvider;
import de.juyas.margas.api.loot.MargasItem;
import de.juyas.margas.config.parser.AbstractReaderFixture;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MargasItemReaderTest extends AbstractReaderFixture {

    private static Stream<Arguments> valid() throws IOException {
        return readFromFile("config/margas", "margas_items.yml", "items");
    }

    private static Stream<Arguments> invalid() throws IOException {
        return readFromFile("config/margas", "bad_margas_items.yml", "items");
    }

    @ParameterizedTest
    @MethodSource("valid")
    void valid_items_should_parse_correctly(final ConfigurationSection section, final String path) {
        final MargasItemReader reader = new MargasItemReader();
        final ValueProvider<MargasItem> valueProvider = assertDoesNotThrow(() -> reader.read(section, path));
        assertDoesNotThrow(valueProvider::defaultValue);
    }

    @ParameterizedTest
    @MethodSource("invalid")
    void invalid_items_should_fail_to_parse(final ConfigurationSection section, final String path) {
        final MargasItemReader reader = new MargasItemReader();
        assertThrows(MargasException.class, () -> reader.read(section, path));
    }

}
