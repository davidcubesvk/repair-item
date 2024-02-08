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

import cloud.commandframework.CommandManager;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import dev.dejvokep.repairitem.command.Command;
import dev.dejvokep.repairitem.repair.Repairer;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;

/**
 * The main plugin class.
 */
public class RepairItem extends JavaPlugin {

    private YamlDocument config;
    private Repairer repairer;
    private Command command;
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

        //Initialize the repairer
        repairer = new Repairer(this);
        //Load
        repairer.reload();

        // Commands
        try {
            CommandManager<CommandSender> commandManager = new BukkitCommandManager<>(this, CommandExecutionCoordinator.simpleCoordinator(), Function.identity(), Function.identity());
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, "An unexpected error occurred whilst registering commands!", ex);
        }

        //Initialize metrics
        new Metrics(this, 9131);
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
    public Repairer getRepairer() {
        return repairer;
    }

    public Command getCommand() {
        return command;
    }

    public Messenger getMessenger() {
        return messenger;
    }
}