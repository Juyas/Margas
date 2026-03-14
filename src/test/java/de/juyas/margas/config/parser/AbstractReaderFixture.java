package de.juyas.margas.config.parser;

import de.juyas.margas.api.config.ConfigSectionReader;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.params.provider.Arguments;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Class AbstractReaderFixture to reduce boilerplate code in reader-related tests.
 */
abstract class AbstractReaderFixture {

    /**
     * Reads a configuration section from a test file extracting arguments for a {@link ConfigSectionReader} test.
     *
     * @param folder                    the folder of the test file
     * @param file                      the name of the test file
     * @param pathToParentSectionToList the path to the parent section inside the test file
     * @return the arguments for the parameterized test
     * @throws IOException if the test file cannot be read
     */
    protected static Stream<Arguments> readFromFile(final String folder, final String file, final String pathToParentSectionToList) throws IOException {
        final File pathToFile = new File("src/test/resources/" + folder, file);
        final Configuration validConfiguration = YamlConfiguration.loadConfiguration(Files.newBufferedReader(pathToFile.toPath()));
        final ConfigurationSection parentSection = validConfiguration.getConfigurationSection(pathToParentSectionToList);
        if (parentSection == null) {
            throw new IllegalStateException("parent section '%s' is null".formatted(pathToParentSectionToList));
        }
        final Set<String> keys = parentSection.getKeys(false);
        return keys.stream().map(key -> Arguments.of(parentSection, key));
    }

}
