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
package dev.dejvokep.repairitem.command.wrapper;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.route.Route;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * A representation for a command sender.
 */
public class Sender {

    /**
     * Route to the replacement for the <code>{sender}</code> placeholder when the sender is the console.
     */
    private static final Route ROUTE_REPLACEMENT_CONSOLE = Route.fromString("messages.repair.target.source-placeholder.console");

    private final CommandSender sender;

    /**
     * Constructs a sender representation.
     *
     * @param sender the command sender
     */
    private Sender(@NotNull CommandSender sender) {
        this.sender = sender;
    }

    /**
     * Returns the replacement for the <code>{sender}</code> placeholder.
     *
     * @param config the config
     * @return the placeholder replacement
     */
    public String getReplacement(@NotNull YamlDocument config) {
        return sender instanceof ConsoleCommandSender ? config.getString(ROUTE_REPLACEMENT_CONSOLE) : sender.getName();
    }

    /**
     * Returns the underlying sender instance.
     *
     * @return the sender instance
     */
    @NotNull
    public CommandSender get() {
        return sender;
    }

    /**
     * Constructs a sender from the given sender instance.
     *
     * @param sender the sender
     * @return the sender
     */
    public static Sender of(@NotNull CommandSender sender) {
        return new Sender(sender);
    }

}