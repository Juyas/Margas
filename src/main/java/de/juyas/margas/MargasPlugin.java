package de.juyas.margas;

import de.juyas.margas.api.MargasException;
import de.juyas.margas.internal.ConfigurationLoader;
import de.juyas.margas.internal.MargasElementManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        try {
            loadConfigurationFiles();
        } catch (final MargasException e) {
            getLogger().severe("Could not load configuration files: " + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        // Empty
    }

    private void loadConfigurationFiles() throws MargasException {
        final File dataFolder = getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            getLogger().severe("Could not create data folder for Margas plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        final List<ConfigurationSection> sections = new ArrayList<>();
        ConfigurationLoader.load(dataFolder, sections);
        for (final ConfigurationSection section : sections) {
            elementManager.loadConfiguration(section, getLogger());
        }
    }
}
