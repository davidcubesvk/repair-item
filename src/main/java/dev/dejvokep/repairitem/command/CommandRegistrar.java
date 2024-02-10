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
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.CommandMeta;
import dev.dejvokep.repairitem.RepairItem;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Command executor for the main plugin command <code>/repair</code>.
 */
public class CommandRegistrar {

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
     * @throws Exception thrown if failed to construct the command manager
     */
    public CommandRegistrar(@NotNull RepairItem plugin) throws Exception {
        //Set the plugin
        this.plugin = plugin;

        CommandManager<CommandSender> manager = new BukkitCommandManager<>(plugin, CommandExecutionCoordinator.simpleCoordinator(), Function.identity(), Function.identity());

        for (CommandFunction function : CommandFunction.values()) {
            List<String> literals = plugin.getConfiguration().getStringList("command.function." + function.getPath());
            if (literals.isEmpty())
                return;

            String[] rest = literals.size() == 1 ? new String[0] : literals.subList(1, literals.size()).toArray(new String[literals.size() - 1]);
            FunctionHandler handler = function.initHandler(plugin);

            manager.command(manager.commandBuilder("repair")
                    .literal(literals.get(0), rest)
                    .permission(String.format("%s.%s.self", PERMISSION_BASE, function.getPermission()))
                    .meta(CommandMeta.DESCRIPTION, function.getDescription())
                    .handler(handler::accept).build());
            manager.command(manager.commandBuilder("repair")
                    .literal(literals.get(0), rest)
                    .argument(StringArgument.of("target"))
                    .permission(String.format("%s.%s.other", PERMISSION_BASE, function.getPermission()))
                    .meta(CommandMeta.DESCRIPTION, function.getDescription())
                    .handler(handler::accept).build());
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