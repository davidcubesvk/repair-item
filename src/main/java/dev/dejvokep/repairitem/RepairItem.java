/*
 * Copyright 2024 https://dejvokep.dev/
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

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import dev.dejvokep.repairitem.command.CommandRegistrar;
import dev.dejvokep.repairitem.repair.Repairer;
import dev.dejvokep.repairitem.utils.Messenger;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

/**
 * The main plugin class.
 */
public class RepairItem extends JavaPlugin {

    private YamlDocument config;
    private Repairer repairer;
    private CommandRegistrar commandRegistrar;
    private Messenger messenger;

    @Override
    public void onEnable() {
        // Thank you message
        getLogger().info("Thank you for downloading RepairItem!");

        try {
            // Create the config file
            config = YamlDocument.create(new File(getDataFolder(), "config.yml"), Objects.requireNonNull(getResource("config.yml")), LoaderSettings.builder().setAutoUpdate(true).build(), UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Failed to initialize the config file!", ex);
            return;
        }

        // Initialize
        repairer = new Repairer(this);
        messenger = new Messenger(this);

        // Commands
        try {
            commandRegistrar = new CommandRegistrar(this);
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, "An unexpected error occurred whilst registering commands!", ex);
        }

        // Metrics
        if (config.getBoolean("metrics")) {
            getLogger().info("Initializing metrics.");
            new Metrics(this, 9131);
        }
    }

    /**
     * Returns the plugin configuration.
     *
     * @return the plugin configuration
     */
    @NotNull
    public YamlDocument getConfiguration() {
        return config;
    }

    /**
     * Returns the repairer.
     *
     * @return the repairer
     */
    @NotNull
    public Repairer getRepairer() {
        return repairer;
    }

    /**
     * Returns the command registrar.
     *
     * @return the command registrar
     */
    @NotNull
    public CommandRegistrar getCommandRegistrar() {
        return commandRegistrar;
    }

    /**
     * Returns the messenger.
     *
     * @return the messenger
     */
    public Messenger getMessenger() {
        return messenger;
    }
}