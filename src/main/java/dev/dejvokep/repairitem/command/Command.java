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
package dev.dejvokep.repairitem.command;

import cloud.commandframework.CommandManager;
import cloud.commandframework.meta.CommandMeta;
import dev.dejvokep.repairitem.RepairItem;
import dev.dejvokep.repairitem.repair.RepairResult;
import dev.dejvokep.repairitem.repair.Repairer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Command executor for the main plugin command <code>/repair</code>.
 */
public class Command {

    /**
     * The base permission node.
     */
    public static final String PERMISSION_BASE = "repairitem";

    private final Set<String> allTarget = new HashSet<>();
    private final RepairItem plugin;

    /**
     * Initializes the command with the given main class.
     *
     * @param plugin the main plugin class
     */
    public Command(@NotNull RepairItem plugin, @NotNull CommandManager<CommandSender> manager) {
        //Set the plugin
        this.plugin = plugin;

        for (CommandFunction function : CommandFunction.values()) {
            List<String> literals = plugin.getConfiguration().getStringList("command.function." + function.getPath());
            if (literals.isEmpty())
                return;

            manager.command(manager.commandBuilder("repair")
                    .literal(literals.get(0), literals.size() == 1 ? new String[0] : literals.subList(1, literals.size()).toArray(new String[literals.size() - 1]))
                    .permission(PERMISSION_BASE + "." + function.getPermission() + ".self")
                    .meta(CommandMeta.DESCRIPTION, function.getDescription())
                    .handler(context -> {

                    }).build());
        }
    }

    /**
     * Reloads the function bindings.
     */
    public void reload() {
        allTarget.clear();
        allTarget.addAll(plugin.getConfiguration().getStringList("command.target.all"));
    }

    public Set<String> getAllTarget() {
        return allTarget;
    }
}