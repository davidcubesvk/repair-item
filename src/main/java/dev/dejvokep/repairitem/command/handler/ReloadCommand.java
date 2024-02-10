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
package dev.dejvokep.repairitem.command.handler;

import cloud.commandframework.context.CommandContext;
import dev.dejvokep.repairitem.RepairItem;
import dev.dejvokep.repairitem.command.function.FunctionHandler;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Reload function command handler.
 */
public class ReloadCommand implements FunctionHandler {

    private final RepairItem plugin;

    /**
     * Initializes the command handler.
     *
     * @param plugin the plugin instance
     */
    public ReloadCommand(@NotNull RepairItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void accept(@NotNull CommandContext<CommandSender> context) {
        try {
            plugin.getConfiguration().reload();
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred whilst reloading plugin configuration!", ex);
        }
        plugin.getCommandRegistrar().reload();
        plugin.getRepairer().reload();
        plugin.getMessenger().send(context, "reload");
    }
}