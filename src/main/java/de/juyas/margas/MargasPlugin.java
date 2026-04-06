package de.juyas.margas;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.internal.ConfigurationLoader;
import de.juyas.margas.internal.MargasElementManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * The main class of the plugin.
 */
public class MargasPlugin extends JavaPlugin {

    /**
     * The manager of all elements.
     */
    private final MargasElementManager elementManager;

    /**
     * Creates a new instance of the plugin.
     */
    public MargasPlugin() {
        super();
        this.elementManager = new MargasElementManager();
    }

    @Override
    public void onEnable() {
        loadConfigurationFiles();
    }

    @Override
    public void onDisable() {
        // Empty
    }

    private void loadConfigurationFiles() {
        final File dataFolder = getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            getLogger().severe("Could not create data folder for Margas plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        final Map<File, ConfigurationSection> sections = new HashMap<>();
        ConfigurationLoader.load(dataFolder, sections);
        for (final Map.Entry<File, ConfigurationSection> sectionEntry : sections.entrySet()) {
            try {
                elementManager.loadConfiguration(sectionEntry.getValue());
            } catch (final MargasException e) {
                getLogger().log(Level.SEVERE, "Error while loading file '%s'.%n => %s".formatted(sectionEntry.getKey(), e.getMessage()), e);
            }
        }
        elementManager.printInfo(getLogger());
    }
}
