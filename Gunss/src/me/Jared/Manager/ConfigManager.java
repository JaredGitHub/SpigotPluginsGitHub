/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 */
package me.Jared.Manager;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.Jared.GunsPlugin;

public class ConfigManager {
    private final File file;
    private FileConfiguration fileConfiguration;

    public ConfigManager(GunsPlugin plugin, String name) {
        this.file = new File(plugin.getDataFolder(), String.valueOf(name) + ".yml");
    }

    public void init() {
        this.fileConfiguration = YamlConfiguration.loadConfiguration((File)this.file);
        try {
            if (!this.file.exists()) {
                this.file.createNewFile();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
        try {
            this.fileConfiguration.save(this.file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfiguration() {
        return this.fileConfiguration;
    }
}

