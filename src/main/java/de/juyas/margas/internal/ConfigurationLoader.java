package de.juyas.margas.internal;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * Class ConfigurationLoader to load all configuration files from a folder and all subfolders.
 */
public final class ConfigurationLoader {

    /**
     * Is not supposed to be instantiated.
     */
    private ConfigurationLoader() {
    }

    /**
     * Loads all configuration files from the given folder and all subfolders into the given list.
     *
     * @param sourceFolder the folder to load the configuration files from
     * @param sections     the list to add the loaded configuration sections to
     */
    public static void load(final File sourceFolder, final List<ConfigurationSection> sections) {
        final File[] files = sourceFolder.listFiles(file -> file.getName().toLowerCase(Locale.ROOT).endsWith(".yml"));
        if (files != null) {
            for (final File file : files) {
                final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                sections.add(configuration);
            }
        }
        final File[] folders = sourceFolder.listFiles(File::isDirectory);
        if (folders != null) {
            for (final File folder : folders) {
                load(folder, sections);
            }
        }
    }
}
