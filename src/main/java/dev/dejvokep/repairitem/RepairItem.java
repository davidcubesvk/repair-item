/*
 * Copyright 2022 https://dejvokep.dev/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.dejvokep.repairitem;

import dev.dejvokep.repairitem.command.Command;
import dev.dejvokep.repairitem.repair.Repairer;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

/**
 * The main plugin class.
 */
public class RepairItem extends JavaPlugin {

    //The configuration file
    private File configFile;
    private final YamlConfiguration configuration = new YamlConfiguration();

    //Repairer
    private Repairer repairer;

    @Override
    public void onEnable() {
        //Thank you message
        getLogger().log(Level.INFO, "Thank you for downloading RepairItem!");

        //Create the folder(s)
        if (!getDataFolder().exists())
            getDataFolder().mkdirs();
        //Create an abstract instance of the file
        configFile = new File(getDataFolder(), "config.yml");
        //If the file doesn't exist
        if (!configFile.exists()) {
            try (InputStream in = getResource("config.yml")) {
                Files.copy(in, configFile.toPath());
            } catch (IOException ex) {
                //Log
                getLogger().log(Level.SEVERE, "The configuration file could not be created; please restart the server. If the problem persists, please report it.", ex);
            }
        }
        //Load
        load();

        //Initialize the repairer
        repairer = new Repairer(this);
        //Load
        repairer.reload();

        //Register the command and load
        new Command(this).reload();

        //Initialize metrics
        new Metrics(this, 9131);
    }

    /**
     * Loads the configuration file from the disk.
     */
    public void load() {
        try {
            configuration.load(configFile);
        } catch (IOException | InvalidConfigurationException ex) {
            //Log
            getLogger().log(Level.SEVERE, "The configuration file could not be loaded; please reload the plugin or restart the server. If the problem persists, please report it.", ex);
        }
    }

    /**
     * Returns the configuration file.
     *
     * @return the configuration file
     */
    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Returns the repairer.
     *
     * @return the repairer
     */
    public Repairer getRepairer() {
        return repairer;
    }
}