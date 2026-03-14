package de.juyas.margas.config.parser;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.api.config.ValueProvider;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NumberReaderTest {

    private static Stream<Arguments> valid() throws IOException {
        final Configuration validConfiguration = YamlConfiguration.loadConfiguration(Files.newBufferedReader(new File("src/test/resources/config/numbers.yml").toPath()));
        final ConfigurationSection numbersSection = validConfiguration.getConfigurationSection("numbers");
        if (numbersSection == null) {
            throw new IllegalStateException("numbers section is null");
        }
        final Set<String> keys = numbersSection.getKeys(false);
        return keys.stream().map(key -> Arguments.of(numbersSection, key));
    }

    private static Stream<Arguments> invalid() throws IOException {
        final Configuration invalidConfiguration = YamlConfiguration.loadConfiguration(Files.newBufferedReader(new File("src/test/resources/config/bad_numbers.yml").toPath()));
        final ConfigurationSection numbersSection = invalidConfiguration.getConfigurationSection("numbers");
        if (numbersSection == null) {
            throw new IllegalStateException("numbers section is null");
        }
        final Set<String> keys = numbersSection.getKeys(false);
        return keys.stream().map(key -> Arguments.of(numbersSection, key));
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
}
