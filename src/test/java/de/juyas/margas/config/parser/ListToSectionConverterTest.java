package de.juyas.margas.config.parser;

import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ListToSectionConverterTest extends AbstractReaderFixture {

    private static Stream<Arguments> subSections() throws IOException {
        return readFromFile("config", "sections.yml", "sections");
    }

    @ParameterizedTest
    @MethodSource("subSections")
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void convert_subsections_correctly(final ConfigurationSection section, final String path) {
        final ConfigurationSection converted = assertDoesNotThrow(() -> ListToSectionConverter.convert(section, path));
        assertTrue(converted.isConfigurationSection("test1"), "test1 should be a configuration section");
        assertTrue(converted.isConfigurationSection("test1.elem0"), "test1.elem0 should be a configuration section");
        assertTrue(converted.isConfigurationSection("test1.elem1"), "test1.elem1 should be a configuration section");
        assertTrue(converted.isConfigurationSection("test1.elem2"), "test1.elem2 should be a configuration section");
        assertTrue(converted.isConfigurationSection("test1.elem3"), "test1.elem3 should be a configuration section");
        assertTrue(converted.isConfigurationSection("test1.elem0.subsection1"), "test1.elem0.subsection1 should be a configuration section");
        assertTrue(converted.isConfigurationSection("test1.elem1.subsection2"), "test1.elem1.subsection2 should be a configuration section");
        assertTrue(converted.isConfigurationSection("test1.elem2.subsection3"), "test1.elem2.subsection3 should be a configuration section");
        assertEquals(1, converted.getInt("test1.elem0.subsection1.value1"), "test1.elem0.subsection1.value1 should be 1");
        assertEquals(1, converted.getInt("test1.elem1.subsection2.value1"), "test1.elem1.subsection2.value1 should be 1");
        assertEquals(2, converted.getInt("test1.elem0.subsection1.value2"), "test1.elem0.subsection1.value2 should be 2");
        assertEquals(2, converted.getInt("test1.elem1.subsection2.value2"), "test1.elem1.subsection2.value2 should be 2");
        assertEquals(1, converted.getInt("test1.elem2.subsection3.depth.deeper.value"), "test1.elem2.subsection3.depth.deeper.value should be 1");
        assertEquals(4, converted.getInt("test1.elem3.subsection4"), "test1.elem3.subsection4 should be 4");
        assertEquals(5, converted.getInt("test1.elem3.subsection5"), "test1.elem3.subsection5 should be 5");
    }

}
